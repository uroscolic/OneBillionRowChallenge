package org.example;

import org.example.argument.Arguments;
import org.example.argumentParser.ArgumentParser;
import org.example.command.CommandType;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        //scan --min 10 --max 20.0 --letter p --output uros.txt --job job1
        //scan -m 10 -M 20.0 -l p -o uros.txt -j job1

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
                    System.out.println(arguments);

                } catch (IllegalArgumentException e) {
                    System.err.println("Error: " + e.getMessage());
                }

            } catch (IllegalArgumentException e) {
                System.err.println("Unknown command: " + commandParts[0]);
                break;  //TODO remove this line, it's here just to stop the infinite loop
            }
        }

        scanner.close();
    }
}