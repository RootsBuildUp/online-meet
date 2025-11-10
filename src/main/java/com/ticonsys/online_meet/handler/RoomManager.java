package com.ticonsys.online_meet.handler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.springframework.web.socket.WebSocketSession;

public class RoomManager {

    // Map<roomId, Set of sessions>
    private final ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    public void addToRoom(String roomId, WebSocketSession session) {
        rooms.computeIfAbsent(roomId, k -> new CopyOnWriteArraySet<>()).add(session);
    }

    public void removeFromRoom(String roomId, WebSocketSession session) {
        Set<WebSocketSession> sessions = rooms.get(roomId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                rooms.remove(roomId);
            }
        }
    }

    public Set<WebSocketSession> getRoomSessions(String roomId) {
        return rooms.getOrDefault(roomId, new CopyOnWriteArraySet<>());
    }

    public ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocketSession>> getRooms() {
        return rooms;
    }
}
