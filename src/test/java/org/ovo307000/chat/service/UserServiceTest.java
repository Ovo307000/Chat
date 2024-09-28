package org.ovo307000.chat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ovo307000.chat.module.entity.User;
import org.ovo307000.chat.module.enumeration.UserStatus;
import org.ovo307000.chat.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceTest
{

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp()
    {
        // 初始化所有的@Mock注解的mock对象
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUserAsync()
    {
        // 创建一个测试用的User对象
        var user = new User();
        user.setNickName("testUser");

        // 调用被测试的方法
        this.userService.saveUserAsync(user);

        // 验证用户状态被设置为在线
        assertEquals(UserStatus.ONLINE, user.getStatus());
        // 验证userRepository.save方法在1秒内被调用
        verify(this.userRepository, timeout(1000)).save(user);
    }

    @Test
    void testUpdateStatusToOfflineAsyncUserExists()
    {
        // 创建一个测试用的User对象
        var user = new User();
        user.setNickName("testUser");
        // 模拟userRepository的行为
        when(this.userRepository.findById("testUser")).thenReturn(Optional.of(user));

        // 调用被测试的方法
        this.userService.updateStatusToOfflineAsync(user);

        // 验证userRepository.save方法在1秒内被调用，并且用户状态被设置为离线
        verify(this.userRepository, timeout(1000)).save(argThat(u -> u.getStatus() == UserStatus.OFFLINE));
    }

    @Test
    void testUpdateStatusToOfflineAsyncUserNotExists()
    {
        // 创建一个测试用的User对象
        var user = new User();
        user.setNickName("testUser");
        // 模拟userRepository的行为，返回空的Optional
        when(this.userRepository.findById("testUser")).thenReturn(Optional.empty());

        // 验证当用户不存在时，会抛出IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> this.userService.updateStatusToOfflineAsync(user));
    }

    @Test
    void testFindConnectedUsersAsync()
    {
        // 创建测试用的在线用户列表
        var onlineUsers = Arrays.asList(new User(), new User());
        // 模拟userRepository的行为
        when(this.userRepository.findByStatus(UserStatus.ONLINE)).thenReturn(onlineUsers);

        // 调用被测试的方法
        var future = this.userService.findConnectedUsersAsync();

        // 验证结果
        assertEquals(2,
                     future.join()
                           .size());
        // 验证userRepository.findByStatus方法被调用了一次
        verify(this.userRepository, times(1)).findByStatus(UserStatus.ONLINE);
    }

    @Test
    void testUpdateStatusToOnlineAsyncUserExists()
    {
        // 创建一个测试用的User对象
        var user = new User();
        user.setNickName("testUser");
        // 模拟userRepository的行为
        when(this.userRepository.findById("testUser")).thenReturn(Optional.of(user));

        // 调用被测试的方法
        this.userService.updateStatusToOnlineAsync(user);

        // 验证userRepository.save方法在1秒内被调用，并且用户状态被设置为在线
        verify(this.userRepository, timeout(1000)).save(argThat(u -> u.getStatus() == UserStatus.ONLINE));
    }

    @Test
    void testUpdateStatusToOnlineAsyncUserNotExists()
    {
        // 创建一个测试用的User对象
        var user = new User();
        user.setNickName("testUser");
        // 模拟userRepository的行为，返回空的Optional
        when(this.userRepository.findById("testUser")).thenReturn(Optional.empty());

        // 验证当用户不存在时，会抛出IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> this.userService.updateStatusToOnlineAsync(user));
    }
}