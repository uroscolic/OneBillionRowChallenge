package org.example.command;

import org.example.argument.StatusArguments;

import java.util.concurrent.ConcurrentHashMap;

public class StatusCommand extends Command {

    private final ConcurrentHashMap<String, Command> jobStatusMap;
    private StatusArguments statusArguments;


    public StatusCommand(StatusArguments statusArguments, ConcurrentHashMap<String, Command> jobStatusMap) {
        setCommandType(CommandType.STATUS);
        this.statusArguments = statusArguments;
        this.jobStatusMap = jobStatusMap;
    }

    @Override
    public String call() {

        String jobName = statusArguments.getJobName();
        boolean found = false;
        for (String key : jobStatusMap.keySet()) {
            if (Thread.interrupted())
                return "Interrupted: " + jobName;

            if (key.startsWith(jobName + "-")) {
                Command command = jobStatusMap.get(key);
                System.out.println("Job: " + command.getJobName() + ", status: " + command.getStatus());
                found = true;
            }
        }
        if (found)
            return "Status of job: " + jobName + " printed";

        System.out.println("No such job: " + jobName);
        return "No such job: " + jobName;
    }
}
