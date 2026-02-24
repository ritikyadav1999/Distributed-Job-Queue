package org.example.disributed_job_queues.repository;

import org.example.disributed_job_queues.entity.DeadJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeadJobRepo extends JpaRepository<DeadJob, UUID> {
}
