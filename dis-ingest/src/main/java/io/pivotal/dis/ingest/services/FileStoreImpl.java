package io.pivotal.dis.ingest.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FileStoreImpl implements FileStore {

    private static final Logger log = LoggerFactory.getLogger(FileStoreImpl.class);

    @Override
    public void save(String input) {
        log.info("Saving input to log [{}]", input);
    }
}
