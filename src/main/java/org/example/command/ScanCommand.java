package org.example.command;

import org.example.argument.ScanArguments;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ScanCommand implements Runnable {
    private final String directoryPath;
    private final ScanArguments scanArguments;

    public ScanCommand(String directoryPath, ScanArguments scanArguments) {
        this.directoryPath = directoryPath;
        this.scanArguments = scanArguments;
    }

    @Override
    public void run() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(scanArguments.getOutputFile()))) {
            Files.list(Paths.get(directoryPath))
                    .filter(path -> path.toString().endsWith(".txt") || path.toString().endsWith(".csv"))
                    .forEach(path -> processFile(path, writer));
            System.out.println("SCAN job '" + scanArguments.getJobName() + "' completed. Results saved to " + scanArguments.getOutputFile());
        } catch (IOException e) {
            System.err.println("Error during SCAN job '" + scanArguments.getJobName() + "': " + e.getMessage());
        }
    }

    private void processFile(Path filePath, BufferedWriter writer) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(String.valueOf(scanArguments.getLetter()))) {
                    String[] parts = line.split(";");
                    if (parts.length == 2) {
                        String station = parts[0];
                        double temperature = Double.parseDouble(parts[1]);
                        if (temperature >= scanArguments.getMinTemperature() && temperature <= scanArguments.getMaxTemperature()) {
                            synchronized (writer) {
                                writer.write(line);
                                writer.newLine();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file '" + filePath + "': " + e.getMessage());
        }
    }
}