package org.ovo307000.chat.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ovo307000.chat.module.dto.ChatMessageDTO;
import org.ovo307000.chat.module.dto.ChatNotificationDTO;
import org.ovo307000.chat.module.entity.ChatMessage;
import org.ovo307000.chat.service.ChatMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatControllerTest
{

    @Mock
    private ChatMessageService chatMessageService;

    @Mock
    private SimpMessagingTemplate brokerMessagingTemplate;

    @InjectMocks
    private ChatController chatController;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchChatMessages()
    {
        // 准备测试数据
        var senderId   = "sender1";
        var receiverId = "receiver1";
        var messages = Arrays.asList(new ChatMessage("1",
                                                     "chatRoom1",
                                                     senderId,
                                                     receiverId,
                                                     "Hello",
                                                     LocalDateTime.now()),
                                     new ChatMessage("2",
                                                                   "chatRoom1",
                                                                   receiverId,
                                                                   senderId,
                                                                   "Hi",
                                                                   LocalDateTime.now()
                                                                                .plusMinutes(1)));

        // 模拟服务方法
        when(this.chatMessageService.fetchChatMessagesAsync(senderId,
                                                            receiverId)).thenReturn(CompletableFuture.completedFuture(
                messages));

        // 执行测试
        var response = this.chatController.fetchChatMessages(senderId, receiverId);

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2,
                     response.getBody()
                             .size());
        assertEquals("Hello",
                     response.getBody()
                             .get(0)
                             .content());
        assertEquals("Hi",
                     response.getBody()
                             .get(1)
                             .content());

        // 验证服务方法被调用
        verify(this.chatMessageService, times(1)).fetchChatMessagesAsync(senderId, receiverId);
    }

    @Test
    void testFetchChatMessagesWithException()
    {
        // 准备测试数据
        var senderId   = "sender1";
        var receiverId = "receiver1";

        // 模拟服务方法抛出异常
        when(this.chatMessageService.fetchChatMessagesAsync(senderId, receiverId)).thenReturn(CompletableFuture.failedFuture(
                new RuntimeException("Test exception")));

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> this.chatController.fetchChatMessages(senderId, receiverId));

        // 验证服务方法被调用
        verify(this.chatMessageService, times(1)).fetchChatMessagesAsync(senderId, receiverId);
    }

    @Test
    void testProcessChatMessage()
    {
        // 准备测试数据
        var chatMessage = new ChatMessage("1",
                                          "chatRoom1",
                                          "sender1",
                                          "receiver1",
                                          "Hello",
                                          LocalDateTime.now());

        // 执行测试
        this.chatController.processChatMessage(chatMessage);

        // 验证服务方法被调用
        verify(this.chatMessageService, times(1)).saveChatMessageAsync(chatMessage);
        verify(this.brokerMessagingTemplate, times(1)).convertAndSendToUser(eq("receiver1"),
                                                                            eq("/queue/messages"),
                                                                            any(ChatNotificationDTO.class));
    }

    @Test
    void testProcessChatMessageWithException()
    {
        // 准备测试数据
        var chatMessage = new ChatMessage("1",
                                          "chatRoom1",
                                          "sender1",
                                          "receiver1",
                                          "Hello",
                                          LocalDateTime.now());

        // 模拟服务方法抛出异常
        doThrow(new RuntimeException("Test exception")).when(this.chatMessageService)
                                                       .saveChatMessageAsync(chatMessage);

        // 执行测试
        assertDoesNotThrow(() -> this.chatController.processChatMessage(chatMessage));

        // 验证服务方法被调用，但不会发送消息
        verify(this.chatMessageService, times(1)).saveChatMessageAsync(chatMessage);
        verify(this.brokerMessagingTemplate, never()).convertAndSendToUser(anyString(),
                                                                           anyString(),
                                                                           any(ChatNotificationDTO.class));
    }
}