package org.ovo307000.chat.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ovo307000.chat.module.entity.User;
import org.ovo307000.chat.module.enumeration.UserStatus;
import org.ovo307000.chat.service.UserService;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest
{

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddUser()
    {
        // 准备测试数据
        var user = new User();
        user.setNickName("testUser");
        user.setFullName("Test User");
        user.setStatus(UserStatus.ONLINE);

        // 模拟服务方法
        doNothing().when(this.userService)
                   .saveUserAsync(user);

        // 执行测试
        var response = this.userController.addUser(user);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testUser",
                     Objects.requireNonNull(response.getBody())
                            .nickName());
        assertEquals("Test User",
                     response.getBody()
                             .fullName());
        assertEquals(UserStatus.ONLINE,
                     response.getBody()
                             .status());

        // 验证服务方法被调用
        verify(this.userService, times(1)).saveUserAsync(user);
    }

    @Test
    void testConnectUser()
    {
        // 准备测试数据
        var user = new User();
        user.setNickName("testUser");
        user.setFullName("Test User");
        user.setStatus(UserStatus.ONLINE);

        // 模拟服务方法
        doNothing().when(this.userService)
                   .updateStatusToOnlineAsync(user);

        // 执行测试
        var response = this.userController.connectUser(user);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testUser",
                     Objects.requireNonNull(response.getBody())
                            .nickName());
        assertEquals("Test User",
                     response.getBody()
                             .fullName());
        assertEquals(UserStatus.ONLINE,
                     response.getBody()
                             .status());

        // 验证服务方法被调用
        verify(this.userService, times(1)).updateStatusToOnlineAsync(user);
    }

    @Test
    void testGetAllUsers()
    {
        // 准备测试数据
        var users = Arrays.asList(new User("user1", "User One", "password", UserStatus.ONLINE),
                                  new User("user2", "User Two", "password", UserStatus.ONLINE));

        // 模拟服务方法
        when(this.userService.findConnectedUsersAsync()).thenReturn(CompletableFuture.completedFuture(users));

        // 执行测试
        var response = this.userController.getAllUsers();

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2,
                     Objects.requireNonNull(response.getBody())
                            .size());
        assertEquals("user1",
                     response.getBody()
                             .get(0)
                             .nickName());
        assertEquals("User One",
                     response.getBody()
                             .get(0)
                             .fullName());
        assertEquals(UserStatus.ONLINE,
                     response.getBody()
                             .get(0)
                             .status());

        // 验证服务方法被调用
        verify(this.userService, times(1)).findConnectedUsersAsync();
    }

    @Test
    void testGetAllUsersWithException()
    {
        // 模拟服务方法抛出异常
        when(this.userService.findConnectedUsersAsync()).thenReturn(CompletableFuture.failedFuture(new RuntimeException(
                "Test exception")));

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> this.userController.getAllUsers());

        // 验证服务方法被调用
        verify(this.userService, times(1)).findConnectedUsersAsync();
    }
}