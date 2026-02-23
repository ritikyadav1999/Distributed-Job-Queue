package org.example.disributed_job_queues.worker;

import lombok.RequiredArgsConstructor;
import org.example.disributed_job_queues.entity.Job;
import org.example.disributed_job_queues.service.JobService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.Executor;

@Component
public class JobWorker {

    private final JobService jobService;
    private final Executor jobExecutor;

    public JobWorker(JobService jobService,
                     @Qualifier("taskExecutor") Executor jobExecutor) {
        this.jobService = jobService;
        this.jobExecutor = jobExecutor;
    }

    @Scheduled(fixedDelay = 18000)
    public void pollAndProcess() {
        Job job = jobService.pickJob();
        if(job == null) {
            return;
        }

        jobExecutor.execute(()-> process(job));

    }

    private void process(Job job){
        try{
            System.out.println(Thread.currentThread().getName() + " processing job: " + job.getId());
            Thread.sleep(30000);

            jobService.markCompleted(job.getId());
        } catch (RuntimeException e) {
            jobService.markFailed(job.getId());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Scheduled(fixedDelay = 12000)
    public void pollAndRecover(){
        jobService.pollAndRecover();
    }

}
