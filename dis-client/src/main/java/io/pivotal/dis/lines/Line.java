package io.pivotal.dis.lines;

public class Line {
  private final String name;
  private final String status;
  private String startTime;

  public Line(String name, String status) {
    this.name = name;
    this.status = status;
  }

  public Line(String name, String status, String startTime) {
    this.name = name;
    this.status = status;
    this.startTime = startTime;
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
}
