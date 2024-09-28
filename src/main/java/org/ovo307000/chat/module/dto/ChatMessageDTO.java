package org.ovo307000.chat.module.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link org.ovo307000.chat.module.entity.ChatMessage}
 */
public record ChatMessageDTO(String id, String chatId, String senderId, String receiverId, String content,
                             LocalDateTime timestamp) implements Serializable
{
}