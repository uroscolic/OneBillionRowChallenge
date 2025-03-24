package org.example.command;

import lombok.Setter;
import org.example.JobManager;
import org.example.model.SharedData;
import org.example.model.WeatherMetrics;
import org.example.utils.Printer;

import java.util.concurrent.ConcurrentSkipListMap;

@Setter
public class ExportMapCommand extends Command {

    private ConcurrentSkipListMap<Character, WeatherMetrics> stationDataMap;
    private SharedData sharedData;

    public ExportMapCommand() {
        setCommandType(CommandType.EXPORTMAP);
        setJobName("exportmap-" + JobManager.counter.get());
    }

    public ExportMapCommand(ConcurrentSkipListMap<Character, WeatherMetrics> stationDataMap, SharedData sharedData) {
        this();
        this.stationDataMap = stationDataMap;
        this.sharedData = sharedData;
    }

    @Override
    public String call() {
        setStatus(Status.RUNNING);

        Printer printer = new Printer(stationDataMap);

        synchronized (sharedData.monitor) {

            while (sharedData.numberOfThreadsModifying.get() > 0) {

                if (stationDataMap.isEmpty())
                    System.out.println("No data to generate report");

                try {
                    System.out.println("Waiting for data to be processed...");
                    sharedData.monitor.wait();
                } catch (InterruptedException e) {
                    System.out.println("ExportMapCommand interrupted");
                    return "ExportMapCommand interrupted";
                }
            }

            try {
                printer.printMapToCSVFile("src/main/resources/output/log.csv", "EXPORTMAP");
            } catch (InterruptedException e) {
                System.out.println("ExportMapCommand interrupted");
                return "ExportMapCommand interrupted";
            }

            setStatus(Status.COMPLETED);
            return "ExportMap generated";
        }

    }
}
