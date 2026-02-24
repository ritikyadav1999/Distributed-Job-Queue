# 🚀 Distributed Job Queue System (Spring Boot)

A production-grade, distributed, crash-resilient job processing system built with **Spring Boot + PostgreSQL**.

This project demonstrates how to design and implement a fault-tolerant job queue using database-level atomic operations, lease-based locking, retry with backoff, multi-instance scaling, and observability.

---

# 📌 Key Features

## ✅ Atomic Job Claiming
- Uses `UPDATE ... RETURNING`
- Uses `FOR UPDATE SKIP LOCKED`
- Prevents duplicate job execution across multiple instances

## ✅ Lease-Based Locking
- Uses `locked_at` timestamp
- Jobs auto-recover if worker crashes
- Safe multi-node coordination

## ✅ Retry with Backoff
- Linear retry delay (`attemptCount * delay`)
- Max attempts protection
- Prevents infinite retry loops

## ✅ Dead Letter Queue (DLQ)
- Permanently failed jobs moved to `dead_jobs` table
- Stores failure reason and execution snapshot
- Keeps main queue clean

## ✅ Multi-Instance Support
- Run multiple application instances
- Safe concurrent processing
- Distributed-safe locking

## ✅ Thread Pool Execution
- `ThreadPoolTaskExecutor`
- Configurable concurrency
- Backpressure using `CallerRunsPolicy`

## ✅ Graceful Shutdown
- Stops polling on shutdown
- Waits for running jobs to complete
- Prevents orphan locks during controlled termination

## ✅ Crash Recovery
- Recover stuck jobs after lease timeout
- Automatic re-queuing of abandoned jobs

## ✅ Composite Index Optimization

```sql
CREATE INDEX idx_job_pickup
ON jobs (status, next_retry_at, priority DESC, created_at ASC);
```

## ✅ Observability & Metrics
- Spring Boot Actuator
- Micrometer metrics
- Job completion count
- Retry count
- Dead job count
- Processing time metrics

---

# 🏗 System Architecture

```
Scheduler (single thread)
        ↓
Atomic DB Job Claim
        ↓
ThreadPoolTaskExecutor
        ↓
Worker Threads
        ↓
Mark Completed / Retry / Move to DLQ
```

---

# ⚙️ Tech Stack

- Java 17+
- Spring Boot
- Spring Data JPA
- PostgreSQL
- HikariCP (Connection Pool)
- Micrometer
- Spring Actuator

---

# 🧠 Core Concepts Implemented

- Atomic DB operations
- Transaction isolation
- Lost update prevention
- Lease-based distributed locking
- Backpressure handling
- Thread pool tuning
- DB connection pool bottleneck analysis
- Horizontal scaling
- Graceful degradation
- Observability engineering

---

# 🗂 Database Schema

## jobs table

| Column | Description |
|--------|------------|
| id | UUID Primary Key |
| payload | Job data |
| status | PENDING / PROCESSING |
| attempt_count | Number of attempts |
| max_attempts | Retry limit |
| locked_at | Lease timestamp |
| next_retry_at | Retry scheduling |
| priority | Job priority |
| created_at | Creation time |
| updated_at | Last update time |

---

## dead_jobs table

| Column | Description |
|--------|------------|
| id | Original job ID |
| payload | Job data |
| failure_reason | Failure cause |
| created_at | Original creation time |
| failed_at | Time moved to DLQ |
| attempt_count | Total attempts |
| max_attempts | Configured max attempts |

---

# ▶️ Running Multiple Instances

Instance 1:
```
--server.port=8080
```

Instance 2:
```
--server.port=8081
```

Both connect to same PostgreSQL database.

---

# 📊 Metrics Endpoints

List all metrics:
```
http://localhost:8080/actuator/metrics
```

Examples:
```
/actuator/metrics/jobs.completed
/actuator/metrics/jobs.dead
/actuator/metrics/jobs.retried
/actuator/metrics/jobs.processing.time
```

---

# 🔥 Failure Handling Strategy

| Scenario | Handling |
|----------|----------|
| Worker crash | Lease timeout recovery |
| Max attempts exceeded | Move to DLQ |
| Executor overload | CallerRunsPolicy |
| Graceful shutdown | Stop polling + wait for tasks |
| DB contention | Composite index + pool tuning |

---

# 🧪 Test Scenarios

- Insert multiple jobs
- Run 2+ instances
- Kill one instance mid-processing
- Observe recovery after lease timeout
- Simulate overload with limited thread pool
- Verify DLQ behavior

---

# 🎯 Learning Outcomes

This project demonstrates how to build:

- A distributed-safe job processing engine
- A crash-resilient retry system
- A horizontally scalable background worker architecture
- A production-ready concurrency model

---

# 📌 Why This Project Matters

Most engineers use Kafka or RabbitMQ without understanding internal mechanics.

This project shows how distributed job processing works under the hood using database primitives and concurrency control.

---

# 🚀 Future Improvements

- Prometheus + Grafana integration
- Rate limiting
- Idempotency keys
- Kafka-backed implementation
- Sharding for massive scale
- Exactly-once semantics

---

# 🏁 Final Note

This is not a toy queue.

It is a distributed, crash-safe, production-grade job processing system implemented from scratch.
