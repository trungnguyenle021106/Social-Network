/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/SpringFramework/Controller.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.model.Message;
import static java.lang.String.format;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author Nguyen Le
 */
@Controller
public class SocketController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @GetMapping("/chatroom")
    @MessageMapping("/chat/{roomId}/sendMessage")
    public void sendMessage(@DestinationVariable String roomId, @Payload Message message) {
        messagingTemplate.convertAndSend(format("/channel/%s", roomId), message);
    }

//    @MessageMapping("/chat/{roomId}/addUser")
//    public void addUser(@DestinationVariable String roomId, @Payload Message message,
//            SimpMessageHeaderAccessor headerAccessor) {
//        headerAccessor.getSessionAttributes().put("room_id", roomId);
//        headerAccessor.getSessionAttributes().put("username", message.getSender());
//    }
}