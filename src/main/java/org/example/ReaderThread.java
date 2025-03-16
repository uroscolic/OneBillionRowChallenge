package org.example;

import org.example.model.SharedData;
import org.example.model.WeatherMetrics;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentSkipListMap;

import static org.example.utils.WeatherMetricsProcessor.isNumeric;
import static org.example.utils.WeatherMetricsProcessor.processLines;

public class ReaderThread extends Thread {

    private static final int BUFFER_SIZE = 1024 * 1024 * 16;
    private final String filePath;
    private final ConcurrentSkipListMap<Character, WeatherMetrics> stationDataMap;
    private final SharedData sharedData;


    public ReaderThread(ConcurrentSkipListMap<Character, WeatherMetrics> stationDataMap, SharedData sharedData, String filePath) {
        this.stationDataMap = stationDataMap;
        this.sharedData = sharedData;
        this.filePath = filePath;
    }

    @Override
    public void run() {

        if (!filePath.endsWith(".csv") && !filePath.endsWith(".txt")) {
            System.err.println("Error: File must be .csv or .txt");
            return;
        }

        synchronized (sharedData.monitor) {
            sharedData.numberOfThreadsModifying.incrementAndGet();
        }

        System.out.println("Reading file: " + filePath);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath))) {

            byte[] buffer = new byte[BUFFER_SIZE];
            StringBuilder leftover = new StringBuilder();
            int bytesRead;

            if (filePath.endsWith(".csv")) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {

                    String line = reader.readLine();
                    String[] headers = line.split(";");

                    if (headers.length >= 2 && !isNumeric(headers[1]))
                        bis.skip(line.getBytes(StandardCharsets.UTF_8).length);
                }
            }

            while ((bytesRead = bis.read(buffer)) != -1) {

                String readChunk = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                String data = leftover + readChunk;
                int lastNewline = data.lastIndexOf("\n");

                if (lastNewline == -1)
                    leftover = new StringBuilder(data);

                else {

                    String completeLines = data.substring(0, lastNewline);

                    processLines(completeLines, stationDataMap);


                    leftover = new StringBuilder(data.substring(lastNewline + 1));

                }
            }

            if (leftover.length() > 0)
                processLines(leftover.toString(), stationDataMap);

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } finally {
            synchronized (sharedData.monitor) {
                sharedData.numberOfThreadsModifying.decrementAndGet();
                if (sharedData.numberOfThreadsModifying.get() <= 0)
                    sharedData.monitor.notifyAll();
                System.out.println("Finished reading file: " + filePath);
            }
        }


    }


}