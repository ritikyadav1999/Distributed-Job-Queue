package org.example.disributed_job_queues.worker;

import lombok.RequiredArgsConstructor;
import org.example.disributed_job_queues.entity.Job;
import org.example.disributed_job_queues.service.JobService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JobWorker {

    private final JobService jobService;

    @Scheduled(fixedDelay = 18000)
    public void pollAndProcess() {
        Job job = jobService.pickJob();
        if(job == null) {
            return;
        }
        try {
            // simulate processing
            Thread.sleep(30000);

            jobService.markCompleted(job.getId());
        } catch (Exception e) {
            jobService.markFailed(job.getId());
        }
    }


}
