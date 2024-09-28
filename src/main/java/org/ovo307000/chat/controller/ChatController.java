package org.ovo307000.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ovo307000.chat.module.dto.ChatMessageDTO;
import org.ovo307000.chat.module.dto.ChatNotificationDTO;
import org.ovo307000.chat.module.entity.ChatMessage;
import org.ovo307000.chat.service.ChatMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController
{
    private final ChatMessageService    chatMessageService;
    private final SimpMessagingTemplate brokerMessagingTemplate;

    @GetMapping("/messages/{senderId}/{receiverId}")
    public ResponseEntity<List<ChatMessageDTO>> fetchChatMessages(
            @NonNull @PathVariable("senderId") final String senderId,
            @NonNull @PathVariable("receiverId") final String receiverId)
    {
        log.info("Fetching chat messages between {} and {}", senderId, receiverId);

        final var chatMessages = this.chatMessageService.fetchChatMessagesAsync(senderId, receiverId);

        return ResponseEntity.ok(ChatMessageDTO.fromChatMessages(chatMessages.join()));
    }

    @MessageMapping("/chat")
    public void processChatMessage(@NonNull final ChatMessage chatMessage)
    {
        log.info("Processing chat message: {}", chatMessage);

        this.chatMessageService.saveChatMessageAsync(chatMessage);
        this.brokerMessagingTemplate.convertAndSendToUser(chatMessage.getReceiverId(),
                                                          "/queue/messages",
                                                          ChatNotificationDTO.fromChatMessage(chatMessage));
    }
}
