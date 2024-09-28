package org.ovo307000.chat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ovo307000.chat.module.entity.ChatMessage;
import org.ovo307000.chat.repository.ChatMessageRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatMessageServiceTest
{

    @Mock
    private ChatRoomService chatRoomService;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatMessageService chatMessageService;

    @BeforeEach
    void setUp()
    {
        // 初始化所有的@Mock注解的mock对象
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveChatMessageAsync()
    {
        // 创建一个测试用的ChatMessage对象
        var message = new ChatMessage();
        message.setSenderId("sender1");
        message.setReceiverId("receiver1");
        message.setContent("Hello");
        message.setTimestamp(LocalDateTime.now());

        // 模拟chatRoomService的行为
        when(this.chatRoomService.getChatRoomId("sender1", "receiver1", true)).thenReturn(Optional.of("chatRoom1"));
        // 模拟chatMessageRepository的行为
        when(this.chatMessageRepository.save(any(ChatMessage.class))).thenReturn(message);

        // 调用被测试的方法
        var future = this.chatMessageService.saveChatMessageAsync(message);

        // 验证结果
        assertNotNull(future);
        assertEquals(message, future.join());
        assertEquals("chatRoom1", message.getChatRoomId());
        // 验证chatMessageRepository.save方法被调用了一次
        verify(this.chatMessageRepository, times(1)).save(message);
    }

    @Test
    void testFetchChatMessagesAsync()
    {
        // 模拟chatRoomService的行为
        when(this.chatRoomService.getChatRoomId("sender1", "receiver1", false)).thenReturn(Optional.of("chatRoom1"));

        // 创建测试用的消息列表
        var messages = Arrays.asList(new ChatMessage("1",
                                                     "chatRoom1",
                                                     "sender1",
                                                     "receiver1",
                                                     "Hello",
                                                     LocalDateTime.now()),
                                     new ChatMessage("2",
                                                     "chatRoom1",
                                                     "receiver1",
                                                     "sender1",
                                                     "Hi",
                                                     LocalDateTime.now()));

        // 模拟chatMessageRepository的行为
        when(this.chatMessageRepository.findByChatRoomId("chatRoom1")).thenReturn(messages);

        // 调用被测试的方法
        var future = this.chatMessageService.fetchChatMessagesAsync("sender1", "receiver1");

        // 验证结果
        assertNotNull(future);
        assertEquals(2,
                     future.join()
                           .size());
        // 验证chatMessageRepository.findByChatRoomId方法被调用了一次
        verify(this.chatMessageRepository, times(1)).findByChatRoomId("chatRoom1");
    }

    @Test
    void testSaveChatMessageAsyncChatRoomNotFound()
    {
        // 创建一个测试用的ChatMessage对象
        var message = new ChatMessage();
        message.setSenderId("sender1");
        message.setReceiverId("receiver1");

        // 模拟chatRoomService的行为，返回空的Optional
        when(this.chatRoomService.getChatRoomId("sender1", "receiver1", true)).thenReturn(Optional.empty());

        // 验证当聊天室不存在时，会抛出IllegalArgumentException
        assertThrows(IllegalArgumentException.class,
                     () -> this.chatMessageService.saveChatMessageAsync(message)
                                                  .join());
    }

    @Test
    void testFetchChatMessagesAsyncChatRoomNotFound()
    {
        // 模拟chatRoomService的行为，返回空的Optional
        when(this.chatRoomService.getChatRoomId("sender1", "receiver1", false)).thenReturn(Optional.empty());

        // 验证当聊天室不存在时，会抛出IllegalArgumentException
        assertThrows(IllegalArgumentException.class,
                     () -> this.chatMessageService.fetchChatMessagesAsync("sender1", "receiver1")
                                                  .join());
    }
}