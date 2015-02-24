package io.pivotal.dis.ingest.services;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class IngestJob {

    private final UrlProvider urlProvider;
    private final FileStore fileStore;

    @Autowired
    public IngestJob(UrlProvider urlProvider, FileStore fileStore) {
        this.urlProvider = urlProvider;
        this.fileStore = fileStore;
    }

    @Scheduled(fixedRate = 60_000)
    public void execute() {
        try (InputStream is = urlProvider.getUrl().openConnection().getInputStream()) {
            fileStore.save(IOUtils.toString(is));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
