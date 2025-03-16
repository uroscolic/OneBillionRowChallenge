package org.example;

import org.example.argument.Arguments;
import org.example.argument.ScanArguments;
import org.example.argumentParser.ArgumentParser;
import org.example.command.CommandType;
import org.example.command.ScanCommand;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CLIThread implements Runnable {

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final String directoryPath;

    public CLIThread(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        //scan --min 10 --max 20.0 --letter U --output uros.txt --job job1
        //scan -m 0 -M 100.0 -l K -o uros.txt -j job1

        while (true) {

            System.out.print("> ");
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

                    if (command == CommandType.MAP) {
                        System.out.println(command);
                        continue;
                    }

                    if (command == CommandType.EXPORTMAP) {
                        System.out.println(command);
                        continue;
                    }

                    Arguments arguments = ArgumentParser.parseArguments(argumentsWithoutCommand, command);

                    if (command == CommandType.SCAN) {

                        File directory = new File(directoryPath);
                        File[] files = directory.listFiles();

                        ScanArguments scanArguments = (ScanArguments) arguments;
                        List<Future<String>> futures = new ArrayList<>();


                        if (files != null) {
                            for (File file : files) {
                                if (file.isFile() && (file.getName().endsWith(".csv") || file.getName().endsWith(".txt"))) {

                                    ScanCommand scanCommand = new ScanCommand(file.getAbsolutePath(), scanArguments);
                                    Future<String> future = executorService.submit(scanCommand);
                                    futures.add(future);
                                }
                            }
                        }

                        try {
                            for (Future<String> future : futures) {
                                String result = future.get();
                                System.out.println("ScanCommand result: " + result);
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            System.err.println("Error executing ScanCommand: " + e.getMessage());
                        }


                    }

                } catch (IllegalArgumentException e) {
                    System.err.println("Error: " + e.getMessage());
                }

            } catch (IllegalArgumentException e) {
                System.err.println("Unknown command: " + commandParts[0]);
                executorService.shutdown();
                break;  //TODO remove this line, it's here just to stop the infinite loop
            }
        }

        scanner.close();
    }
}
