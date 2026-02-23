package org.example.disributed_job_queues.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.disributed_job_queues.service.JobService;
import org.example.disributed_job_queues.dto.CreateJobRequest;
import org.example.disributed_job_queues.entity.Job;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping("/create")
    public ResponseEntity<Job> createJob(@Valid @RequestBody CreateJobRequest request) {
        Job job = jobService.createJob(request.payload(),request.priority());
        return ResponseEntity.ok(job);
    }

}
