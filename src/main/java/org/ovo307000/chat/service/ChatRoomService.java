package org.ovo307000.chat.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ovo307000.chat.module.entity.ChatRoom;
import org.ovo307000.chat.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService
{
    private final ChatRoomRepository chatRoomRepository;

    /**
     * 根据发送者和接收者的ID获取聊天室ID
     * 如果聊天室不存在且参数指示需要创建，则创建新聊天室
     *
     * @param senderId                发送者的ID，用于查找或创建聊天室
     * @param receiverId              接收者的ID，用于查找或创建聊天室
     * @param createNewRoomIfNotExist 如果为true且聊天室不存在时，尝试创建新聊天室
     * @return 可能包含聊天室ID的Optional如果聊天室存在或被创建则包含ID，否则为空
     */
    @Transactional
    public Optional<String> getChatRoomId(@NonNull final String senderId,
                                          @NonNull final String receiverId,
                                          final boolean createNewRoomIfNotExist)
    {
        // 尝试根据发送者和接收者ID查找现有的聊天室
        return this.chatRoomRepository.findBySenderIdAndReceiverId(senderId, receiverId)
                                      .map(ChatRoom::getId) // 如果找到聊天室，返回其ID
                                      .or(() ->
                                          {
                                              // 如果没有找到聊天室并且参数指示需要创建新聊天室
                                              if (createNewRoomIfNotExist)
                                              {
                                                  // 创建并保存新聊天室，然后返回其ID
                                                  return Optional.of(this.createAndSaveChatRoom(senderId, receiverId));
                                              }

                                              // 如果不需要创建新的聊天室或者创建失败，则返回空的Optional
                                              return Optional.empty();
                                          });
    }

    @Transactional
    public String createAndSaveChatRoom(@NonNull final String senderId, @NonNull final String receiverId)
    {
        final var chatId = String.format("%s ❤ %s", senderId, receiverId);

        final var senderReceiverChatRoom = ChatRoom.builder()
                                                   .senderId(senderId)
                                                   .receiverId(receiverId)
                                                   .chatId(chatId)
                                                   .build();

        final var receiverSenderChatRoom = ChatRoom.builder()
                                                   .senderId(receiverId)
                                                   .receiverId(senderId)
                                                   .chatId(chatId)
                                                   .build();

        CompletableFuture.runAsync(() -> this.chatRoomRepository.save(senderReceiverChatRoom))
                         .thenRunAsync(() -> this.chatRoomRepository.save(receiverSenderChatRoom))
                         .thenRun(() -> log.info("Chat room created and saved: {}", chatId))
                         .exceptionally(throwable ->
                                        {
                                            log.error("Error occurred while creating chat room: {}", chatId, throwable);

                                            return null;
                                        });

        return chatId;
    }
}
