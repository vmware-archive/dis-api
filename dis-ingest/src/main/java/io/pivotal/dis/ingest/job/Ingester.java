package io.pivotal.dis.ingest.job;

import com.amazonaws.util.json.JSONException;
import io.pivotal.dis.ingest.config.ApplicationConfig;
import io.pivotal.dis.ingest.store.FileStore;
import io.pivotal.dis.ingest.store.OngoingDisruptionsStore;
import io.pivotal.dis.ingest.system.Clock;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class Ingester {

    private static final DateTimeFormatter FILE_NAME_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

    private final URL url;
    private final FileStore rawFileStore;
    private final FileStore digestedFileStore;
    private final OngoingDisruptionsStore ongoingDisruptionsStore;

    @Autowired
    public Ingester(URL tflUrl,
                    FileStore rawFileStore,
                    FileStore digestedFileStore,
                    OngoingDisruptionsStore ongoingDisruptionsStore) {
        this.url = tflUrl;
        this.rawFileStore = rawFileStore;
        this.digestedFileStore = digestedFileStore;
        this.ongoingDisruptionsStore = ongoingDisruptionsStore;
    }

    public void ingest(Clock clock) {
        try (InputStream inputStream = url.openConnection().getInputStream()) {
            String tflData = IOUtils.toString(inputStream);
            String rawFileName = nameRawFile(clock);
            rawFileStore.save(rawFileName, tflData);

            System.out.println("Saved raw file " + rawFileName);

            Optional<String> previousDisruptionDigest = ongoingDisruptionsStore.getPreviousDisruptionDigest();

            String digestedTflData =
                    new TflDigestor(
                            tflData,
                            clock.getCurrentTime(),
                            previousDisruptionDigest)
                        .digest();

            digestedFileStore.save("disruptions.json", digestedTflData);

            System.out.println("Saved digested file");

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
