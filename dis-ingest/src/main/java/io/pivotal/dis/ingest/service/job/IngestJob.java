package io.pivotal.dis.ingest.service.job;

import com.amazonaws.util.json.JSONException;

import io.pivotal.dis.ingest.config.OngoingDisruptionsStore;
import io.pivotal.dis.ingest.service.store.FileStore;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class IngestJob implements Runnable {

    private static final DateTimeFormatter FILE_NAME_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

    private final URL url;
    private final FileStore fileStore;
    private final FileStore digestedFileStore;
    private final LocalDateTime currentTime;
    private final OngoingDisruptionsStore ongoingDisruptionsStore;

    public IngestJob(URL url, FileStore rawFileStore, FileStore digestedFileStore, LocalDateTime currentTime, OngoingDisruptionsStore ongoingDisruptionsStore) {
        this.url = url;
        this.fileStore = rawFileStore;
        this.digestedFileStore = digestedFileStore;
        this.currentTime = currentTime;
        this.ongoingDisruptionsStore = ongoingDisruptionsStore;
    }

    private String nameRawFile() {
        String timestamp = LocalDateTime.now()
                .atOffset(ZoneOffset.UTC)
                .format(FILE_NAME_DATE_TIME);
        return String.format("tfl_api_line_mode_status_tube_%s.json", timestamp);
    }

    public void run() {
        try (InputStream inputStream = url.openConnection().getInputStream()) {
            String tflData = IOUtils.toString(inputStream);
            fileStore.save(nameRawFile(), tflData);
            String digestedTflData = TflToDisTranslator.digestTflData(tflData, ongoingDisruptionsStore.getPreviousDisruptionDigest(), currentTime);
            digestedFileStore.save("disruptions.json", digestedTflData);
            ongoingDisruptionsStore.setPreviousDisruptionDigest(digestedTflData);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

}
