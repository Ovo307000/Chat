package org.ovo307000.chat.module.dto;

import java.io.Serializable;

/**
 * ChatRoomDTO数据传输对象，用于聊天室信息的传输
 * 该类实现了Serializable接口，确保对象可以被序列化，以便于远程通信或数据持久化
 */
public record ChatRoomDTO(String id, String chatId, String senderId, String receiverId) implements Serializable
{
    /**
     * 从ChatRoom实体类转换为ChatRoomDTO对象
     * 这个静态方法提供了一种简便的方式，将ChatRoom实体类对象转换为ChatRoomDTO对象，便于数据传输或展示层使用
     *
     * @param chatRoom 一个ChatRoom实体对象，包含聊天室的相关信息
     * @return 返回一个ChatRoomDTO对象，该对象基于传入的ChatRoom实体对象的信息创建
     */
    public static ChatRoomDTO fromChatRoom(final org.ovo307000.chat.module.entity.ChatRoom chatRoom)
    {
        return new ChatRoomDTO(chatRoom.getId(),
                               chatRoom.getChatId(),
                               chatRoom.getSenderId(),
                               chatRoom.getReceiverId());
    }
}
