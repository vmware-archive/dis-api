package io.pivotal.dis.ingest.service;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import io.pivotal.dis.ingest.service.job.IngestJob;
import io.pivotal.dis.ingest.service.store.FileStore;
import io.pivotal.dis.ingest.service.tfl.UrlProviderImpl;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class IngestJobTest {

    @Test
    public void savesTflDataToFileStore() throws IOException {
        MockWebServer tflMockWebServer = new MockWebServer();
        tflMockWebServer.enqueue(new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setBody("{\"abc\": 1}")
        );
        tflMockWebServer.play();

        MockFileStore mockFileStore = new MockFileStore();

        UrlProviderImpl urlProviderImpl = new UrlProviderImpl(tflMockWebServer.getUrl("/"));
        IngestJob job = new IngestJob(urlProviderImpl, mockFileStore);
        job.run();

        assertThat(mockFileStore.getLastFile(), equalTo("{\"abc\": 1}"));
    }

    @Test
    public void namesFilesWithTheTimestamp() throws Exception {
        runInASingleSecond(
                () -> IngestJob.nameRawFile(),
                (timestamp, fileName) -> {
                    assertThat(fileName, equalTo("tfl_api_line_mode_status_tube_" + timestamp.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")) + ".json"));
                });
    }

    private class MockFileStore implements FileStore {

        private String lastFile;

        @Override
        public void save(String input) {
            lastFile = input;
        }

        public String getLastFile() {
            return lastFile;
        }

    }

    /**
     * Interesting testing technique.
     */
    private <T> void runInASingleSecond(Supplier<T> block, BiConsumer<LocalDateTime, T> onSuccess) {
        long deadline = System.currentTimeMillis() + 1000L;
        while (System.currentTimeMillis() < deadline) {
            LocalDateTime before = LocalDateTime.now().withNano(0);
            T result = block.get();
            LocalDateTime after = LocalDateTime.now().withNano(0);
            if (after.equals(before)) {
                onSuccess.accept(before, result);
                return;
            }
        }
    }

}
