package org.ovo307000.chat.module.entity;

import lombok.*;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatNotification
{
    /* 消息 ID */
    @Id
    private String id;

    /* 发送者 ID */
    private String senderId;

    /* 接收者 ID */
    private String receiverId;

    /* 消息内容 */
    private String content;
}
