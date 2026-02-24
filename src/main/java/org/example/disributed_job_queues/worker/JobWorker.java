package org.example.disributed_job_queues.worker;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.example.disributed_job_queues.entity.Job;
import org.example.disributed_job_queues.service.JobService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Component
public class JobWorker {

    private final JobService jobService;
    private final Executor jobExecutor;
    private final MeterRegistry meterRegistry;

    private volatile boolean shuttingDown = false;

    public JobWorker(JobService jobService,
                     @Qualifier("taskExecutor") Executor jobExecutor , MeterRegistry meterRegistry) {
        this.jobService = jobService;
        this.jobExecutor = jobExecutor;
        this.meterRegistry = meterRegistry;
    }

    @PreDestroy
    public void onShutDown() {
        System.out.println("Shutting down...");
        shuttingDown = true;
    }

    @Scheduled(fixedDelay = 18000)
    public void pollAndProcess() {

        if(shuttingDown) {
           return;
        }

        Job job = jobService.pickJob();
        if(job == null) {
            return;
        }

        jobExecutor.execute(()-> process(job));

    }

    private void process(Job job){
        long startTime = System.currentTimeMillis();
        try{
            System.out.println(Thread.currentThread().getName() + " processing job: " + job.getId());
            Thread.sleep(20000);

            jobService.markCompleted(job.getId());
        } catch (RuntimeException e) {
            jobService.markFailed(job.getId());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            long duration = System.currentTimeMillis() - startTime;
            meterRegistry.timer("jobs.processing.time").record(duration, TimeUnit.MILLISECONDS);
        }

    }

    @Scheduled(fixedDelay = 12000)
    public void pollAndRecover(){
        jobService.pollAndRecover();
    }

}
