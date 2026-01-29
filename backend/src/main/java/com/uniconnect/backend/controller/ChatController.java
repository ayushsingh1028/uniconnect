package com.uniconnect.backend.controller;

import com.uniconnect.backend.entity.ChatMessage;
import com.uniconnect.backend.entity.User;
import com.uniconnect.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<ChatMessage> sendMessage(@RequestBody Map<String, Object> request, Authentication auth) {
        Long receiverId = ((Number) request.get("receiverId")).longValue();
        Long itemId = request.get("itemId") != null ? ((Number) request.get("itemId")).longValue() : null;
        String content = (String) request.get("content");
        return ResponseEntity.ok(chatService.sendMessage(receiverId, itemId, content, auth));
    }

    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<List<ChatMessage>> getConversation(@PathVariable Long otherUserId, Authentication auth) {
        return ResponseEntity.ok(chatService.getConversation(otherUserId, auth));
    }

    @GetMapping("/partners")
    public ResponseEntity<List<User>> getChatPartners(Authentication auth) {
        return ResponseEntity.ok(chatService.getChatPartners(auth));
    }
}
