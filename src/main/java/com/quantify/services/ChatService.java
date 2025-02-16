package com.quantify.services;

import lombok.Data;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Data
@Service
public class ChatService {

    @Autowired
    private final ChatModel chatModel;

    public String getResponse(String prompt){
        return  chatModel.call(prompt);
    }

    public String getResponse(Prompt prompt){
        return  chatModel.call(prompt).getResult().getOutput().getContent();
    }
}
