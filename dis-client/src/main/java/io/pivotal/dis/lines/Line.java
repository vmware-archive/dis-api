package io.pivotal.dis.lines;

public class Line {
  private final String name;
  private final String status;

  public Line(String name, String status) {
    this.name = name;
    this.status = status;
  }

  public String getName() {
    return name;
  }

  public String getStatus() {
    return status;
  }
}
