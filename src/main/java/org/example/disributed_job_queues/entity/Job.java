package org.example.disributed_job_queues.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(
        name = "jobs",
        indexes = {
                @Index(
                        name = "idx_job_pickup",
                        columnList = "status, next_retry_at , priority , created-at    "
                )
        }

)
public class Job {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    @Column(nullable = false,columnDefinition = "Text")
    private String payload;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    private Instant lockedAt;

    private int attemptCount;
    private int maxAttempts;
    private Instant nextRetryAt;

    private int priority;

    @PrePersist
    public void prePersist() {
        this.attemptCount = 0;
        this.maxAttempts = 3;
        this.nextRetryAt = null;
  ;  }



}
