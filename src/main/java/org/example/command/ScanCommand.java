package org.example.command;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.example.JobManager;
import org.example.argument.ScanArguments;
import org.example.model.SharedData;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@Setter
public class ScanCommand extends Command {

    private String filePath;
    private ScanArguments scanArguments;
    @JsonIgnore
    private SharedData sharedData;

    public ScanCommand() {
        setCommandType(CommandType.SCAN);
        setJobName("scan-" + JobManager.counter.get());
    }

    public ScanCommand(String filePath, ScanArguments scanArguments, SharedData sharedData) {
        setCommandType(CommandType.SCAN);
        this.filePath = filePath;
        this.scanArguments = scanArguments;
        this.sharedData = sharedData;
        Path path = Paths.get(filePath);
        setJobName(scanArguments.getJobName() + "-" + new File(path.getFileName().toString()).getName());
    }

    @Override
    public String call() {

        setStatus(Status.RUNNING);
        Path path = Paths.get(filePath);

        try {
            synchronized (sharedData.files.get(path.getFileName().toString())) {

                while (sharedData.files.get(path.getFileName().toString()).get() > 0) {
                    try {
                        System.out.println("Waiting: " + path.getFileName().toString());
                        sharedData.files.get(path.getFileName().toString()).wait();
                    } catch (InterruptedException e) {
                        return "Interrupted: " + filePath;
                    }
                }
                sharedData.files.get(path.getFileName().toString()).incrementAndGet();
            }
        } catch (Exception e) {
            System.err.println("Error " + e.getMessage());
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(scanArguments.getOutputFile(), true))) {

            System.out.println("Job: " + getJobName() + " is processing file: " + path.getFileName().toString());
            long startTime = System.currentTimeMillis();

            String line;

            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {

                if (Thread.interrupted())
                    return "Interrupted: " + filePath;

                if (isFirstLine && filePath.endsWith(".csv")) {
                    isFirstLine = false;
                    continue;
                }

                char letter = scanArguments.getLetter();

                if (Character.toLowerCase(line.charAt(0)) != Character.toLowerCase(letter))
                    continue;

                double minTemp = scanArguments.getMinTemperature();
                double maxTemp = scanArguments.getMaxTemperature();

                String[] parts = line.split(";");

                if (parts.length != 2)
                    continue;

                double temperature = Double.parseDouble(parts[1].trim());

                if (temperature >= minTemp && temperature <= maxTemp) {

                    writer.write(line);
                    writer.newLine();
                }


            }

            synchronized (sharedData.files.get(path.getFileName().toString())) {

                sharedData.files.get(path.getFileName().toString()).decrementAndGet();

                if (sharedData.files.get(path.getFileName().toString()).get() <= 0) {
                    try {
                        sharedData.files.get(path.getFileName().toString()).notifyAll();
                    } catch (Exception e) {
                        System.err.println("Error " + e.getMessage());
                    }
                }
                setStatus(Status.COMPLETED);
                long finishedTime = System.currentTimeMillis();
                System.out.println("Job: " + getJobName() + " completed processing file: " + path.getFileName().toString() + " in: " + (finishedTime - startTime) / 1000 + "s");
            }

            return "Completed: " + filePath;

        } catch (IOException e) {
            System.err.println("Error processing file: " + path.getFileName().toString() + " - " + e.getMessage());
            return "Error: " + filePath;
        }
    }

    @Override
    public String toString() {
        return "ScanCommand | " +
                "filePath = '" + filePath + '\'' +
                ", scanArguments = " + scanArguments;
    }

}