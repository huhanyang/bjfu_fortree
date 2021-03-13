package com.bjfu.fortree.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 异步任务配置类
 * @author warthog
 */
@Configuration
public class AsyncConfig {

    private static final int MAX_POOL_SIZE = 20;

    private static final int CORE_POOL_SIZE = 10;

    @Bean
    public AsyncTaskExecutor asyncExportTaskExecutor() {
        ThreadPoolTaskExecutor asyncTaskExecutor = new ThreadPoolTaskExecutor();
        asyncTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        asyncTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        asyncTaskExecutor.setThreadNamePrefix("async-export-task-thread-pool-");
        asyncTaskExecutor.initialize();
        return asyncTaskExecutor;
    }

}
