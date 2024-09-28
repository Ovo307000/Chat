package org.ovo307000.chat.module.entity;

import lombok.*;
import org.ovo307000.chat.module.enumeration.UserStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor
public class User
{
    /* 用户昵称 */
    @Id
    private String     nickName;

    /* 用户全名 */
    private String     fullName;

    /* 用户密码 */
    private String     password;

    /* 用户状态 */
    private UserStatus status;
}
