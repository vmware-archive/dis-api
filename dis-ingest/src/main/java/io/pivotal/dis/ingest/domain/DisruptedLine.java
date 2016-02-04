package io.pivotal.dis.ingest.domain;

public class DisruptedLine {

    private final String status;
    private final String line;
    private final String startTime;
    private final String endTime;

    public DisruptedLine(String status, String line, String startTime, String endTime) {
        this.status = status;
        this.line = line;
        this.startTime = startTime;
        this.endTime = endTime;
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
}
