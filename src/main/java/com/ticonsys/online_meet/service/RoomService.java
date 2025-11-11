package com.ticonsys.online_meet.service;

import com.ticonsys.online_meet.dto.JoinUserDto;
import com.ticonsys.online_meet.dto.PublishStreamDto;
import com.ticonsys.online_meet.dto.RoomDto;
import io.github.rashedul.janus.client.JanusClient;
import io.github.rashedul.janus.client.JanusConfiguration;
import io.github.rashedul.janus.client.JanusSession;
import io.github.rashedul.janus.client.handle.impl.VideoRoomHandle;
import io.github.rashedul.janus.client.plugins.videoroom.events.*;
import io.github.rashedul.janus.client.plugins.videoroom.listeners.JanusVideoRoomListener;
import io.github.rashedul.janus.client.plugins.videoroom.models.CreateRoomRequest;
import io.github.rashedul.janus.client.plugins.videoroom.models.CreateRoomResponse;
import io.github.rashedul.janus.client.plugins.videoroom.models.JoinRoomRequest;
import io.github.rashedul.janus.client.plugins.videoroom.models.PublishRequest;
import io.github.rashedul.janus.utils.ServerInfo;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class RoomService {
    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    private static JanusClient client;

    @Value("${janus.url}")
    private String janusUrl;
    @Value("${janus.ws-port}")
    private int janusWsPort;
    @Value("${janus.ws-ssl}")
    private boolean janusWsSSL;
    @Value("${janus.log}")
    private boolean janusLog;


    // app-level maps
    private final Map<String, RoomDto> rooms = new ConcurrentHashMap<>(); // roomId -> room always one room create
    private final Map<String, List<JoinUserDto>> joinRooms = new ConcurrentHashMap<>();   // appRoomId -> user list
    private final Map<String, VideoRoomHandle> userHandles = new ConcurrentHashMap<>(); // clientId -> Video handle
    private final Map<String, JanusSession> userSessions = new ConcurrentHashMap<>();  // clientId -> Janus session (for cleanup)
    private final static String ROOM_NAME = "Test Room";
    private final static Integer PARTICIPANTS = 50;

    @PostConstruct
    @SneakyThrows
    public void init() {
//        JanusConfiguration config = new JanusConfiguration(
//                janusUrl,
//                janusWsPort,
//                "/janus",
//                janusWsSSL,
//                janusLog
//        );
        JanusConfiguration config = new JanusConfiguration(janusUrl);

        client = new JanusClient(config);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down JanusClient...");
            try {
                if (client != null) client.disconnect();
            } catch (Exception ignore) {
            }
        }));

        logger.info("Connecting to Janus server at {}...", config.getUri());
        client.connect().get(10, TimeUnit.SECONDS);
    }

    /**
     * Create a room and join admin as publisher.
     */
    @SneakyThrows
    public RoomDto createRoom() {
        ServerInfo info = client.getServerInfo().get();
        logger.info("Janus server: {}", info.versionString());

        JanusSession session = client.createSession().get();
        VideoRoomHandle adminHandle = session.attachToVideoRoom().get();

        // add listener to admin handle; this listener also forwards JSEP answers to clients:
        addVideoRoomListener(adminHandle);

        CreateRoomRequest createRequest = new CreateRoomRequest.Builder()
                .setDescription(ROOM_NAME)
                .setPublishers(PARTICIPANTS)
                .build();

        CreateRoomResponse createResp = adminHandle.createRoom(createRequest).get();
        long janusRoomId = createResp.room();

        RoomDto dto = new RoomDto();
        dto.setRoomId(UUID.randomUUID().toString());
        dto.setJanusRoomId(janusRoomId);
        dto.setParticipants(PARTICIPANTS);
        dto.setName(ROOM_NAME);
        rooms.clear();
        rooms.put(dto.getRoomId(), dto);

        logger.info("Created room {} (janus {})", dto.getRoomId(), janusRoomId);
        return dto;
    }

    /**
     * Join room as a new participant (creates session + handle per user).
     */
    @SneakyThrows
    public JoinUserDto joinRoom(JoinUserDto dto) {
        RoomDto roomDto = rooms.get(dto.getRoomId());
        if (roomDto == null) throw new RuntimeException("Room not found");

        List<JoinUserDto> users = joinRooms.getOrDefault(dto.getRoomId(), new ArrayList<>());
        List<String> userIds = users.stream().map(JoinUserDto::getClientId).toList();
        if (userIds.contains(dto.getClientId())) throw new RuntimeException("User already joined");
        if (roomDto.getParticipants() <= users.size()) throw new RuntimeException("Room is full");

        JanusSession session = client.createSession().get();
        VideoRoomHandle userHandle = session.attachToVideoRoom().get();

        addVideoRoomListener(userHandle);

        JoinRoomRequest joinReq = new JoinRoomRequest.Builder(roomDto.getJanusRoomId())
                .setDisplay(dto.getDisplayName())
                .build();

        userHandle.join(joinReq).get();
        users.add(dto);
        joinRooms.put(dto.getRoomId(), users);

        userHandles.put(dto.getClientId(), userHandle);
        userSessions.put(dto.getClientId(), session);

        logger.info("User {} joined room {}", dto.getClientId(), dto.getRoomId());
        return dto;
    }

    /**
     * Publish stream: send publish request with client SDP offer to Janus. Forward jsep in response (if any).
     */
    @SneakyThrows
    public void publishStream(PublishStreamDto dto) {

        RoomDto roomDto = rooms.get(dto.getRoomId());
        if (roomDto == null) throw new RuntimeException("Room not found");

        JoinUserDto userDto = joinRooms.get(dto.getRoomId()).stream()
                .filter(user -> user.getClientId().equals(dto.getClientId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not joined"));

        VideoRoomHandle handle = userHandles.get(dto.getClientId());
        if (handle == null) throw new RuntimeException("User not joined");

        PublishRequest request = new PublishRequest.Builder()
                .setDisplay(userDto.getDisplayName())
                .setVideoCodec("vp8")
                .setAudioCodec("opus")
                .build();

        JSONObject json = request.toJson();

        logger.info("Sending publish request for {}", userDto.getDisplayName());
        handle.sendMessage(json, dto.getJsep());

    }


    /**
     * Unpublish / hangup for a user
     */
    @SneakyThrows
    public void unpublish(String displayName) {
        VideoRoomHandle handle = userHandles.remove(displayName);
        JanusSession session = userSessions.remove(displayName);
        if (handle != null) {
            try {
                handle.unpublish().get();
            } catch (Exception ignored) {
            }
            try {
                handle.detach();
            } catch (Exception ignored) {
            }
        }
        if (session != null) {
            try {
                session.destroy();
            } catch (Exception ignored) {
            }
        }
        // also remove from joinRooms
        joinRooms.forEach((roomId, list) -> list.remove(displayName));
    }

    /**
     * Forward jsep to connected clients over STOMP.
     * topic: /topic/notify-received
     * payload contains: {display, jsep, roomId}
     */
    private void forwardJsepToClients(String roomId, String display, JSONObject jsep) {
        JSONObject payload = new JSONObject();
        payload.put("display", display);
        payload.put("roomId", roomId == null ? "" : roomId);
        payload.put("jsep", jsep);
        //TODO WEB SOCKET
        logger.info("Forwarded JSEP to clients for {} (room {})", display, roomId);
    }

    /**
     * Add listener to a handle. For some events, we forward info to clients as well.
     */
    private void addVideoRoomListener(VideoRoomHandle handle) {
        handle.addVideoRoomListener(new JanusVideoRoomListener() {
            @Override
            public void onJoined(JoinedEvent event) {
                logger.info("Handle joined janus room {} as id {}", event.room(), event.id());
            }

            @Override
            public void onPublisherAdded(PublisherAddedEvent event) {
                event.publishers().forEach(p ->
                        logger.info("Publisher added: {} (id {})", p.display(), p.id()));
                // inform clients about new publishers (display+id)
                JSONObject payload = new JSONObject();
                payload.put("event", "publisher-added");
                payload.put("publishers", event.publishers().stream()
                        .map(p -> {
                            JSONObject o = new JSONObject();
                            o.put("display", p.display());
                            o.put("id", p.id());
                            return o.toString();
                        }).toList());
                System.err.println(payload);
                //TODO WEB SOCKET
            }

            @Override
            public void onUnpublished(UnpublishedEvent event) {
                logger.info("Unpublished: {}", event.unpublished());
                JSONObject payload = new JSONObject();
                payload.put("event", "unpublished");
                payload.put("unpublished", event.unpublished());
                //TODO WEB SOCKET
            }

            @Override
            public void onParticipantLeft(ParticipantLeftEvent event) {
                logger.info("Participant left: {}", event.leaving());
                JSONObject payload = new JSONObject();
                payload.put("event", "participant-left");
                payload.put("leaving", event.leaving());
                //TODO WEB SOCKET
            }

            @Override
            public void onRoomDestroyed(RoomDestroyedEvent event) {
                logger.info("Room destroyed: {}", event.room());
                JSONObject payload = new JSONObject();
                payload.put("event", "room-destroyed");
                payload.put("room", event.room());
                //TODO WEB SOCKET
            }

            @Override
            public void onSubscriberAttached(AttachedEvent event) {
                logger.info("onSubscriberAttached Event: {}", event);
            }

            @Override
            public void onEvent(JSONObject event) {
                logger.info("Room Event: {}", event);

            }

            @Override
            public void onTalking(TalkingEvent event) {
                logger.info("onTalking Event: {}", event);
            }

            @Override
            public void onStoppedTalking(StoppedTalkingEvent event) {
                logger.info("StoppedTalkingEvent Event: {}", event);
            }

            @Override
            public void onSubscriptionUpdated(UpdatedEvent event) {
                logger.info("UpdatedEvent Event: {}", event);
            }

            @Override
            public void onSwitched(SwitchedEvent event) {
                logger.info("SwitchedEvent Event: {}", event);
            }

        });
    }

    @PreDestroy
    public void shutdown() {
        try {
            if (client != null) client.disconnect();
        } catch (Exception e) {
            logger.warn("Error closing Janus client", e);
        }
    }
}
