package org.example.disributed_job_queues.repository;

import jakarta.transaction.Transactional;
import org.example.disributed_job_queues.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface JobRepo extends JpaRepository<Job, UUID> {
    @Transactional
    @Query(value = """
               UPDATE jobs
               SET status = 'PROCESSING' , 
               locked_at = Now() , 
               attempt_count = attempt_count + 1
               WHERE id = (
                   SELECT id FROM jobs
                   WHERE (status = 'PENDING' AND attempt_count < max_attempts AND (next_retry_at IS NULL OR next_retry_at <= Now())) 
                   ORDER BY priority DESC , created_at ASC 
                   LIMIT 1
                   FOR UPDATE SKIP LOCKED
               )
               RETURNING *;
    """ , nativeQuery = true)
    Optional<Job> pickNextJob();

    Optional<Job> findById(UUID id);


    @Transactional
    @Modifying
    @Query(value = """
            UPDATE jobs
            SET status = 'PENDING' ,
            locked_at = NULL 
            WHERE (status = 'PROCESSING' AND locked_at < NOW() - INTERVAL '30 seconds')
    """ , nativeQuery = true
    )
    int recoverStuckJobs();



}
