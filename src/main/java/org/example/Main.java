package org.example;

import org.example.argument.ScanArguments;
import org.example.command.ScanCommand;
import org.example.model.SharedData;
import org.example.model.WeatherMetrics;
import org.example.utils.ConfigReader;
import org.example.utils.Printer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {

        //src/main/resources/measurements_small.txt
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
            scanner.close();
            configReader.setValue("directory_path", directoryPath);
            configReader.saveConfig();
        }

        ConcurrentSkipListMap<Character, WeatherMetrics> stationDataMap = new ConcurrentSkipListMap<>();

//        DirectoryScanner directoryWatcher = new DirectoryScanner(directoryPath);
//        directoryWatcher.start();
//        SharedData sharedData = new SharedData();
//
//        ReaderThread readerThread = new ReaderThread(stationDataMap, sharedData, "src/main/resources/measurements_small.txt");
//        ReaderThread readerThread2 = new ReaderThread(stationDataMap, sharedData, "src/main/resources/measurements_medium.txt");
//        ReaderThread readerThread3 = new ReaderThread(stationDataMap, sharedData, "src/main/resources/measurements_small.txt");
//        ReaderThread readerThread4 = new ReaderThread(stationDataMap, sharedData, "src/main/resources/measurements_medium.txt");
//
//        ReportThread reportThread = new ReportThread(stationDataMap, sharedData, 1);
//        ReportThread reportThread2 = new ReportThread(stationDataMap, sharedData, 2);
//
//        readerThread.start();
//        readerThread2.start();
//        readerThread3.start();
//        readerThread4.start();
//        reportThread.start();
//        reportThread2.start();
//
//        try {
//            readerThread.join();
//            readerThread2.join();
//            readerThread3.join();
//            readerThread4.join();
//            Thread.sleep(10 * 1000);
//            reportThread.stopScheduler();
//            reportThread2.stopScheduler();
//
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//        Printer printer = new Printer(stationDataMap);
//        printer.printMapToConsole();

        CLIThread cliThread = new CLIThread(directoryPath);
        Thread thread = new Thread(cliThread);
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }




    }
}