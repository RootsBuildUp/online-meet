package com.ticonsys.online_meet.controller;

import com.ticonsys.online_meet.dto.JoinUserDto;
import com.ticonsys.online_meet.dto.RoomDto;
import com.ticonsys.online_meet.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;


    @PostMapping("/create")
    public RoomDto createRoom(@RequestBody RoomDto name) {
        return roomService.createRoom(name);
    }

    @PostMapping("/join")
    public JoinUserDto joinRoom(@RequestBody JoinUserDto dto) {
        return roomService.joinRoom(dto);
    }

}
