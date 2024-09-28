package org.ovo307000.chat.module.dto;

import java.io.Serializable;

/**
 * DTO for {@link org.ovo307000.chat.module.entity.ChatRoom}
 */
public record ChatRoomDTO(String id, String chatId, String senderId, String receiverId) implements Serializable
{
}