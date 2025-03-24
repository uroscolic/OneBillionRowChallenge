package org.example.argument;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScanArguments implements Arguments {

    private double minTemperature;
    private double maxTemperature;
    private char letter;
    private String outputFile;
    private String jobName;

    public ScanArguments(Double minTemperature, Double maxTemperature, char letter, String outputFile, String jobName) {

        this.minTemperature = (minTemperature != null) ? minTemperature : Integer.MIN_VALUE;
        this.maxTemperature = (maxTemperature != null) ? maxTemperature : Integer.MAX_VALUE;

        if (this.minTemperature > this.maxTemperature)
            throw new IllegalArgumentException("Min temperature cannot be greater than max temperature.");

        if (outputFile == null)
            throw new IllegalArgumentException("Output file is required.");

        if (letter == '\0')
            throw new IllegalArgumentException("Letter is required.");

        if (jobName == null || jobName.isEmpty())
            throw new IllegalArgumentException("Job name is required.");

        this.letter = letter;
        this.outputFile = outputFile;
        this.jobName = jobName;
    }

    @Override
    public String toString() {
        return "ScanArguments | " +
                "minTemperature = " + minTemperature +
                ", maxTemperature = " + maxTemperature +
                ", letter = " + letter +
                ", outputFile = '" + outputFile + '\'' +
                ", jobName = '" + jobName + '\'';
    }
}