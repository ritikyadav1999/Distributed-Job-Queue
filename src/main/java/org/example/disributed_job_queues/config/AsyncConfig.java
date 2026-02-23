package org.example.disributed_job_queues.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class AsyncConfig {

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);  //minimum threads
        executor.setMaxPoolSize(10);  // max thread
        executor.setQueueCapacity(50); //waiting task
        executor.setThreadNamePrefix("job-worker-");
        executor.initialize();
        return executor;
    }
}