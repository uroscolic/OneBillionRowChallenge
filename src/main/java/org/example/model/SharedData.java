package org.example.model;

import java.util.concurrent.atomic.AtomicInteger;

public class SharedData {

    public final AtomicInteger numberOfThreadsModifying = new AtomicInteger(0);
    public final Object monitor = new Object();

}
