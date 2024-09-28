package org.ovo307000.chat.repository;

import org.ovo307000.chat.module.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 用户仓库接口，用于操作用户数据
 * 继承自MongoRepository，提供对用户实体的CRUD操作
 */
public interface UserRepository extends MongoRepository<User, String>
{
    // 此处无需额外定义方法，MongoRepository已提供基本的CRUD操作
}
