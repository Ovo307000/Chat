package org.ovo307000.chat.repository;

import org.ovo307000.chat.module.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

/**
 * 聊天消息仓库接口，用于操作MongoDB中的聊天消息数据
 */
@Component
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String>
{
    // 省略具体方法，具体方法由MongoRepository提供
}
