package org.example.job;

import lombok.Data;

@Data
public class Job {

    private String jobName;
    private Status status = Status.PENDING;

    @Override
    public String toString() {
        return "Job | " +
                "jobName = '" + jobName + '\'' +
                ", status = " + status;
    }
}
