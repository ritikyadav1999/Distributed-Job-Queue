package org.example.disributed_job_queues.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.disributed_job_queues.entity.JobPriority;

public record CreateJobRequest(
        @NotNull
        String payload,

        JobPriority priority

) {
}
