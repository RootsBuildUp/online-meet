package com.ticonsys.online_meet.service;

import com.ticonsys.online_meet.dto.JoinUserDto;
import com.ticonsys.online_meet.dto.PublishStreamDto;
import com.ticonsys.online_meet.dto.RoomDto;
import com.ticonsys.online_meet.dto.SubscribeToStreamsDto;
import io.github.rashedul.janus.client.JanusClient;
import io.github.rashedul.janus.client.JanusConfiguration;
import io.github.rashedul.janus.client.JanusSession;
import io.github.rashedul.janus.client.handle.impl.VideoRoomHandle;
import io.github.rashedul.janus.client.plugins.videoroom.events.*;
import io.github.rashedul.janus.client.plugins.videoroom.listeners.JanusVideoRoomListener;
import io.github.rashedul.janus.client.plugins.videoroom.models.*;
import io.github.rashedul.janus.utils.ServerInfo;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
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

    private final SimpMessagingTemplate ws;

    // app-level maps
    private final Map<String, RoomDto> rooms = new ConcurrentHashMap<>();
    private final Map<Long, VideoRoomHandle> adminRoomHandles = new ConcurrentHashMap<>(); // janusRoomId -> admin handle
    private final Map<String, List<String>> joinRooms = new ConcurrentHashMap<>();   // appRoomId -> user list
    private final Map<String, VideoRoomHandle> userHandles = new ConcurrentHashMap<>(); // displayName -> handle
    private final Map<String, JanusSession> userSessions = new ConcurrentHashMap<>();  // displayName -> session (for cleanup)

    public RoomService(SimpMessagingTemplate ws) {
        this.ws = ws;
    }

    @PostConstruct
    @SneakyThrows
    public void init() {
        JanusConfiguration config = new JanusConfiguration(
                janusUrl,
                janusWsPort,
                "/janus",
                janusWsSSL,
                janusLog
        );

        client = new JanusClient(config);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down JanusClient...");
            try {
                if (client != null) client.disconnect();
            } catch (Exception ignore) {}
        }));

        logger.info("Connecting to Janus server at {}...", config.getUri());
        client.connect().get(10, TimeUnit.SECONDS);
    }

    /**
     * Create a room and join admin as publisher.
     */
    @SneakyThrows
    public RoomDto createRoom(RoomDto dto) {
        ServerInfo info = client.getServerInfo().get();
        logger.info("Janus server: {}", info.versionString());

        JanusSession session = client.createSession().get();
        VideoRoomHandle adminHandle = session.attachToVideoRoom().get();

        // add listener to admin handle; this listener also forwards JSEP answers to clients:
        addVideoRoomListener(adminHandle, dto.getName());

        CreateRoomRequest createRequest = new CreateRoomRequest.Builder()
                .setDescription(dto.getName())
                .setPublishers(dto.getParticipants())
                .build();

        CreateRoomResponse createResp = adminHandle.createRoom(createRequest).get();
        long janusRoomId = createResp.room();

        JoinRoomRequest joinReq = new JoinRoomRequest.Builder(janusRoomId)
                .setDisplay(dto.getAdminName())
                .build();

        adminHandle.join(joinReq).get();

        dto.setRoomId(UUID.randomUUID().toString());
        dto.setJanusRoomId(janusRoomId);
        rooms.put(dto.getRoomId(), dto);

        adminRoomHandles.put(janusRoomId, adminHandle);
        joinRooms.put(dto.getRoomId(), new ArrayList<>(List.of(dto.getAdminName())));
        userHandles.put(dto.getAdminName(), adminHandle); // admin's handle
        userSessions.put(dto.getAdminName(), session);

        logger.info("Created room {} (janus {}) by {}", dto.getRoomId(), janusRoomId, dto.getAdminName());
        return dto;
    }

    /**
     * Join room as a new participant (creates session + handle per user).
     */
    @SneakyThrows
    public JoinUserDto joinRoom(JoinUserDto dto) {
        RoomDto roomDto = rooms.get(dto.getRoomId());
        if (roomDto == null) throw new RuntimeException("Room not found");

        List<String> users = joinRooms.get(dto.getRoomId());
        if (users.contains(dto.getName())) throw new RuntimeException("User already joined");
        if (roomDto.getParticipants() <= users.size()) throw new RuntimeException("Room is full");

        JanusSession session = client.createSession().get();
        VideoRoomHandle userHandle = session.attachToVideoRoom().get();

        addVideoRoomListener(userHandle, dto.getName());

        JoinRoomRequest joinReq = new JoinRoomRequest.Builder(roomDto.getJanusRoomId())
                .setDisplay(dto.getName())
                .build();

        userHandle.join(joinReq).get();

        users.add(dto.getName());
        joinRooms.put(dto.getRoomId(), users);

        userHandles.put(dto.getName(), userHandle);
        userSessions.put(dto.getName(), session);

        logger.info("User {} joined room {}", dto.getName(), dto.getRoomId());
        return dto;
    }

    /**
     * Publish stream: send publish request with client SDP offer to Janus. Forward jsep in response (if any).
     */
    @SneakyThrows
    public void publishStream(PublishStreamDto dto) {
        VideoRoomHandle handle = userHandles.get(dto.getDisplayName());
        if (handle == null) throw new RuntimeException("User not joined");

        PublishRequest request = new PublishRequest.Builder()
                .setDisplay(dto.getDisplayName())
                .setVideoCodec("vp8")
                .setAudioCodec("opus")
                .build();

        JSONObject json = request.toJson();
        json.put("jsep", new JSONObject().put("type", "offer").put("sdp", dto.getSdpOffer()));

        logger.info("Sending publish request for {}", dto.getDisplayName());
        JSONObject resp = handle.sendMessage(json).get();

        // Check direct response for jsep (some client libs return it directly)
        if (resp != null && resp.has("jsep")) {
            JSONObject jsep = resp.getJSONObject("jsep");
            forwardJsepToClients(dto.getRoomId(), dto.getDisplayName(), jsep);
        }
        // Otherwise the listener will receive any asynchronous jsep; listener also forwards
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
            } catch (Exception ignored) {}
            try {
                handle.detach();
            } catch (Exception ignored) {}
        }
        if (session != null) {
            try {
                session.destroy();
            } catch (Exception ignored) {}
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
        ws.convertAndSend("/topic/notify-received", payload.toString());
        logger.info("Forwarded JSEP to clients for {} (room {})", display, roomId);
    }

    /**
     * Add listener to a handle. For some events, we forward info to clients as well.
     */
    private void addVideoRoomListener(VideoRoomHandle handle, String displayName) {
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
                ws.convertAndSend("/topic/notify-received", payload.toString());
            }

            @Override
            public void onUnpublished(UnpublishedEvent event) {
                logger.info("Unpublished: {}", event.unpublished());
                JSONObject payload = new JSONObject();
                payload.put("event", "unpublished");
                payload.put("unpublished", event.unpublished());
                ws.convertAndSend("/topic/notify-received", payload.toString());
            }

            @Override
            public void onParticipantLeft(ParticipantLeftEvent event) {
                logger.info("Participant left: {}", event.leaving());
                JSONObject payload = new JSONObject();
                payload.put("event", "participant-left");
                payload.put("leaving", event.leaving());
                ws.convertAndSend("/topic/notify-received", payload.toString());
            }

            @Override
            public void onRoomDestroyed(RoomDestroyedEvent event) {
                logger.info("Room destroyed: {}", event.room());
                JSONObject payload = new JSONObject();
                payload.put("event", "room-destroyed");
                payload.put("room", event.room());
                ws.convertAndSend("/topic/notify-received", payload.toString());
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
