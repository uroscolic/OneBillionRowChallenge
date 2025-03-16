package org.example;

import org.example.model.SharedData;
import org.example.model.WeatherMetrics;
import org.example.utils.Printer;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReportThread extends Thread {

    private final ConcurrentSkipListMap<Character, WeatherMetrics> stationDataMap;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final SharedData sharedData;
    private int counter = 0;


    public ReportThread(ConcurrentSkipListMap<Character, WeatherMetrics> stationDataMap, SharedData sharedData, int counter) {
        this.stationDataMap = stationDataMap;
        this.sharedData = sharedData;
        this.counter = counter;
    }

    @Override
    public void run() {

        scheduler.scheduleAtFixedRate(
                this::generateReport,
                0,
                10,
                TimeUnit.SECONDS
        );
    }


    private void generateReport() {

        Printer printer = new Printer(stationDataMap);

        synchronized (sharedData.monitor) {

            while (sharedData.numberOfThreadsModifying.get() > 0) {

                if (stationDataMap.isEmpty())    // TODO: MOVE TO MAP
                    System.out.println("No data to generate report");

                try {
                    sharedData.monitor.wait();
                } catch (InterruptedException e) {
                    System.err.println("ReportThread interrupted: " + e.getMessage());
                }
            }

            System.out.println("Generating report " + counter);
            printer.printMapToCSVFile("src/main/resources/output/log.csv", "REPORT" + " " + counter);
        }

        System.out.println("Report " + counter + " generated");
    }


    public void stopScheduler() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS))
                scheduler.shutdownNow();

        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

}
