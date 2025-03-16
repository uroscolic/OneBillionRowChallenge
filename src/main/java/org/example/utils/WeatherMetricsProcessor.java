package org.example.utils;

import org.example.model.WeatherMetrics;

import java.util.concurrent.ConcurrentSkipListMap;

public class WeatherMetricsProcessor {

    public static void processLines(String lines, ConcurrentSkipListMap<Character, WeatherMetrics> stationDataMap) {

        String[] data = lines.split("\n");

        for (String line : data) {

            String[] values = line.split(";");

            if (values.length < 2)
                continue;

            String station = values[0];
            String temperature = values[1];

            if (!isNumeric(temperature))
                continue;

            Character stationCharacter = station.charAt(0);
            double temp = Double.parseDouble(temperature.trim());

            stationDataMap.compute(stationCharacter, (ch, weatherMetrics) -> {

                if (weatherMetrics == null)
                    weatherMetrics = new WeatherMetrics(ch);
                weatherMetrics.addValue(temp);
                return weatherMetrics;

            });
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
