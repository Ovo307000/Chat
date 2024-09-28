package org.ovo307000.chat.module.dto;

import org.ovo307000.chat.module.entity.User;
import org.ovo307000.chat.module.enumeration.UserStatus;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
public record UserDTO(String nickName, String fullName, UserStatus status) implements Serializable
{
    public static UserDTO fromUser(final User user)
    {
        return new UserDTO(user.getNickName(), user.getFullName(), user.getStatus());
    }
}