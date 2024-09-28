package org.ovo307000.chat.repository;

import org.ovo307000.chat.module.entity.User;
import org.ovo307000.chat.module.enumeration.UserStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓库接口，用于操作用户数据
 * 继承自MongoRepository，提供对用户实体的CRUD操作
 */
@Component
public interface UserRepository extends MongoRepository<User, String>
{
    // 此处无需额外定义方法，MongoRepository已提供基本的CRUD操作
    Optional<User> findByNickName(String nickName);

    List<User> findByStatus(UserStatus userStatus);
}
