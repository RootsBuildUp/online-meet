package com.ticonsys.online_meet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MeetingController {

    @GetMapping("/meet/{room}")
    public String meet(@PathVariable String room, Model model) {
        model.addAttribute("roomName", room);
        return "index"; // returns templates/index.html (Thymeleaf)
    }
}
