package org.ovo307000.chat.module.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom
{
    /* 聊天室 ID */
    @Id
    private String id;

    /* 聊天室名称 */
    private String chatId;

    /* 发送者 ID */
    private String senderId;

    /* 接收者 ID */
    private String receiverId;
}
