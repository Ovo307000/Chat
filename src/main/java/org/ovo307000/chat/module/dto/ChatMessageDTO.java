package org.ovo307000.chat.module.dto;

import org.ovo307000.chat.module.entity.ChatMessage;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

// ChatMessageDTO 类用于表示聊天消息的数据传输对象
public record ChatMessageDTO(String id,          // 消息的唯一标识符
                             String chatId,      // 聊天室的唯一标识符
                             String senderId,    // 发送消息的用户ID
                             String receiverId,  // 接收消息的用户ID
                             String content,     // 消息的具体内容
                             LocalDateTime timestamp  // 消息发送的时间戳
) implements Serializable
{
    // 静态方法，用于将ChatMessage实体列表转换为ChatMessageDTO列表
    public static List<ChatMessageDTO> fromChatMessages(final List<? extends ChatMessage> chatMessages)
    {
        // 使用Stream API处理chatMessages列表
        return chatMessages.stream()
                           // 将每个ChatMessage对象映射为新的ChatMessageDTO对象
                           .map(chatMessage -> new ChatMessageDTO(chatMessage.getId(),
                                                                  chatMessage.getChatRoomId(),
                                                                  chatMessage.getSenderId(),
                                                                  chatMessage.getReceiverId(),
                                                                  chatMessage.getContent(),
                                                                  chatMessage.getTimestamp()))
                           // 收集转换后的对象到一个新的List中
                           .toList();
    }
}
