package org.example.argument;

import lombok.Data;

@Data
public class ShutdownArguments implements Arguments {

    private boolean saveJobs;

    public ShutdownArguments(boolean saveJobs) {
        this.saveJobs = saveJobs;
    }

    @Override
    public String toString() {
        return "ShutdownArguments | " +
                "saveJobs = " + saveJobs;
    }
}
