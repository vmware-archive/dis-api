package io.pivotal.dis.ingest.domain.tfl;

import java.util.List;

public class Line {
    private String name;
    private List<LineStatus> lineStatuses;

    public Line(String name, List<LineStatus> lineStatuses) {
        this.name = name;
        this.lineStatuses = lineStatuses;
    }

    public List<LineStatus> getLineStatuses() {
        return lineStatuses;
    }

    public String getName() {
        return name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Line line = (Line) o;

        if (name != null ? !name.equals(line.name) : line.name != null) return false;
        return !(lineStatuses != null ? !lineStatuses.equals(line.lineStatuses) : line.lineStatuses != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (lineStatuses != null ? lineStatuses.hashCode() : 0);
        return result;
    }
}
