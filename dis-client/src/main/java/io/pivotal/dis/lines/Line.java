package io.pivotal.dis.lines;

import com.squareup.moshi.Json;

public class Line {
  @Json(name = "line")
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

  @Override
  public String toString() {
    return "Line{" +
            "name='" + name + '\'' +
            ", status='" + status + '\'' +
            ", startTime='" + startTime + '\'' +
            ", endTime='" + endTime + '\'' +
            ", earliestEndTime='" + earliestEndTime + '\'' +
            ", latestEndTime='" + latestEndTime + '\'' +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Line line = (Line) o;

    if (name != null ? !name.equals(line.name) : line.name != null) return false;
    if (status != null ? !status.equals(line.status) : line.status != null) return false;
    if (startTime != null ? !startTime.equals(line.startTime) : line.startTime != null)
      return false;
    if (endTime != null ? !endTime.equals(line.endTime) : line.endTime != null) return false;
    if (earliestEndTime != null ? !earliestEndTime.equals(line.earliestEndTime) : line.earliestEndTime != null)
      return false;
    return !(latestEndTime != null ? !latestEndTime.equals(line.latestEndTime) : line.latestEndTime != null);

  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (status != null ? status.hashCode() : 0);
    result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
    result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
    result = 31 * result + (earliestEndTime != null ? earliestEndTime.hashCode() : 0);
    result = 31 * result + (latestEndTime != null ? latestEndTime.hashCode() : 0);
    return result;
  }
}
