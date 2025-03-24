package org.example.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.command.Command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class JsonUtil {

    public static void exportSavedJobsToJson(Map<String, Command> savedJobs, String filePath) {

        Path path = Paths.get(filePath);

        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            System.err.println("Error creating directories: " + e.getMessage());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            String json = objectMapper.writeValueAsString(savedJobs);
            writer.write(json);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public static Map<String, Command> loadSavedJobsFromJson(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File file = new File(filePath);
            Path path = Paths.get(filePath);
            if (!file.exists() || file.length() == 0) {
                System.out.println("No jobs to start from file: " + path.getFileName().toString());
                return null;
            }
            return objectMapper.readValue(new File(filePath), new TypeReference<>() {});
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
            return null;
        }
    }
}
