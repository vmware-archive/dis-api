package io.pivotal.dis.ingest.service;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import io.pivotal.dis.ingest.service.job.IngestJob;
import io.pivotal.dis.ingest.service.store.FileStore;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class IngestJobTest {

    private final MockWebServer tflMockWebServer = new MockWebServer();
    private final MockFileStore rawFileStore = new MockFileStore();
    private final MockFileStore digestedFileStore = new MockFileStore();

    @Before
    public void prepareServer() throws Exception {
        tflMockWebServer.enqueue(new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setBody("[{\"name\": \"Bakerloo\", \"lineStatuses\": [{\"statusSeverityDescription\": \"Runaway Train\"}]}]")
        );
        tflMockWebServer.play();
    }

    @Test
    public void savesTflDataToFileStore() throws IOException {
        IngestJob job = new IngestJob(tflMockWebServer.getUrl("/"), rawFileStore, digestedFileStore);

        runInASingleSecond(
                () -> job.run(),
                (timestamp) -> {
                    assertThat(rawFileStore.getLastName(), equalTo("tfl_api_line_mode_status_tube_" + timestamp.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")) + ".json"));
                    assertThat(rawFileStore.getLastFile(), equalTo("[{\"name\": \"Bakerloo\", \"lineStatuses\": [{\"statusSeverityDescription\": \"Runaway Train\"}]}]"));
                });
    }

    @Test
    public void savesTranslatedDataToFileStore() throws Exception {
        IngestJob job = new IngestJob(tflMockWebServer.getUrl("/"), rawFileStore, digestedFileStore);

        job.run();

        assertThat(digestedFileStore.getLastName(), equalTo("disruptions.json"));
        assertThat(digestedFileStore.getLastFile(), equalTo("{\"disruptions\":[{\"line\":\"Bakerloo\",\"status\":\"Runaway Train\"}]}"));
    }

    private class MockFileStore implements FileStore {

        private String lastName;
        private String lastFile;

        @Override
        public void save(String name, String input) {
            lastName = name;
            lastFile = input;
        }

        public String getLastName() {
            return lastName;
        }

        public String getLastFile() {
            return lastFile;
        }

    }

    /**
     * Interesting testing technique.
     */
    private <T> void runInASingleSecond(Runnable block, Consumer<LocalDateTime> onSuccess) {
        long deadline = System.currentTimeMillis() + 1000L;
        while (System.currentTimeMillis() < deadline) {
            LocalDateTime before = LocalDateTime.now().withNano(0);
            block.run();
            LocalDateTime after = LocalDateTime.now().withNano(0);
            if (after.equals(before)) {
                onSuccess.accept(before);
                return;
            }
        }
        throw new AssertionError("was not able to run in a single second");
    }

}
