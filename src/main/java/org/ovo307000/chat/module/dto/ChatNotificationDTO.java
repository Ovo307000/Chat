package org.ovo307000.chat.module.dto;

import org.ovo307000.chat.module.entity.ChatMessage;
import org.ovo307000.chat.module.entity.ChatNotification;

/**
 * DTO for {@link org.ovo307000.chat.module.entity.ChatNotification}
 */
public record ChatNotificationDTO(String id, String senderId, String receiverId, String content)
{
    public static ChatNotificationDTO fromChatNotification(final ChatNotification chatNotification)
    {
        return new ChatNotificationDTO(chatNotification.getId(),
                                       chatNotification.getSenderId(),
                                       chatNotification.getReceiverId(),
                                       chatNotification.getContent());
    }

    public static ChatNotificationDTO fromChatMessage(final ChatMessage chatMessage)
    {
        return new ChatNotificationDTO(chatMessage.getId(),
                                       chatMessage.getSenderId(),
                                       chatMessage.getReceiverId(),
                                       chatMessage.getContent());
    }
}
