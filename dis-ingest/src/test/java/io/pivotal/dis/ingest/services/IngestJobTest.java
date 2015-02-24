package io.pivotal.dis.ingest.services;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.junit.Test;

import java.io.IOException;

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
        job.execute();

        assertThat(mockFileStore.getLastFile(), equalTo("{\"abc\": 1}"));
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

}