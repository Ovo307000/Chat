package org.ovo307000.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ovo307000.chat.module.dto.UserDTO;
import org.ovo307000.chat.module.entity.User;
import org.ovo307000.chat.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

// 使用日志记录用户操作
@Slf4j
// 通过构造器注入必需的依赖，好处是不需要手动编写构造器，Lombok 会自动生成
@RequiredArgsConstructor
// 定义用户相关API的基路径
@RequestMapping("api/v1/user")
@RestController
public class UserController
{
    // 注入用户服务类
    private final UserService userService;

    /**
     * 添加新用户
     *
     * @param user 要添加的用户对象，不能为空
     * @return 添加的用户对象
     */
    @SendTo("/user/topic")
    @MessageMapping("/user.addUser")
    public ResponseEntity<UserDTO> addUser(@Payload @NonNull final User user)
    {
        // 记录正在添加的用户昵称
        log.info("Adding user: {}", user.getNickName());

        // 异步保存用户
        this.userService.saveUserAsync(user);

        // 返回添加的用户
        return ResponseEntity.ok(new UserDTO(user.getNickName(), user.getFullName(), user.getStatus()));
    }

    /**
     * 用户连接
     *
     * @param user 要连接的用户对象，不能为空
     * @return 包含用户昵称、全名和状态的用户详细信息对象
     */
    @SendTo("/user/topic")
    @MessageMapping("/user.connectUser")
    public ResponseEntity<UserDTO> connectUser(@Payload @NonNull final User user)
    {
        // 记录正在连接的用户昵称
        log.info("Connecting user: {}", user.getNickName());

        // 异步更新用户状态为在线
        this.userService.updateStatusToOnlineAsync(user);

        // 返回用户详细信息的响应实体
        return ResponseEntity.ok(new UserDTO(user.getNickName(), user.getFullName(), user.getStatus()));
    }

    /**
     * 获取所有用户信息
     *
     * @return 包含所有用户信息的响应实体
     */
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers()
    {
        // 定义变量用于存储所有用户信息
        final List<UserDTO> users;

        // 尝试块，用于处理执行过程中可能抛出的异常
        try
        {
            // 调用用户服务获取所有连接的用户，并处理可能抛出的异常
            users = Objects.requireNonNull(this.userService.findConnectedUsers())
                           .get()
                           .stream()
                           .map(user -> new UserDTO(user.getNickName(), user.getFullName(), user.getStatus()))
                           .toList();
        }
        catch (InterruptedException | ExecutionException e)
        {
            // 当捕获到异常时，记录错误日志并抛出运行时异常
            log.error("Error occurred while finding connected users", e);
            throw new RuntimeException(e);
        }

        // 返回包含所有用户信息的响应实体
        return ResponseEntity.ok(users);
    }
}
