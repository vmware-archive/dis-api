package io.pivotal.dis.lines;

public class Line {
  private final String name;
  private final String status;
  private String startTime;
  private String endTime;

  public Line(String name, String status) {
    this.name = name;
    this.status = status;
  }

  public Line(String name, String status, String startTime, String endTime) {
    this.name = name;
    this.status = status;
    this.startTime = startTime;
    this.endTime = endTime;
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
}
