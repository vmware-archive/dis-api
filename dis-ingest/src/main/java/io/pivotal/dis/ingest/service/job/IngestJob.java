package io.pivotal.dis.ingest.service.job;

import com.amazonaws.util.json.JSONException;
import io.pivotal.dis.ingest.service.store.FileStore;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class IngestJob implements Runnable {

    private static final DateTimeFormatter FILE_NAME_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

    private final URL url;
    private final FileStore fileStore;
    private final FileStore digestedFileStore;

    public IngestJob(URL url, FileStore rawFileStore, FileStore digestedFileStore) {
        this.url = url;
        this.fileStore = rawFileStore;
        this.digestedFileStore = digestedFileStore;
    }

    private String nameRawFile() {
        String timestamp = LocalDateTime.now()
                .atOffset(ZoneOffset.UTC)
                .format(FILE_NAME_DATE_TIME);
        return String.format("tfl_api_line_mode_status_tube_%s.json", timestamp);
    }

    public void run() {
        try {
            String tflData = IOUtils.toString(url.openConnection().getInputStream());
            fileStore.save(nameRawFile(), tflData);
            digestedFileStore.save("disruptions.json", TflToDisTranslator.convertJsonArrayToList(tflData));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

}
