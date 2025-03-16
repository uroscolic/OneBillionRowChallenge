package org.example.utils;

import org.example.model.WeatherMetrics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class Printer {

    private final ConcurrentSkipListMap<Character, WeatherMetrics> stationDataMap;

    public Printer(ConcurrentSkipListMap<Character, WeatherMetrics> stationDataMap) {
        this.stationDataMap = stationDataMap;
    }

    public void printMapToTxtFile(String filePath) {

        Path path = Paths.get(filePath);

        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            System.err.println("Error creating directories: " + e.getMessage());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            StringBuilder line = new StringBuilder();
            final int groupSize = 2;
            int counter = 0;

            var entries = stationDataMap.entrySet().toArray(new java.util.Map.Entry[0]);

            for (int i = 0; i < entries.length; i++) {

                counter = getCounter(line, counter, entries, i);

                if (counter == groupSize) {
                    writer.write(line.toString());
                    writer.newLine();
                    line.setLength(0);
                    counter = 0;
                }
            }

            if (line.length() > 0) {
                line.delete(line.length() - 3, line.length());
                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public void printMapToCSVFile(String filePath, String type) {

        Path path = Paths.get(filePath);
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            System.err.println("Error creating directories: " + e.getMessage());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {

            if(!Files.exists(path) || Files.size(path) == 0)
                writer.write("Letter, Station count, Sum");

            writer.newLine();

            var entries = stationDataMap.entrySet().toArray(new java.util.Map.Entry[0]);
            for (int i = 0; i < entries.length; i++) {

                var entry = entries[i];
                Character key = (Character) entry.getKey();
                WeatherMetrics weatherMetrics = (WeatherMetrics) entry.getValue();

                writer.write(key + " " + weatherMetrics.getCount() + " " + String.format("%.2f", Double.parseDouble(String.valueOf(weatherMetrics.getSum()))));
                writer.newLine();

            }
            writer.newLine();
            writer.write("--------------------");
            writer.write("End of " + type);
            writer.write("--------------------");
            writer.newLine();

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }


    }

    public void printMapToConsole() {
        StringBuilder line = new StringBuilder();
        final int groupSize = 2;
        int counter = 0;

        var entries = stationDataMap.entrySet().toArray(new java.util.Map.Entry[0]);

        for (int i = 0; i < entries.length; i++) {

            counter = getCounter(line, counter, entries, i);

            if (counter == groupSize) {
                System.out.println(line);
                line.setLength(0);
                counter = 0;
            }
        }

        if (line.length() > 0) {

            line.delete(line.length() - 3, line.length());
            System.out.println(line);
        }
    }

    private int getCounter(StringBuilder line, int counter, Map.Entry[] entries, int i) {
        var entry = entries[i];

        Character key = (Character) entry.getKey();
        WeatherMetrics weatherMetrics = (WeatherMetrics) entry.getValue();


        line.append(key).append(": ")
                .append(weatherMetrics.getCount()).append(" - ")
                .append(String.format("%.2f", Double.parseDouble(String.valueOf(weatherMetrics.getSum()))));

        counter++;

        if (counter % 2 == 1)
            line.append(" | ");

        return counter;
    }


}
