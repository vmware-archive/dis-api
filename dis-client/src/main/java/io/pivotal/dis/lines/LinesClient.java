package io.pivotal.dis.lines;

import java.util.List;

public interface LinesClient {
  List<Line> fetchDisruptedLines() throws Exception;
}
