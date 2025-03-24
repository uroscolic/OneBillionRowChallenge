package org.example;

import lombok.Getter;
import org.example.command.Command;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class JobManager {

    public static final AtomicInteger counter = new AtomicInteger(0);
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final BlockingQueue<Command> commandQueue = new LinkedBlockingQueue<>();
    @Getter
    private final ConcurrentHashMap<String, Command> jobStatusMap = new ConcurrentHashMap<>();
    private Thread taskConsumer;


    public JobManager() {
        startTaskConsumer();
    }

    public void addTask(Command command) {
        try {
            System.out.println("Adding task to queue: " + command.getJobName());
            commandQueue.put(command);
            jobStatusMap.put(command.getJobName() + "-" + counter.incrementAndGet(), command);
        } catch (InterruptedException e) {
            System.err.println("Error adding task to queue: " + e.getMessage());
        }
    }

    private void startTaskConsumer() {
        taskConsumer = new Thread(() -> {
            while (true) {
                try {
                    Command command = commandQueue.take();
                    executorService.submit(command);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        taskConsumer.start();
    }

    public void stop() {

        executorService.shutdownNow();
        taskConsumer.interrupt();

    }

    public boolean isJobNameTaken(String jobName) {

        for (String key : jobStatusMap.keySet())
            if (key.startsWith(jobName + "-"))
                return true;

        return false;
    }
}
