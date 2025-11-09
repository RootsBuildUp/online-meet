package com.ticonsys.online_meet.controller;

import com.ticonsys.online_meet.dto.PublishStreamDto;
import com.ticonsys.online_meet.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final RoomService roomService;

    // client sends a publish request (SDP offer)
    @MessageMapping("/notify-send")
    public void publish(PublishStreamDto dto) throws Exception {
        System.err.println("publish notify send : " + dto.toString());
        // We do not return; publish response (jsep) will be forwarded asynchronously
        roomService.publishStream(dto);
    }

    // optional unpublish
    @MessageMapping("/unpublish")
    public void unpublish(String displayName) throws Exception {
        roomService.unpublish(displayName);
    }
}
