package org.ovo307000.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ovo307000.chat.module.entity.ChatMessage;
import org.ovo307000.chat.repository.ChatMessageRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService
{
    private final ChatRoomService       chatRoomService;
    private final ChatMessageRepository chatMessageRepository;

    public CompletableFuture<ChatMessage> saveChatMessageAsync(@NonNull final ChatMessage chatMessage)
    {
        final var chatRoomId = this.chatRoomService.getChatRoomId(chatMessage.getSenderId(),
                                                                  chatMessage.getReceiverId(),
                                                                  true)
                                                   .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        chatMessage.setChatRoomId(chatRoomId);

        return CompletableFuture.supplyAsync(() -> this.chatMessageRepository.save(chatMessage));
    }

    public CompletableFuture<List<ChatMessage>> fetchChatMessagesAsync(@NonNull final String senderId,
                                                                       @NonNull final String receiverId)
    {
        final var chatRoomId = this.chatRoomService.getChatRoomId(senderId, receiverId, false)
                                                   .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        return CompletableFuture.supplyAsync(() -> this.chatMessageRepository.findByChatRoomId(chatRoomId));
    }
}
