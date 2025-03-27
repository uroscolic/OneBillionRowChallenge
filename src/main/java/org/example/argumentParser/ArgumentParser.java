package org.example.argumentParser;

import org.example.argument.*;
import org.example.command.CommandType;

public class ArgumentParser {

    public static Arguments parseArguments(String[] args, CommandType command) {

        return switch (command) {
            case SCAN -> parseScanArguments(args);
            case SHUTDOWN -> parseShutdownArguments(args);
            case STATUS -> parseStatusArguments(args);
            case START -> parseStartArguments(args);
            case MAP, EXPORTMAP -> null;
        };

    }

    private static ScanArguments parseScanArguments(String[] args) {

        Double minTemp = null;
        Double maxTemp = null;
        char letter = '\0';
        String outputFile = null;
        String jobName = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {

                case "--min":
                case "-m":

                    if (minTemp != null)
                        throw new IllegalArgumentException("Min temperature already set.");

                    minTemp = Double.parseDouble(args[++i].replace(",", "."));
                    break;

                case "--max":
                case "-M":

                    if (maxTemp != null)
                        throw new IllegalArgumentException("Max temperature already set.");

                    maxTemp = Double.parseDouble(args[++i].replace(",", "."));
                    break;

                case "--letter":
                case "-l":

                    if (letter != '\0')
                        throw new IllegalArgumentException("Letter already set.");

                    if (args[i + 1].length() > 1)
                        throw new IllegalArgumentException("Letter must be a single character.");

                    letter = args[++i].charAt(0);
                    break;
                case "--output":
                case "-o":

                    if (outputFile != null)
                        throw new IllegalArgumentException("Output file already set.");

                    outputFile = args[++i];

                    if (outputFile.isEmpty() || !outputFile.matches("^[a-zA-Z0-9_.-]+\\.(txt)$"))
                        throw new IllegalArgumentException("Output file must be a .txt file containing only letters, numbers '.', '-' or '_'.");

                    break;
                case "--job":
                case "-j":

                    if (jobName != null)
                        throw new IllegalArgumentException("Job name already set.");

                    jobName = args[++i];
                    break;

                default:
                    throw new IllegalArgumentException("Unknown argument: " + args[i]);
            }
        }

        if (letter == '\0' || outputFile == null || jobName == null)
            throw new IllegalArgumentException("Missing arguments for SCAN command.");

        return new ScanArguments(minTemp, maxTemp, letter, outputFile, jobName);
    }

    private static ShutdownArguments parseShutdownArguments(String[] args) {

        if (args.length > 1) {
            throw new IllegalArgumentException("Too many arguments for SHUTDOWN command.");
        }

        boolean saveJobs = false;

        if (args.length == 1) {
            if (!args[0].equals("--save-jobs") && !args[0].equals("-s"))
                throw new IllegalArgumentException("Unknown argument: " + args[0]);

            saveJobs = true;

        }

        return new ShutdownArguments(saveJobs);
    }

    private static Arguments parseStatusArguments(String[] args) {

        if (args.length != 2)
            throw new IllegalArgumentException("STATUS command requires exactly two arguments: --job (or -j) and job name.");

        if (!args[0].equals("--job") && !args[0].equals("-j"))
            throw new IllegalArgumentException("First argument must be --job or -j.");

        return new StatusArguments(args[1]);
    }

    private static Arguments parseStartArguments(String[] args) {

        boolean startPendingJobs = false;

        if (args.length > 1)
            throw new IllegalArgumentException("Too many arguments for START command.");

        if (args.length == 1) {
            if (!args[0].equals("--load-jobs") && !args[0].equals("-l"))
                throw new IllegalArgumentException("Unknown argument: " + args[0]);

            startPendingJobs = true;
        }

        return new StartArguments(startPendingJobs);
    }
}