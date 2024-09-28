package org.ovo307000.chat.repository;

import lombok.NonNull;
import org.ovo307000.chat.module.entity.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 聊天室仓库接口，处理与MongoDB相关的聊天室数据操作
 */
@Component
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String>
{
    /**
     * 根据发送者和接收者的ID查找聊天室
     *
     * @param senderId 发送者的ID，不能为空
     * @param receiverId 接收者的ID，不能为空
     * @return 如果找到匹配的聊天室，则返回包含该聊天室的Optional对象；否则返回空的Optional
     */
    Optional<ChatRoom> findBySenderIdAndReceiverId(@NonNull String senderId, @NonNull String receiverId);
}
