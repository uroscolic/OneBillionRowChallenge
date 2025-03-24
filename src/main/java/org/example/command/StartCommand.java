package org.example.command;

import org.example.JobManager;
import org.example.argument.StartArguments;
import org.example.model.SharedData;
import org.example.model.WeatherMetrics;
import org.example.utils.JsonUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class StartCommand extends Command {

    private final StartArguments startArguments;
    private final JobManager jobManager;
    private final ConcurrentSkipListMap<Character, WeatherMetrics> stationDataMap;
    private final SharedData sharedData;


    public StartCommand(StartArguments startArguments, JobManager jobManager, ConcurrentSkipListMap<Character, WeatherMetrics> stationDataMap, SharedData sharedData) {
        setCommandType(CommandType.START);
        this.startArguments = startArguments;
        this.jobManager = jobManager;
        this.stationDataMap = stationDataMap;
        this.sharedData = sharedData;
    }


    @Override
    public String call() {

        Map<String, Command> savedJobs;

        if(startArguments.isStartPendingJobs())
            savedJobs = JsonUtil.loadSavedJobsFromJson("src/main/resources/config/load_config.json");
        else {
            clearFile();
            return "Ignoring pending jobs.";
        }


        if(savedJobs == null || savedJobs.isEmpty())
            System.out.println("No pending jobs to start.");
        else
        {
            System.out.println("Loading jobs...");
            for(var job : savedJobs.entrySet())
            {
                if(job.getValue() != null)
                {
                    job.getValue().setStatus(Status.PENDING);

                    if(job.getValue() instanceof ScanCommand scanCommand)
                    {
                        scanCommand.setSharedData(this.sharedData);
                        jobManager.addTask(scanCommand);
                    }
                    else if(job.getValue() instanceof MapCommand mapCommand)
                    {
                        mapCommand.setStationDataMap(this.stationDataMap);
                        mapCommand.setSharedData(this.sharedData);
                        jobManager.addTask(mapCommand);

                    }
                    else if(job.getValue() instanceof ExportMapCommand exportMapCommand)
                    {
                        exportMapCommand.setStationDataMap(this.stationDataMap);
                        exportMapCommand.setSharedData(this.sharedData);
                        jobManager.addTask(exportMapCommand);

                    }

                }

            }
            clearFile();
            return "Starting jobs...";
        }

        return "No pending jobs to start.";
    }

    private void clearFile() {
        try (FileWriter fileWriter = new FileWriter("src/main/resources/config/load_config.json", false)) {

            fileWriter.write("");
        } catch (IOException e) {
            System.err.println("Error clearing file: " + e.getMessage());
        }
    }
}
