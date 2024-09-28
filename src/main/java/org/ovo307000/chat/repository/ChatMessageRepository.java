package org.ovo307000.chat.repository;

import org.ovo307000.chat.module.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 聊天消息仓库接口，用于操作MongoDB中的聊天消息数据
 */
@Component
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String>
{
    /**
     * 根据聊天室ID查询聊天消息列表
     *
     * @param chatRoomId 聊天室ID，用于定位特定聊天室的聊天消息
     * @return 与指定聊天室ID相关的聊天消息列表
     */
    List<ChatMessage> findByChatRoomId(String chatRoomId);
}
