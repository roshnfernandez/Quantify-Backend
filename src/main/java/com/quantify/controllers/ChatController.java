package com.quantify.controllers;

import com.quantify.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("quantify")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping
    public String respondToPrompt(@RequestParam String prompt){
        return  chatService.getResponse(prompt);
    }
}
