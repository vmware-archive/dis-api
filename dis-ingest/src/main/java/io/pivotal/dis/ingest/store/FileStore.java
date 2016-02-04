package io.pivotal.dis.ingest.store;

public interface FileStore {
    void save(String name, String input);
}
