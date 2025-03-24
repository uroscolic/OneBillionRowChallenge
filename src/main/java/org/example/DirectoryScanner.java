package org.example;


import org.example.model.SharedData;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DirectoryScanner extends Thread {

    private final Path directory;
    private final SharedData sharedData;
    private final ConcurrentHashMap<Path, Long> lastProcessedMap = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);


    public DirectoryScanner(String directoryPath, SharedData sharedData) {
        this.directory = Paths.get(directoryPath);
        this.sharedData = sharedData;
    }

    @Override
    public void run() {

        processExistingFiles();

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {

            directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);

            while (!Thread.currentThread().isInterrupted()) {

                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    Path filePath = directory.resolve((Path) event.context());
                    Path path = Paths.get(filePath.toString());
                    if (filePath.toString().endsWith(".txt") || filePath.toString().endsWith(".csv")) {

                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {

                            System.out.println("New file detected: " + path.getFileName().toString());

                            processExistingFiles();

                        } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {

                            long currentTime = System.currentTimeMillis();

                            if (lastProcessedMap.containsKey(filePath) && (currentTime - lastProcessedMap.get(filePath)) >= 200) {

                                System.out.println("File modified: " + path.getFileName().toString());

                                processExistingFiles();
                            }
                        }
                    }
                }
                if (!key.reset()) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error watching directory: " + e.getMessage());
        } catch (InterruptedException ignored) {

        }
    }

    private void processExistingFiles() {


        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.{txt,csv}")) {
            for (Path filePath : stream) {
                Path path = Paths.get(filePath.toString());
                if (Files.isRegularFile(filePath)) {

                    if(sharedData.files.containsKey(path.getFileName().toString()))
                        sharedData.files.get(path.getFileName().toString()).incrementAndGet();
                    else
                        sharedData.files.put(path.getFileName().toString(), new AtomicInteger(1));

                    lastProcessedMap.put(filePath, System.currentTimeMillis());

                    executorService.submit(new ReaderThread(sharedData, filePath.toString()));

                }
            }
        } catch (IOException e) {
            System.err.println("Error processing existing files: " + e.getMessage());
        }
    }

    public void stopScanner() {
        executorService.shutdownNow();
        interrupt();
    }

}