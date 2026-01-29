package com.uniconnect.backend.repository;

import com.uniconnect.backend.entity.ChatMessage;
import com.uniconnect.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m WHERE (m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1) ORDER BY m.createdAt ASC")
    List<ChatMessage> findConversation(@Param("user1") User user1, @Param("user2") User user2);

    List<ChatMessage> findBySender(User sender);

    List<ChatMessage> findByReceiver(User receiver);
}
