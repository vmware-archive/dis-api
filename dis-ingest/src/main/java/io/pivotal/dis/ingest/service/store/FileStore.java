package io.pivotal.dis.ingest.service.store;

public interface FileStore {
    void save(String name, String input);
}
