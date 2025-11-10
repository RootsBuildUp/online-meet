package com.ticonsys.online_meet.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.json.JSONObject;

@Component
public class VideoSignalingHandler extends TextWebSocketHandler {

    private final RoomManager roomManager = new RoomManager();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JSONObject json = new JSONObject(message.getPayload());
        String roomId = json.getString("roomId");
        String type = json.getString("type");

        // Add session to room when joining
        if ("join".equals(type)) {
            roomManager.addToRoom(roomId, session);
        }

        // Broadcast message to other peers in the room
        for (WebSocketSession s : roomManager.getRoomSessions(roomId)) {
            if (!s.getId().equals(session.getId())) { // don't send to self
                s.sendMessage(message);
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
