package org.example.disributed_job_queues.utils;

import org.example.disributed_job_queues.entity.Job;
import org.example.disributed_job_queues.entity.JobPriority;

public class JobPriorityMapping {

    public static int getPriorityValue(JobPriority jobPriority) {
        int value = 0;
        switch (jobPriority) {
            case HIGH -> value = 10;
            case MEDIUM -> value = 5;
            case LOW -> value = 3;
        }
        return value;
    }

}
