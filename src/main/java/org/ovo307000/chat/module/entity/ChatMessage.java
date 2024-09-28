package org.ovo307000.chat.module.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage
{
    /* 消息 ID */
    @Id
    private String id;

    /* 聊天室 ID */
    private String chatId;

    /* 发送者 ID */
    private String senderId;

    /* 接收者 ID */
    private String receiverId;

    /* 消息内容 */
    private String content;

    /* 时间戳 */
    private LocalDateTime timestamp;
}
