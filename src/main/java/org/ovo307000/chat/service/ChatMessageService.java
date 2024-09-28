package org.ovo307000.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ovo307000.chat.module.entity.ChatMessage;
import org.ovo307000.chat.repository.ChatMessageRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * ChatMessageService 提供异步服务来处理聊天消息的保存和检索。
 * 它依赖于 ChatRoomService 来获取聊天室 ID，并与 ChatMessageRepository 交互来存取数据库中的消息。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService
{
    // 依赖的服务和存储库，用于处理聊天室和消息
    private final ChatRoomService       chatRoomService;
    private final ChatMessageRepository chatMessageRepository;

    /**
     * 异步保存聊天消息。
     *
     * @param chatMessage 待保存的聊天消息对象，不能为空。
     * @return CompletableFuture<ChatMessage> 保存操作的异步结果。
     * @throws IllegalArgumentException 如果找不到对应的聊天室。
     */
    public CompletableFuture<ChatMessage> saveChatMessageAsync(@NonNull final ChatMessage chatMessage)
    {
        // 获取发送者和接收者之间的聊天室 ID，如果不存在则抛出异常
        final var chatRoomId = this.chatRoomService.getChatRoomId(chatMessage.getSenderId(),
                                                                  chatMessage.getReceiverId(),
                                                                  true)
                                                   .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        // 设置消息的聊天室 ID
        chatMessage.setChatRoomId(chatRoomId);

        // 异步保存消息并返回未来的结果
        return CompletableFuture.supplyAsync(() -> this.chatMessageRepository.save(chatMessage));
    }

    /**
     * 异步获取两个用户之间的聊天消息。
     *
     * @param senderId 发送者的唯一标识，不能为空。
     * @param receiverId 接收者的唯一标识，不能为空。
     * @return CompletableFuture<List<ChatMessage>> 消息列表的异步结果。
     * @throws IllegalArgumentException 如果找不到对应的聊天室。
     */
    public CompletableFuture<List<ChatMessage>> fetchChatMessagesAsync(@NonNull final String senderId,
                                                                       @NonNull final String receiverId)
    {
        // 获取发送者和接收者之间的聊天室 ID，如果不存在则抛出异常
        final var chatRoomId = this.chatRoomService.getChatRoomId(senderId, receiverId, false)
                                                   .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        // 异步检索聊天室的所有消息并返回未来的结果
        return CompletableFuture.supplyAsync(() -> this.chatMessageRepository.findByChatRoomId(chatRoomId));
    }
}
