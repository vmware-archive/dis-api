package io.pivotal.dis.ingest.service.job;

import com.amazonaws.util.json.JSONException;
import io.pivotal.dis.ingest.service.store.FileStore;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Ingester {

    private static final DateTimeFormatter FILE_NAME_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

    private final URL url;
    private final FileStore fileStore;
    private final FileStore digestedFileStore;
    private final OngoingDisruptionsStore ongoingDisruptionsStore;

    public Ingester(URL url,
                    FileStore rawFileStore,
                    FileStore digestedFileStore,
                    OngoingDisruptionsStore ongoingDisruptionsStore) {
        this.url = url;
        this.fileStore = rawFileStore;
        this.digestedFileStore = digestedFileStore;
        this.ongoingDisruptionsStore = ongoingDisruptionsStore;
    }

    public void ingest(Clock clock) {
        try (InputStream inputStream = url.openConnection().getInputStream()) {
            String tflData = IOUtils.toString(inputStream);
            fileStore.save(nameRawFile(clock), tflData);

            Optional<String> previousDisruptionDigest = ongoingDisruptionsStore.getPreviousDisruptionDigest();

            String digestedTflData =
                    new TflDigestor(
                            tflData,
                            clock.getCurrentTime(),
                            previousDisruptionDigest)
                        .digest();

            digestedFileStore.save("disruptions.json", digestedTflData);
            ongoingDisruptionsStore.setPreviousDisruptionDigest(digestedTflData);

        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private String nameRawFile(Clock clock) {
        String timestamp =
                clock.getCurrentTime()
                        .atOffset(ZoneOffset.UTC)
                        .format(FILE_NAME_DATE_TIME);

        return String.format("tfl_api_line_mode_status_tube_%s.json", timestamp);
    }

}
