package org.ovo307000.chat.module.dto;

import org.ovo307000.chat.module.entity.ChatMessage;
import org.ovo307000.chat.module.entity.ChatNotification;

/**
 * DTO for {@link org.ovo307000.chat.module.entity.ChatNotification}
 * 此记录类用于传输聊天通知信息。
 */
public record ChatNotificationDTO(String id, String senderId, String receiverId, String content)
{
    /**
     * 将 ChatNotification 实体转换为 ChatNotificationDTO。
     *
     * @param chatNotification 要转换的 ChatNotification 实体。
     * @return 转换后的 ChatNotificationDTO。
     */
    public static ChatNotificationDTO fromChatNotification(final ChatNotification chatNotification)
    {
        return new ChatNotificationDTO(chatNotification.getId(),
                                       chatNotification.getSenderId(),
                                       chatNotification.getReceiverId(),
                                       chatNotification.getContent());
    }

    /**
     * 将 ChatMessage 实体转换为 ChatNotificationDTO。
     *
     * @param chatMessage 要转换的 ChatMessage 实体。
     * @return 转换后的 ChatNotificationDTO。
     */
    public static ChatNotificationDTO fromChatMessage(final ChatMessage chatMessage)
    {
        return new ChatNotificationDTO(chatMessage.getId(),
                                       chatMessage.getSenderId(),
                                       chatMessage.getReceiverId(),
                                       chatMessage.getContent());
    }
}
