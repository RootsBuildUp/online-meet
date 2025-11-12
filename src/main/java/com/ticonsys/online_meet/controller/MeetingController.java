package com.ticonsys.online_meet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MeetingController {

    @GetMapping("/video-meet")
    public String meet(Model model) {
//        model.addAttribute("roomName", room);
        return "videoroom"; // returns templates/videoroom.html (Thymeleaf)
    }

    @GetMapping("/test")
    public String test(Model model) {
//        model.addAttribute("roomName", room);
        return "index-test"; // returns templates/test.html (Thymeleaf)
    }
}
