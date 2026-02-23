package org.example.disributed_job_queues;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DisributedJobQueuesApplication {

    public static void main(String[] args) {
        SpringApplication.run(DisributedJobQueuesApplication.class, args);
    }
}
 