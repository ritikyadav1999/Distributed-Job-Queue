package org.example.disributed_job_queues.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "dead_jobs",
        indexes = {
                @Index(name = "idx_dead_failed_at",columnList = "failed_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class DeadJob {


    @Id
    private UUID id;

    @NotNull
    @Column(nullable = false,columnDefinition = "TEXT")
    private String payload;

    @Column(name = "failure_reason")
    private String failureReason;

    //original job creation time
    @Column(nullable = false ,name = "created_at")
    private Instant createdAt;

    @CreationTimestamp
    @Column(nullable = false,name = "failed_at")
    private Instant failedAt;

    @NotNull
    @Column(nullable = false,name = "max_attempts")
    private int maxAttempts;

    @NotNull
    @Column(nullable = false,name = "attempt_count")
    private int attemptCount;


}
