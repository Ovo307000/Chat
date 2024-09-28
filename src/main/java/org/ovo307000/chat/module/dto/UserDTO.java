package org.ovo307000.chat.module.dto;

import org.ovo307000.chat.enumration.UserStatus;

import java.io.Serializable;

/**
 * DTO for {@link org.ovo307000.chat.module.User}
 */
public record UserDTO(String nickName, String fullName, UserStatus status) implements Serializable
{
}