package org.example.disributed_job_queues.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.disributed_job_queues.entity.Job;
import org.example.disributed_job_queues.entity.JobStatus;
import org.example.disributed_job_queues.repository.JobRepo;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepo jobRepo;

    @Transactional
    public Job createJob(String payload){
        Job job = new Job();
        job.setPayload(payload);
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
        job.ifPresent(value -> value.setStatus(JobStatus.COMPLETED));
    }

    @Transactional
    public void markFailed(UUID id) {
        Optional<Job> job = jobRepo.findById(id);
        job.ifPresent(value -> value.setStatus(JobStatus.FAILED));
    }
}
