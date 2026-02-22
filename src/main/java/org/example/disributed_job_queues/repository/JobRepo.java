package org.example.disributed_job_queues.repository;

import jakarta.transaction.Transactional;
import org.example.disributed_job_queues.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface JobRepo extends JpaRepository<Job, UUID> {
    @Modifying
    @Transactional
    @Query(value = """
               UPDATE jobs
               SET status = 'PROCESSING'
               WHERE id = (
                   SELECT id FROM jobs
                   WHERE status = 'PENDING'
                   ORDER BY created_at
                   LIMIT 1
                   FOR UPDATE SKIP LOCKED
               )
               RETURNING *;
    """ , nativeQuery = true)
    Optional<Job> pickNextJob();
}
