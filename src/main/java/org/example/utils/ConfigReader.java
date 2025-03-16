package org.example.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ConfigReader {

    private final String configFilePath;
    private final Map<String, String> config;

    public ConfigReader(String configFilePath) {
        this.configFilePath = configFilePath;
        this.config = new HashMap<>();
        loadConfig();
    }


    private void loadConfig() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFilePath)))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("=", 2);

                if (parts.length == 2)
                    config.put(parts[0].trim(), parts[1].trim());

            }

        } catch (IOException e) {
            System.out.println("No config file found with path: " + configFilePath);
        }
    }


    public void saveConfig() {

        Path path = Paths.get(configFilePath);

        try {

            Files.createDirectories(path.getParent());

            try (OutputStream outputStream = new FileOutputStream(configFilePath);
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {

                for (Map.Entry<String, String> entry : config.entrySet()) {
                    writer.write(entry.getKey() + "=" + entry.getValue());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving configuration: " + e.getMessage());
        }
    }

    public String getValue(String key) {
        return config.get(key);
    }

    public void setValue(String key, String value) {
        config.put(key, value);
    }
}