package org.example.disributed_job_queues.service;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.disributed_job_queues.entity.DeadJob;
import org.example.disributed_job_queues.entity.Job;
import org.example.disributed_job_queues.entity.JobPriority;
import org.example.disributed_job_queues.entity.JobStatus;
import org.example.disributed_job_queues.repository.DeadJobRepo;
import org.example.disributed_job_queues.repository.JobRepo;
import org.example.disributed_job_queues.utils.JobPriorityMapping;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepo jobRepo;
    private final DeadJobRepo deadJobRepo;
    private final MeterRegistry meterRegistry;

    @Transactional
    public Job createJob(String payload, JobPriority prority){
        Job job = new Job();
        job.setPayload(payload);
        job.setPriority(JobPriorityMapping.getPriorityValue(prority));
        job.setStatus(JobStatus.PENDING);
        return jobRepo.save(job);
    }


    public Job pickJob() {
        Optional<Job> job = jobRepo.pickNextJob();
        return job.orElse(null);
    }

    @Transactional
    public void markCompleted(UUID id) {
        Optional<Job> job = jobRepo.findById(id);
        if(job.isPresent()){
            job.get().setStatus(JobStatus.COMPLETED);
            job.get().setLockedAt(null);
            meterRegistry.counter("jobs.completed").increment();
        }

    }

    @Transactional
    public void markFailed(UUID id) {
        Optional<Job> job = jobRepo.findById(id);
        if(job.isPresent()) {
            long delaySeconds = (long) Math.pow(2,job.get().getAttemptCount()) *30;
            if(job.get().getAttemptCount() < job.get().getMaxAttempts()) {
                job.get().setStatus(JobStatus.PENDING);
                job.get().setNextRetryAt(Instant.now().plusSeconds(delaySeconds));
                job.get().setLockedAt(null);

                meterRegistry.counter("jobs.reteried").increment();
            }
            else  {
                DeadJob deadJob = new DeadJob();
                deadJob.setId(job.get().getId());
                deadJob.setAttemptCount(job.get().getAttemptCount());
                deadJob.setPayload(job.get().getPayload());
                deadJob.setCreatedAt(job.get().getCreatedAt());
                deadJob.setMaxAttempts(job.get().getMaxAttempts());

                deadJobRepo.save(deadJob);
                jobRepo.delete(job.get());
                meterRegistry.counter("jobs.dead").increment();
            }
        }
        else  {
            throw new RuntimeException("Job with id " + id + " not found");
        }

    }

    @Transactional
    public void pollAndRecover() {
        int res = jobRepo.recoverStuckJobs();
    }
}
