package org.ovo307000.chat.module;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.ovo307000.chat.enumration.UserStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
@RequiredArgsConstructor
public class User
{
    @Id
    private String     nickName;
    private String     fullName;
    private String     password;
    private UserStatus status;
}
