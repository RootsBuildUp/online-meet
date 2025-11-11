package com.ticonsys.online_meet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MeetingController {

    @GetMapping("/meet")
    public String meet(Model model) {
//        model.addAttribute("roomName", room);
        return "index"; // returns templates/index.html (Thymeleaf)
    }
}
