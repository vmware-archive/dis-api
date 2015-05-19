package io.pivotal.dis.ingest.config;

public class OngoingDisruptionsStore {
    private String previousDisruptionDigest;
    public String getPreviousDisruptionDigest() {
        return previousDisruptionDigest;
    }
    public void setPreviousDisruptionDigest(String digest) {
        previousDisruptionDigest = digest;
    }
}
