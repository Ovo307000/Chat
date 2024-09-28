package org.ovo307000.chat.module.dto;

import org.ovo307000.chat.module.entity.User;
import org.ovo307000.chat.module.enumeration.UserStatus;

import java.io.Serializable;

/**
 * 用户数据传输对象（DTO）。用于在不同层或服务之间传输用户数据。
 * 包含用户的昵称、全名和状态信息。
 */
public record UserDTO(String nickName, String fullName, UserStatus status) implements Serializable
{
    /**
     * 将 User 实体转换为 UserDTO 对象。
     * 此静态方法提供了一种方便的方式将 User 实体转换为 DTO，适用于需要简化格式传输或存储用户信息的场景。
     *
     * @param user 需要转换的 User 实体
     * @return 转换后的 UserDTO 对象
     */
    public static UserDTO fromUser(final User user)
    {
        return new UserDTO(user.getNickName(), user.getFullName(), user.getStatus());
    }
}
