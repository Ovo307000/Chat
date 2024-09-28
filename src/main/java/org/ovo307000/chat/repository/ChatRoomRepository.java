package org.ovo307000.chat.repository;

import lombok.NonNull;
import org.ovo307000.chat.module.entity.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String>
{
    Optional<ChatRoom> findBySenderIdAndReceiverId(@NonNull String senderId, @NonNull String receiverId);
}
