package io.pivotal.dis.ingest.domain;


import java.util.List;
import java.util.Optional;

public class Digest {
    private List<DisruptedLine> disruptions;

    public Digest(List<DisruptedLine> disruptions) {
        this.disruptions = disruptions;
    }

    public List<DisruptedLine> getDisruptions() {
        return disruptions;
    }

    public boolean isLineDisrupted(String lineName) {
        return disruptions.stream()
                .anyMatch(disruptedLine -> disruptedLine.getLine().equals(lineName));
    }

    public Optional<DisruptedLine> getLine(String lineName) {
        return getDisruptions().stream()
                .filter(disruptedLine -> disruptedLine.getLine().equals(lineName))
                .findFirst();
    }
}
