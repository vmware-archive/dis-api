package io.pivotal.dis.ingest.domain;

import io.pivotal.dis.ingest.domain.tfl.LineColor;

public class DisruptedLine {

    private final String status;
    private final String line;
    private final String startTime;
    private final String endTime;
    private final String earliestEndTime;
    private final String latestEndTime;
    private final String backgroundColor;
    private final String foregroundColor;

    public DisruptedLine(String status,
                         String line,
                         String startTime,
                         String endTime,
                         String earliestEndTime,
                         String latestEndTime) {

        this.status = status;
        this.line = line;
        this.startTime = startTime;
        this.endTime = endTime;
        this.earliestEndTime = earliestEndTime;
        this.latestEndTime = latestEndTime;
        this.backgroundColor = LineColor.getBackgroundColorForLine(line);
        this.foregroundColor = LineColor.getForegroundColorForLine(line);
    }

    public String getStatus() {
        return status;
    }

    public String getLine() {
        return line;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getEarliestEndTime() {
        return earliestEndTime;
    }

    public String getLatestEndTime() {
        return latestEndTime;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getForegroundColor() {
        return foregroundColor;
    }
}
