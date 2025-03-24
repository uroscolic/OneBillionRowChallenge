package org.example;

import org.example.argument.*;
import org.example.argumentParser.ArgumentParser;
import org.example.command.*;
import org.example.model.SharedData;
import org.example.model.WeatherMetrics;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ConcurrentSkipListMap;

public class CLIThread implements Runnable {

    private JobManager jobManager;
    private DirectoryScanner directoryScanner;
    private ReportThread reportThread;
    private String directoryPath;
    private ConcurrentSkipListMap<Character, WeatherMetrics> stationDataMap;
    private SharedData sharedData;
    private boolean isApplicationStarted = false;

    public CLIThread(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public void run() {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            String input = scanner.nextLine().trim();

            if (input.isEmpty())
                continue;

            String[] commandParts = input.split("\\s+");

            try {

                CommandType command = CommandType.valueOf(commandParts[0].toUpperCase());

                String[] argumentsWithoutCommand = Arrays.stream(commandParts)
                        .skip(1)
                        .toArray(String[]::new);

                try {

                    Arguments arguments = ArgumentParser.parseArguments(argumentsWithoutCommand, command);

                    if (command == CommandType.START) {

                        if (isApplicationStarted) {
                            System.err.println("Application already started.");
                            continue;
                        }

                        System.out.println("Starting application...");

                        isApplicationStarted = true;

                        this.sharedData = new SharedData();
                        this.stationDataMap = sharedData.stationDataMap;
                        this.jobManager = new JobManager();
                        this.directoryScanner = new DirectoryScanner(directoryPath, sharedData);
                        reportThread = new ReportThread(stationDataMap, sharedData);
                        reportThread.start();
                        directoryScanner.start();

                        StartArguments startArguments = (StartArguments) arguments;
                        StartCommand startCommand = new StartCommand(startArguments, jobManager, stationDataMap, sharedData);
                        startCommand.call();
                    }

                    if (!isApplicationStarted) {
                        System.err.println("Application not started.");
                        continue;
                    }

                    if (command == CommandType.MAP) {
                        MapCommand mapCommand = new MapCommand(stationDataMap, sharedData);
                        jobManager.addTask(mapCommand);
                    } else if (command == CommandType.EXPORTMAP) {
                        ExportMapCommand exportMapCommand = new ExportMapCommand(stationDataMap, sharedData);
                        jobManager.addTask(exportMapCommand);
                    } else if (command == CommandType.SCAN) {

                        File directory = new File(directoryPath);
                        File[] files = directory.listFiles();

                        if (arguments instanceof ScanArguments scanArguments) {
                            if (files != null) {
                                for (File file : files) {
                                    if (file.isFile() && (file.getName().endsWith(".csv") || file.getName().endsWith(".txt"))) {

                                        ScanCommand scanCommand = new ScanCommand(file.getAbsolutePath(), scanArguments, sharedData);

                                        if (jobManager.isJobNameTaken(scanCommand.getJobName())) {
                                            System.err.println("Job name already taken: " + scanArguments.getJobName());
                                            break;
                                        }

                                        jobManager.addTask(scanCommand);
                                    }
                                }
                            }
                        }

                    } else if (command == CommandType.STATUS) {

                        StatusArguments scanArguments = (StatusArguments) arguments;
                        StatusCommand statusCommand = new StatusCommand(scanArguments, jobManager.getJobStatusMap());

                        statusCommand.call();

                    } else if (command == CommandType.SHUTDOWN) {

                        ShutdownArguments shutdownArguments = (ShutdownArguments) arguments;
                        ShutdownCommand shutdownCommand = new ShutdownCommand(shutdownArguments, jobManager, directoryScanner, reportThread);
                        shutdownCommand.call();

                        break;
                    }


                } catch (IllegalArgumentException e) {
                    System.err.println("Error: " + e.getMessage());
                }

            } catch (IllegalArgumentException e) {
                System.err.println("Unknown command: " + commandParts[0]);
            }
        }

        scanner.close();
    }


}
