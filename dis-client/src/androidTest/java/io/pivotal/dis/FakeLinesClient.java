package io.pivotal.dis;

import java.util.List;

import io.pivotal.dis.lines.Line;
import io.pivotal.dis.lines.LinesClient;

public class FakeLinesClient implements LinesClient {
    private List<Line> lines;

    FakeLinesClient(List<Line> lines) {
        this.lines = lines;
    }

    @Override
    public List<Line> fetchDisruptedLines() {
        return lines;
    }

    public void setDisruptedLines(List<Line> lines) {
        this.lines = lines;
    }
}
