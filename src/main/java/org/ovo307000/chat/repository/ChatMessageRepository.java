package org.ovo307000.chat.repository;

import org.ovo307000.chat.module.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String>
{
}
