package org.ovo307000.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ovo307000.chat.module.entity.User;
import org.ovo307000.chat.module.enumeration.UserStatus;
import org.ovo307000.chat.repository.UserRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService
{
    private final UserRepository userRepository;

    @Transactional
    public void saveUserAsync(@NonNull final User user)
    {
        log.info("Saving user: {}", user.getNickName());

        user.setStatus(UserStatus.ONLINE);

        CompletableFuture.runAsync(() -> this.userRepository.save(user))
                         .thenAcceptAsync(u -> log.info("User saved: {}", user.getNickName()));
    }

    @Transactional
    public void updateStatusToOfflineAsync(@NonNull final User user)
    {
        log.info("Updating user status to offline: {}", user.getNickName());

        if (this.isUserExistInDatabase(user))
        {
            CompletableFuture.runAsync(() ->
                                       {
                                           user.setStatus(UserStatus.OFFLINE);
                                           this.userRepository.save(user);

                                           log.info("User disconnected successfully and status updated: {}",
                                                    user.getNickName());
                                       })
                             .exceptionally(throwable ->
                                            {
                                                log.error("Error occurred while updating user status: {}",
                                                          user.getNickName(),
                                                          throwable);

                                                return null;
                                            });
        }
        else
        {
            log.error("User does not exist in database: {}", user.getNickName());

            throw new IllegalArgumentException("User does not exist in database");
        }
    }

    /**
     * 检查用户是否存在于数据库中
     *
     * <p>
     * 该方法通过用户 Nickname 来查询数据库，以确定该用户是否已经存在
     * 它使用了Optional的isPresent方法来检查查询结果是否存在，而不是直接处理查询结果对象
     * </p>
     *
     * @param user 需要检查的用户对象
     * @return 如果用户存在返回true，否则返回false
     * @throws IllegalArgumentException 如果用户的Nickname为空
     */
    private boolean isUserExistInDatabase(@NonNull final User user)
    {
        log.debug("Checking if user exists in database: {}", user.getNickName());

        Assert.notNull(user.getNickName(), "User nickname cannot be null");

        return this.userRepository.findById(user.getNickName())
                                  .isPresent();
    }

    public @Nullable Future<List<User>> findConnectedUsers()
    {
        log.info("Finding connected users");

        return CompletableFuture.supplyAsync(() -> this.userRepository.findByStatus(UserStatus.ONLINE));
    }

    public void updateStatusToOnlineAsync(final User user)
    {
        log.info("Updating user status to online: {}", user.getNickName());

        if (this.isUserExistInDatabase(user))
        {
            CompletableFuture.runAsync(() ->
                                       {
                                           user.setStatus(UserStatus.ONLINE);
                                           this.userRepository.save(user);

                                           log.info("User connected successfully and status updated: {}",
                                                    user.getNickName());
                                       })
                             .exceptionally(throwable ->
                                            {
                                                log.error("Error occurred while updating user status: {}",
                                                          user.getNickName(),
                                                          throwable);

                                                return null;
                                            });
        }
        else
        {
            log.error("User does not exist in database: {}", user.getNickName());

            throw new IllegalArgumentException("User does not exist in database");
        }
    }
}
