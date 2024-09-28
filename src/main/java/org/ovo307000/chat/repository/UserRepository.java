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
    /**
     * 通过昵称查找用户
     *
     * @param nickName 用户的昵称
     * @return 匹配昵称的用户Optional对象
     */
    Optional<User> findByNickName(String nickName);

    /**
     * 根据用户状态查找用户列表
     *
     * @param userStatus 用户的状态
     * @return 符合给定状态的用户列表
     */
    List<User> findByStatus(UserStatus userStatus);
}
