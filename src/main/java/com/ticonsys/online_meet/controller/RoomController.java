package com.ticonsys.online_meet.controller;

import com.ticonsys.online_meet.dto.JoinUserDto;
import com.ticonsys.online_meet.dto.RoomDto;
import com.ticonsys.online_meet.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;


    @GetMapping("/create")
    public RoomDto createRoom() {
        return roomService.createRoom();
    }

    @PostMapping("/join")
    public JoinUserDto joinRoom(@RequestBody JoinUserDto dto) {
        return roomService.joinRoom(dto);
    }

}
