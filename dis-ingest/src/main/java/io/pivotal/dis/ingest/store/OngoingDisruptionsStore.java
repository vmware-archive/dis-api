package io.pivotal.dis.ingest.store;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OngoingDisruptionsStore {
    private String previousDisruptionDigest;

    public Optional<String> getPreviousDisruptionDigest() {
        return Optional.ofNullable(previousDisruptionDigest);
    }

    public void setPreviousDisruptionDigest(String digest) {
        previousDisruptionDigest = digest;
    }
}
