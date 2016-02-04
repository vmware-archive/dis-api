package io.pivotal.dis.ingest.domain.tfl;

import java.util.List;

public class Line {
    private String name;
    private List<LineStatus> lineStatuses;

    public List<LineStatus> getLineStatuses() {
        return lineStatuses;
    }

    public String getName() {
        return name;
    }
}
