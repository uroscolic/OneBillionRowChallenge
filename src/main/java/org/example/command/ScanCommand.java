package org.example.command;

import org.example.argument.ScanArguments;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class ScanCommand implements Callable<String> {

    private final String filePath;
    private final ScanArguments scanArguments;
    private static final AtomicInteger counter = new AtomicInteger(0);

    public ScanCommand(String filePath, ScanArguments scanArguments) {
        this.filePath = filePath;
        this.scanArguments = scanArguments;
    }

    @Override
    public String call() {

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(scanArguments.getOutputFile(), true))) {

            String currentThreadName = Thread.currentThread().getName();
            System.out.println("Thread: " + currentThreadName + " is processing file: " + filePath);


            char letter = scanArguments.getLetter();
            double minTemp = scanArguments.getMinTemperature();
            double maxTemp = scanArguments.getMaxTemperature();

            String line;

            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {

                if (isFirstLine && filePath.endsWith(".csv")) {
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split(";");

                if (parts.length == 2) {

                    String stationName = parts[0].trim();
                    double temperature = Double.parseDouble(parts[1].trim());


                    if (stationName.charAt(0) == letter && temperature >= minTemp && temperature <= maxTemp) {
                        synchronized (ScanCommand.class) {

//                            if (scanArguments.getOutputFile().endsWith(".csv") && counter.get() == 0
//                                    && (!Files.exists(Paths.get(scanArguments.getOutputFile()))
//                                    || Files.size(Paths.get(scanArguments.getOutputFile())) == 0)) {
//                                counter.incrementAndGet();
//                                writer.write("Station;Temperature");
//                                writer.newLine();
//                            }
//                            else{
//                                System.out.println("ne postoji " + !Files.exists(Paths.get(scanArguments.getOutputFile())));
//                                System.out.println("velicina " + Files.size(Paths.get(scanArguments.getOutputFile())));
//                                System.out.println("counter " + counter.get());
//                            }


                            writer.write(line);
                            writer.newLine();
                        }
                    }

                }
            }

            return "Completed: " + filePath;

        } catch (IOException e) {
            System.err.println("Error processing file: " + filePath + " - " + e.getMessage());
            return "Error: " + filePath;
        }
    }
}