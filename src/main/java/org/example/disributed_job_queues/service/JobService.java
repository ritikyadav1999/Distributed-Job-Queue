package org.example.disributed_job_queues.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.disributed_job_queues.entity.Job;
import org.example.disributed_job_queues.entity.JobPriority;
import org.example.disributed_job_queues.entity.JobStatus;
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
            }
            else  {
                job.get().setStatus(JobStatus.FAILED);
                job.get().setLockedAt(null);
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
