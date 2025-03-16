package org.example.utils;


import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ConcurrentHashMap;

public class DirectoryScanner extends Thread {

    private final Path directory;
    private final ConcurrentHashMap<Path, Long> lastProcessedMap = new ConcurrentHashMap<>();

    public DirectoryScanner(String directoryPath) {
        this.directory = Paths.get(directoryPath);
    }

    @Override
    public void run() {

        processExistingFiles();

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {

            directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);

            while (true) {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    Path filePath = directory.resolve((Path) event.context());
                    if (filePath.toString().endsWith(".txt") || filePath.toString().endsWith(".csv")) {

                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {

                            System.out.println("New file detected: " + filePath);

                            lastProcessedMap.put(filePath, System.currentTimeMillis());

                            // ReaderThread readerThread = new ReaderThread(filePath.toString());
                            // readerThread.start();


                        } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {

                            long currentTime = System.currentTimeMillis();

                            if (lastProcessedMap.containsKey(filePath) && (currentTime - lastProcessedMap.get(filePath)) >= 200) {

                                System.out.println("File modified: " + filePath);

                                lastProcessedMap.put(filePath, System.currentTimeMillis());

                                // ReaderThread readerThread = new ReaderThread(filePath.toString());
                                // readerThread.start();

                            }
                        }
                    }
                }
                if (!key.reset()) {
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error watching directory: " + e.getMessage());
        }
    }

    private void processExistingFiles() {

        System.out.println("Processing existing files in directory: " + directory);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.{txt,csv}")) {
            for (Path filePath : stream) {
                if (Files.isRegularFile(filePath)) {

//                    System.out.println("Existing file detected: " + filePath);
                    lastProcessedMap.put(filePath, System.currentTimeMillis());

//                    ReaderThread readerThread = new ReaderThread(filePath.toString());
//                    readerThread.start();
                }
            }
        } catch (IOException e) {
            System.err.println("Error processing existing files: " + e.getMessage());
        }
    }

}