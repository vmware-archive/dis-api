package io.pivotal.dis.ingest.domain.tfl;

public class LineStatus {
    private String statusSeverityDescription;

    public LineStatus(String statusSeverityDescription) {
        this.statusSeverityDescription = statusSeverityDescription;
    }

    public String getStatusSeverityDescription() {
        return statusSeverityDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineStatus that = (LineStatus) o;

        return !(statusSeverityDescription != null ? !statusSeverityDescription.equals(that.statusSeverityDescription) : that.statusSeverityDescription != null);

    }

    @Override
    public int hashCode() {
        return statusSeverityDescription != null ? statusSeverityDescription.hashCode() : 0;
    }
}
