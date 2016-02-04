package io.pivotal.dis.ingest.config;

import java.util.Optional;

public class OngoingDisruptionsStore {
    private String previousDisruptionDigest;

    public Optional<String> getPreviousDisruptionDigest() {
        return Optional.ofNullable(previousDisruptionDigest);
    }
    public void setPreviousDisruptionDigest(String digest) {
        previousDisruptionDigest = digest;
    }
}
