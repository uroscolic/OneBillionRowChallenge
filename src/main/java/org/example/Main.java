package org.example;

import org.example.utils.ConfigReader;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {


        ConfigReader configReader = new ConfigReader("src/main/resources/config/config.txt");

        String directoryPath = configReader.getValue("directory_path");

        if (directoryPath == null || !Files.isDirectory(Paths.get(directoryPath))) {

            System.out.println("Enter directory path you want to analyze:");
            Scanner scanner = new Scanner(System.in);
            directoryPath = scanner.nextLine();

            while (!Files.isDirectory(Paths.get(directoryPath))) {

                System.err.println("Invalid directory path.");
                System.out.println("Enter directory path:");
                directoryPath = scanner.nextLine();

            }

            configReader.setValue("directory_path", directoryPath);
            configReader.saveConfig();
        }

        System.out.println("Starting application with directory path: " + directoryPath);

        CLIThread cliThread = new CLIThread(directoryPath);
        Thread thread = new Thread(cliThread);
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted");
        }

    }
}