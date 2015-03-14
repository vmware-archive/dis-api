package io.pivotal.dis.ingest.service.job;

import io.pivotal.dis.ingest.service.tfl.UrlProvider;
import io.pivotal.dis.ingest.service.store.FileStore;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class IngestJob implements Runnable {

    private final UrlProvider urlProvider;
    private final FileStore fileStore;

    public IngestJob(UrlProvider urlProvider, FileStore fileStore) {
        this.urlProvider = urlProvider;
        this.fileStore = fileStore;
    }

    public void run() {
        try {
            InputStream is = urlProvider.getUrl().openConnection().getInputStream();
            fileStore.save(IOUtils.toString(is));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
