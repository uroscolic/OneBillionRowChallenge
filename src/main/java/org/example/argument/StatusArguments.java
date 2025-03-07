package org.example.argument;

public class StatusArguments implements Arguments {

    private String jobName;

    public StatusArguments(String jobName) {
        if (jobName == null || jobName.isEmpty())
            throw new IllegalArgumentException("Job name is required.");

        this.jobName = jobName;
    }

    @Override
    public String toString() {
        return "StatusArguments | " +
                "jobName = '" + jobName + '\'';
    }
}
