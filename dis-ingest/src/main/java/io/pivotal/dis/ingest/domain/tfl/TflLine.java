package io.pivotal.dis.ingest.domain.tfl;

import java.util.List;

public class TflLine {
    private String name;
    private List<LineStatus> lineStatuses;

    public TflLine(String name, List<LineStatus> lineStatuses) {
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

        TflLine tflLine = (TflLine) o;

        if (name != null ? !name.equals(tflLine.name) : tflLine.name != null) return false;
        return !(lineStatuses != null ? !lineStatuses.equals(tflLine.lineStatuses) : tflLine.lineStatuses != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (lineStatuses != null ? lineStatuses.hashCode() : 0);
        return result;
    }
}
