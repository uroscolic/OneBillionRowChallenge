package org.example.model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SharedData {

    public final AtomicInteger numberOfThreadsModifying = new AtomicInteger(0);
    public final Object monitor = new Object();
    public final ConcurrentSkipListMap<Character, WeatherMetrics> stationDataMap = new ConcurrentSkipListMap<>();
    public ConcurrentHashMap<String, AtomicInteger> files = new ConcurrentHashMap<>();
}
