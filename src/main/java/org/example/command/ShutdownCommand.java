package org.example.command;

import org.example.DirectoryScanner;
import org.example.JobManager;
import org.example.ReportThread;
import org.example.argument.ShutdownArguments;
import org.example.utils.JsonUtil;

import java.util.concurrent.ConcurrentHashMap;

public class ShutdownCommand extends Command {

    private final ShutdownArguments shutdownArguments;
    private final JobManager jobManager;
    private final DirectoryScanner directoryScanner;
    private final ReportThread reportThread;

    public ShutdownCommand(ShutdownArguments shutdownArguments, JobManager jobManager, DirectoryScanner directoryScanner, ReportThread reportThread) {
        setCommandType(CommandType.SHUTDOWN);
        this.shutdownArguments = shutdownArguments;
        this.jobManager = jobManager;
        this.directoryScanner = directoryScanner;
        this.reportThread = reportThread;
    }

    @Override
    public String call() {

        if (shutdownArguments.isSaveJobs())
            saveJobs();

        System.out.println("Shutting down...");
        jobManager.stop();
        directoryScanner.stopScanner();
        reportThread.stopReport();

        return "Shutting down...";
    }

    private void saveJobs() {


        ConcurrentHashMap<String, Command> jobs = jobManager.getJobStatusMap();
        ConcurrentHashMap<String, Command> unfinishedJobs = new ConcurrentHashMap<>();
        for(var job : jobs.keySet())
        {

            if(jobs.get(job) instanceof Command c && c.getStatus() != Status.COMPLETED)
            {
                System.out.println("Saving job: " + c.getJobName());
                unfinishedJobs.put(c.getJobName(), c);
            }
        }

        if(!unfinishedJobs.isEmpty())
            JsonUtil.exportSavedJobsToJson(unfinishedJobs, "src/main/resources/config/load_config.json");

    }

}
