package io.pivotal.dis.ingest.service.job;

import io.pivotal.dis.ingest.service.store.FileStore;
import io.pivotal.dis.ingest.service.tfl.UrlProvider;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class IngestJob implements Runnable {

    private static final DateTimeFormatter FILE_NAME_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

    private final UrlProvider urlProvider;
    private final FileStore fileStore;

    public IngestJob(UrlProvider urlProvider, FileStore fileStore) {
        this.urlProvider = urlProvider;
        this.fileStore = fileStore;
    }

    private String nameRawFile() {
        String timestamp = LocalDateTime.now()
                .atOffset(ZoneOffset.UTC)
                .format(FILE_NAME_DATE_TIME);
        return String.format("tfl_api_line_mode_status_tube_%s.json", timestamp);
    }

    public void run() {
        try {
            InputStream is = urlProvider.getUrl().openConnection().getInputStream();
            fileStore.save(nameRawFile(), IOUtils.toString(is));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
