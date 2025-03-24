package org.example.model;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;


@Data
public class WeatherMetrics {

    private Character stationCharacter;
    private DoubleAdder sum = new DoubleAdder();
    private AtomicLong count = new AtomicLong(0);

    public WeatherMetrics(Character stationCharacter) {
        this.stationCharacter = stationCharacter;
    }

    public void addValue(double value) {
        sum.add(value);
        count.incrementAndGet();
    }

    public void removeValue(WeatherMetrics value){

        sum.add(-value.getSum().sum());

        for(int i = 0 ; i < value.getCount().get(); i++)
            count.decrementAndGet();
    }

    @Override
    public String toString(){
        return stationCharacter + " | sum: " + getSum() + " count: " + getCount();
    }


}
