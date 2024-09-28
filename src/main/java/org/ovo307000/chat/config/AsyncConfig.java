package org.ovo307000.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 配置异步任务执行器
 */
@Configuration
public class AsyncConfig
{
    /**
     * 配置任务执行器
     *
     * @return 配置好的线程池任务执行器实例
     */
    @Bean
    public ThreadPoolTaskExecutor taskExecutor()
    {
        var executor = new ThreadPoolTaskExecutor();

        // 设置核心线程池大小
        executor.setCorePoolSize(10);
        // 设置最大线程池大小
        executor.setMaxPoolSize(20);
        // 设置队列容量
        executor.setQueueCapacity(10);
        // 设置线程名称前缀
        executor.setThreadNamePrefix("chat-");
        // 初始化线程池
        executor.initialize();

        return executor;
    }
}
