package org.example;

import org.example.model.SharedData;
import org.example.model.WeatherMetrics;
import org.example.utils.Printer;

import java.util.concurrent.*;

public class ReportThread extends Thread {

    private final ConcurrentSkipListMap<Character, WeatherMetrics> stationDataMap;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final SharedData sharedData;


    public ReportThread(ConcurrentSkipListMap<Character, WeatherMetrics> stationDataMap, SharedData sharedData) {
        this.stationDataMap = stationDataMap;
        this.sharedData = sharedData;
    }

    @Override
    public void run() {

        scheduler.scheduleAtFixedRate(
                this::generateReport,
                1,
                1,
                TimeUnit.MINUTES
        );
    }


    private void generateReport() {

        Printer printer = new Printer(stationDataMap);

        synchronized (sharedData.monitor) {

            if (sharedData.numberOfThreadsModifying.get() > 0)
                return;

            System.out.println("Generating report");

            try {
                printer.printMapToCSVFile("src/main/resources/output/log.csv", "REPORT");

                System.out.println("Report generated");
            } catch (InterruptedException e) {
                System.out.println("ReportThread interrupted: " + e.getMessage());
            }
        }

    }


    public void stopReport() {
        scheduler.shutdownNow();
        interrupt();
    }

}
