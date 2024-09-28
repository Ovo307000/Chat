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

/**
 * ChatController 是一个处理聊天相关请求的Rest控制器。
 * 它主要负责获取聊天消息和处理新聊天消息的发送。
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController
{
    // 服务层对象，用于处理聊天消息的业务逻辑
    private final ChatMessageService    chatMessageService;
    // 模板对象，用于发送消息到指定的STOMP端点
    private final SimpMessagingTemplate brokerMessagingTemplate;

    /**
     * 根据发送者和接收者的ID异步获取聊天消息。
     *
     * @param senderId 发送者的唯一标识符。
     * @param receiverId 接收者的唯一标识符。
     * @return 包含聊天消息的响应实体。
     */
    @GetMapping("/messages/{senderId}/{receiverId}")
    public ResponseEntity<List<ChatMessageDTO>> fetchChatMessages(
            @NonNull @PathVariable("senderId") final String senderId,
            @NonNull @PathVariable("receiverId") final String receiverId)
    {
        log.info("Fetching chat messages between {} and {}", senderId, receiverId);

        // 异步获取聊天消息，并将结果转换为ChatMessageDTO列表
        final var chatMessages = this.chatMessageService.fetchChatMessagesAsync(senderId, receiverId);

        return ResponseEntity.ok(ChatMessageDTO.fromChatMessages(chatMessages.join()));
    }

    /**
     * 处理通过WebSocket发送的聊天消息，并将其保存到数据库中。
     * 然后通过brokerMessagingTemplate将消息发送到接收者的个人队列中。
     *
     * @param chatMessage 要处理的聊天消息。
     */
    @MessageMapping("/chat")
    public void processChatMessage(@NonNull final ChatMessage chatMessage)
    {
        log.info("Processing chat message by {} to {}", chatMessage.getSenderId(), chatMessage.getReceiverId());

        // 异步保存聊天消息到数据库
        this.chatMessageService.saveChatMessageAsync(chatMessage);
        // 将消息转换为ChatNotificationDTO并发送给接收者
        this.brokerMessagingTemplate.convertAndSendToUser(chatMessage.getReceiverId(),
                                                          "/queue/messages",
                                                          ChatNotificationDTO.fromChatMessage(chatMessage));
    }
}
