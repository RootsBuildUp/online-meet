package com.ticonsys.online_meet.handler;

import com.ticonsys.online_meet.dto.JoinUserDto;
import com.ticonsys.online_meet.dto.PublishStreamDto;
import com.ticonsys.online_meet.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.json.JSONObject;

@Service
@RequiredArgsConstructor
public class VideoSignalingHandler extends TextWebSocketHandler {

    private final RoomManager roomManager;
    private final RoomService roomService;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JSONObject json = new JSONObject(message.getPayload());
        String roomId = json.getString("roomId");
        String type = json.getString("type");
        String displayName = json.getString("displayName");
        String clientId = json.getString("clientId");

        switch (type) {
            case "join"->{
                // Add session to room when joining
                System.err.println("Joining room: " + roomId);
                roomManager.addToRoom(roomId, session);
                JoinUserDto dto = new  JoinUserDto(roomId, clientId, displayName);
                roomService.joinRoom(dto);
                Thread.sleep(3000);
                session.sendMessage(new TextMessage(new JSONObject().put("type", "new-publisher").toString()));
            }
            case "offer", "answer" -> {
                System.err.println("room activity: " + type);
                String sdp = json.getString("sdp");
                JSONObject jsep = new JSONObject();
                jsep.put("jsep", new JSONObject().put("type", type).put("sdp", sdp));
                roomService.publishStream(new PublishStreamDto(roomId, clientId, type, jsep));
                Thread.sleep(3000);
            }
            case "ice" -> {
                System.err.println("room activity: " + type);
                System.err.println(json);
                JSONObject candidate = json.getJSONObject("candidate");
                JSONObject jsep = new JSONObject();
                jsep.put("jsep", new JSONObject().put("type", type).put("candidate", candidate));

                roomService.publishStream(new PublishStreamDto(roomId, clientId, type, candidate));
                Thread.sleep(3000);
            }
            case "leave"->{
                System.err.println("Leaving room: " + roomId);
                roomManager.removeFromRoom(roomId, session);
                Thread.sleep(1000);
            }
        }


        // Broadcast message to other peers in the room
        for (WebSocketSession s : roomManager.getRoomSessions(roomId)) {
            if (!s.getId().equals(session.getId())) { // don't send to self
                s.sendMessage(message);
                Thread.sleep(3000);
            }
        }

        // Remove session when leaving
        if ("leave".equals(type)) {
            roomManager.removeFromRoom(roomId, session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // Remove from all rooms
        roomManager.getRooms().forEach((roomId, sessions) -> {
            roomManager.removeFromRoom(roomId, session);
        });
    }
}
