package org.ovo307000.chat.module.dto;

import org.ovo307000.chat.module.entity.ChatMessage;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link org.ovo307000.chat.module.entity.ChatMessage}
 */
public record ChatMessageDTO(String id, String chatId, String senderId, String receiverId, String content,
                             LocalDateTime timestamp) implements Serializable
{
    public static List<ChatMessageDTO> fromChatMessages(final List<? extends ChatMessage> chatMessages)
    {
        return chatMessages.stream()
                           .map(chatMessage -> new ChatMessageDTO(chatMessage.getId(),
                                                                  chatMessage.getChatRoomId(),
                                                                  chatMessage.getSenderId(),
                                                                  chatMessage.getReceiverId(),
                                                                  chatMessage.getContent(),
                                                                  chatMessage.getTimestamp()))
                           .toList();
    }
}