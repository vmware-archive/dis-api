package io.pivotal.dis.lines;

public class Line {
  private final String name;
  private final String status;
  private String startTime;
  private String endTime;
  private String earliestEndTime;
  private String latestEndTime;

  public Line(String name, String status) {
    this.name = name;
    this.status = status;
  }

  public Line(String name, String status, String startTime, String endTime, String earliestEndTime, String latestEndTime) {
    this.name = name;
    this.status = status;
    this.startTime = startTime;
    this.endTime = endTime;
    this.earliestEndTime = earliestEndTime;
    this.latestEndTime = latestEndTime;
  }

  public String getName() {
    return name;
  }

  public String getStatus() {
    return status;
  }

  public String getStartTime() {
    return startTime;
  }

  public String getEndTime() { return endTime; }

  public String getEarliestEndTime() { return earliestEndTime; }

  public String getLatestEndTime() { return latestEndTime; }
}
