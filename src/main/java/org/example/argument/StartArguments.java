package org.example.argument;

import lombok.Data;

@Data
public class StartArguments implements Arguments {

    private boolean startPendingJobs;

    public StartArguments(boolean startPendingJobs) {
        this.startPendingJobs = startPendingJobs;
    }

    @Override
    public String toString() {
        return "StartArguments | " +
                "startPendingJobs = '" + startPendingJobs + '\'';
    }

}
