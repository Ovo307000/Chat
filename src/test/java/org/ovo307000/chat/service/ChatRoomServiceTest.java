package org.ovo307000.chat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ovo307000.chat.module.entity.ChatRoom;
import org.ovo307000.chat.repository.ChatRoomRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatRoomServiceTest
{

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @InjectMocks
    private ChatRoomService chatRoomService;

    @BeforeEach
    void setUp()
    {
        // 初始化所有的@Mock注解的mock对象
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetChatRoomIdExisting()
    {
        // 创建一个测试用的ChatRoom对象
        var chatRoom = new ChatRoom();
        chatRoom.setId("chatRoom1");
        // 模拟chatRoomRepository的行为
        when(this.chatRoomRepository.findBySenderIdAndReceiverId("sender1", "receiver1")).thenReturn(Optional.of(
                chatRoom));

        // 调用被测试的方法
        var result = this.chatRoomService.getChatRoomId("sender1", "receiver1", false);

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals("chatRoom1", result.get());
    }

    @Test
    void testGetChatRoomIdNotExistingNoCreate()
    {
        // 模拟chatRoomRepository的行为，返回空的Optional
        when(this.chatRoomRepository.findBySenderIdAndReceiverId("sender1", "receiver1")).thenReturn(Optional.empty());

        // 调用被测试的方法，不创建新的聊天室
        var result = this.chatRoomService.getChatRoomId("sender1", "receiver1", false);

        // 验证结果为空
        assertFalse(result.isPresent());
    }

    @Test
    void testGetChatRoomIdNotExistingCreate()
    {
        // 模拟chatRoomRepository的行为，返回空的Optional
        when(this.chatRoomRepository.findBySenderIdAndReceiverId("sender1", "receiver1")).thenReturn(Optional.empty());

        // 调用被测试的方法，创建新的聊天室
        var result = this.chatRoomService.getChatRoomId("sender1", "receiver1", true);

        // 验证结果
        assertTrue(result.isPresent());
        assertTrue(result.get()
                         .contains("sender1"));
        assertTrue(result.get()
                         .contains("receiver1"));
    }

    @Test
    void testCreateAndSaveChatRoom()
    {
        // 调用被测试的方法
        var chatId = this.chatRoomService.createAndSaveChatRoom("sender1", "receiver1");

        // 验证结果
        assertNotNull(chatId);
        assertTrue(chatId.contains("sender1"));
        assertTrue(chatId.contains("receiver1"));
        // 验证chatRoomRepository.save方法被调用了两次（为发送者和接收者各创建一个聊天室）
        verify(this.chatRoomRepository, times(2)).save(any(ChatRoom.class));
    }
}