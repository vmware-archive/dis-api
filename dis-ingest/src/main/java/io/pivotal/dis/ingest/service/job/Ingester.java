package io.pivotal.dis.ingest.service.job;

import com.amazonaws.util.json.JSONException;

import io.pivotal.dis.ingest.config.OngoingDisruptionsStore;
import io.pivotal.dis.ingest.service.store.FileStore;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Ingester {

    private static final DateTimeFormatter FILE_NAME_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

    private final URL url;
    private final FileStore fileStore;
    private final FileStore digestedFileStore;
    private final Clock clock;
    private final OngoingDisruptionsStore ongoingDisruptionsStore;

    public Ingester(URL url, FileStore rawFileStore, FileStore digestedFileStore, Clock clock, OngoingDisruptionsStore ongoingDisruptionsStore) {
        this.url = url;
        this.fileStore = rawFileStore;
        this.digestedFileStore = digestedFileStore;
        this.clock = clock;
        this.ongoingDisruptionsStore = ongoingDisruptionsStore;
    }

    public void ingest() {
        try (InputStream inputStream = url.openConnection().getInputStream()) {
            String tflData = IOUtils.toString(inputStream);
            fileStore.save(nameRawFile(), tflData);

            String previousDisruptionDigest = ongoingDisruptionsStore.getPreviousDisruptionDigest();
            LocalDateTime currentTime = clock.getCurrentTime();
            String digestedTflData = TflToDisTranslator.digestTflData(tflData, previousDisruptionDigest, currentTime);

            digestedFileStore.save("disruptions.json", digestedTflData);
            ongoingDisruptionsStore.setPreviousDisruptionDigest(digestedTflData);
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private String nameRawFile() {
        String timestamp = clock.getCurrentTime()
                .atOffset(ZoneOffset.UTC)
                .format(FILE_NAME_DATE_TIME);
        return String.format("tfl_api_line_mode_status_tube_%s.json", timestamp);
    }

}
