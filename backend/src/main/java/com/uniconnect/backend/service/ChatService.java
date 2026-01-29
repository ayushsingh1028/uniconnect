package com.uniconnect.backend.service;

import com.uniconnect.backend.entity.ChatMessage;
import com.uniconnect.backend.entity.MarketplaceItem;
import com.uniconnect.backend.entity.User;
import com.uniconnect.backend.exception.ResourceNotFoundException;
import com.uniconnect.backend.repository.ChatMessageRepository;
import com.uniconnect.backend.repository.MarketplaceItemRepository;
import com.uniconnect.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final MarketplaceItemRepository marketplaceItemRepository;

    @Transactional
    public ChatMessage sendMessage(Long receiverId, Long itemId, String content, Authentication auth) {
        User sender = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);

        if (itemId != null) {
            MarketplaceItem item = marketplaceItemRepository.findById(itemId).orElse(null);
            message.setItem(item);
        }

        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getConversation(Long otherUserId, Authentication auth) {
        User user1 = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User user2 = userRepository.findById(otherUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Other user not found"));

        return chatMessageRepository.findConversation(user1, user2);
    }

    public List<User> getChatPartners(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<ChatMessage> sent = chatMessageRepository.findBySender(user);
        List<ChatMessage> received = chatMessageRepository.findByReceiver(user);

        java.util.Set<User> partners = new java.util.HashSet<>();
        sent.forEach(m -> partners.add(m.getReceiver()));
        received.forEach(m -> partners.add(m.getSender()));

        return new java.util.ArrayList<>(partners);
    }
}
