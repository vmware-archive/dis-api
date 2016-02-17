package io.pivotal.dis.ingest.domain;

import io.pivotal.dis.ingest.domain.tfl.LineColor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DisruptedLine {

    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final String status;
    private final String line;
    private final String startTime;
    private final String endTime;
    private final String earliestEndTime;
    private final String latestEndTime;
    private final String backgroundColor;
    private final String foregroundColor;
    private final Long latestEndTimestamp;
    private final Long earliestEndTimestamp;
    private final Long endTimestamp;
    private final Long startTimestamp;

    public DisruptedLine(String status,
                         String line,
                         Long startTimestamp,
                         Long endTimestamp,
                         Long earliestEndTimestamp,
                         Long latestEndTimestamp) {

        this.status = status;
        this.line = line;
        this.backgroundColor = LineColor.getBackgroundColorForLine(line);
        this.foregroundColor = LineColor.getForegroundColorForLine(line);

        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.earliestEndTimestamp = earliestEndTimestamp;
        this.latestEndTimestamp = latestEndTimestamp;

        this.startTime = epochMillisToTimeString(this.startTimestamp);
        this.endTime = epochMillisToTimeString(this.endTimestamp);
        this.earliestEndTime = epochMillisToTimeString(this.earliestEndTimestamp);
        this.latestEndTime = epochMillisToTimeString(this.latestEndTimestamp);
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

    public Long getStartTimestamp() {
        return startTimestamp;
    }

    public Long getEndTimestamp() {
        return endTimestamp;
    }

    public Long getEarliestEndTimestamp() {
        
        return earliestEndTimestamp;
    }

    public Long getLatestEndTimestamp() {
        return latestEndTimestamp;
    }

    private String epochMillisToTimeString(Long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC).format(TIME_FORMAT);
    }
}
