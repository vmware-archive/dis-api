package io.pivotal.dis.ingest.service;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import io.pivotal.dis.ingest.config.OngoingDisruptionsStore;
import io.pivotal.dis.ingest.service.job.Clock;
import io.pivotal.dis.ingest.service.job.Ingester;
import io.pivotal.dis.ingest.service.store.FileStore;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class IngesterTest {

    private final MockWebServer tflMockWebServer = new MockWebServer();
    private final MockFileStore rawFileStore = new MockFileStore();
    private final MockFileStore digestedFileStore = new MockFileStore();
    private OngoingDisruptionsStore ongoingDisruptionsStore;

    @Before
    public void prepareServer() throws Exception {
        tflMockWebServer.enqueue(new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setBody("[{\"name\": \"Bakerloo\", \"lineStatuses\": [{\"statusSeverityDescription\": \"Runaway Train\"}]}]")
        );
        tflMockWebServer.enqueue(new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setBody("[" +
                                "{\"name\": \"Bakerloo\", \"lineStatuses\": [{\"statusSeverityDescription\": \"Runaway Train\"}]}," +
                                "{\"name\": \"Circle\", \"lineStatuses\": [{\"statusSeverityDescription\": \"Leaves on the Line\"}]}" +
                                "]")
        );
        tflMockWebServer.play();
        ongoingDisruptionsStore = new OngoingDisruptionsStore();
    }

    @Test
    public void savesTflDataToFileStore() throws IOException, JSONException {
        Clock clock = new FakeClock(LocalDateTime.now());
        Ingester job = new Ingester(tflMockWebServer.getUrl("/"), rawFileStore, digestedFileStore, clock, ongoingDisruptionsStore);
        job.ingest();
        assertThat(rawFileStore.getLastName(), equalTo("tfl_api_line_mode_status_tube_" + clock.getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")) + ".json"));
        String lastFile = rawFileStore.getLastFile();
        JSONArray lastFileAsJson = new JSONArray(lastFile);
        JSONObject line = lastFileAsJson.getJSONObject(0);
        assertThat(line.get("name"), equalTo("Bakerloo"));
        JSONArray lineStatuses = (JSONArray) line.get("lineStatuses");
        JSONObject lineStatus = (JSONObject) lineStatuses.get(0);
        assertThat(lineStatus.get("statusSeverityDescription"), equalTo("Runaway Train"));
    }

    @Test
    public void savesTflDataToFileStoreForTwoSuccessiveIngestJobs() throws IOException {
        FakeClock clockNow = new FakeClock(LocalDateTime.now());
        Ingester job = new Ingester(tflMockWebServer.getUrl("/"), rawFileStore, digestedFileStore, clockNow, ongoingDisruptionsStore);
        job.ingest();
        assertThat(rawFileStore.getLastName(), equalTo("tfl_api_line_mode_status_tube_" + clockNow.getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")) + ".json"));
        assertThat(rawFileStore.getLastFile(), equalTo("[{\"name\": \"Bakerloo\", \"lineStatuses\": [{\"statusSeverityDescription\": \"Runaway Train\"}]}]"));

        FakeClock clockInTenMinutesTime = new FakeClock(LocalDateTime.now().plusMinutes(10));
        Ingester secondJob = new Ingester(tflMockWebServer.getUrl("/"), rawFileStore, digestedFileStore, clockInTenMinutesTime, ongoingDisruptionsStore);
        secondJob.ingest();

        assertThat(rawFileStore.getLastName(), equalTo("tfl_api_line_mode_status_tube_" + clockInTenMinutesTime.getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")) + ".json"));
        assertThat(rawFileStore.getLastFile(), equalTo("[" +
                                "{\"name\": \"Bakerloo\", \"lineStatuses\": [{\"statusSeverityDescription\": \"Runaway Train\"}]}," +
                                "{\"name\": \"Circle\", \"lineStatuses\": [{\"statusSeverityDescription\": \"Leaves on the Line\"}]}" +
                                "]"));
    }

    @Test
    public void savesTranslatedDataToFileStore() throws Exception {
        Ingester job = new Ingester(tflMockWebServer.getUrl("/"), rawFileStore, digestedFileStore, new FakeClock(LocalDateTime.now()), ongoingDisruptionsStore);
        job.ingest();

        LocalDateTime currentTime = LocalDateTime.now();
        assertThat(digestedFileStore.getLastName(), equalTo("disruptions.json"));
        assertThat(digestedFileStore.getLastFile(), equalTo("{\"disruptions\":" +
            "[{\"line\":\"Bakerloo\"," +
            "\"startTime\":\"" + currentTime.format(DateTimeFormatter.ofPattern("HH:mm")) + "\"," +
            "\"endTime\":\"" + currentTime.format(DateTimeFormatter.ofPattern("HH:mm")) + "\"," +
            "\"status\":\"Runaway Train\"" +
            "}]}"
        ));
    }

    @Test
    public void savesTranslatedDataToFileStoreForTwoSuccessiveIngestJobs() throws Exception {
        LocalDateTime currentTime = LocalDateTime.now();
        Ingester job = new Ingester(tflMockWebServer.getUrl("/"), rawFileStore, digestedFileStore, new FakeClock(LocalDateTime.now()), ongoingDisruptionsStore);
        job.ingest();

        Ingester secondJob = new Ingester(tflMockWebServer.getUrl("/"), rawFileStore, digestedFileStore, new FakeClock(LocalDateTime.now().plusMinutes(10)), ongoingDisruptionsStore);
        secondJob.ingest();

        assertThat(digestedFileStore.getLastName(), equalTo("disruptions.json"));
        assertThat(digestedFileStore.getLastFile(), equalTo("{\"disruptions\":[" +
                "{\"line\":\"Bakerloo\"," +
                "\"startTime\":\"" + currentTime.format(DateTimeFormatter.ofPattern("HH:mm")) + "\"," +
                "\"endTime\":\"" + currentTime.format(DateTimeFormatter.ofPattern("HH:mm")) + "\"," +
                "\"status\":\"Runaway Train\"" +
                "}," +
                "{\"line\":\"Circle\"," +
                "\"startTime\":\"" + currentTime.plusMinutes(10).format(DateTimeFormatter.ofPattern("HH:mm")) + "\"," +
                "\"endTime\":\"" + currentTime.plusMinutes(10).format(DateTimeFormatter.ofPattern("HH:mm")) + "\"," +
                "\"status\":\"Leaves on the Line\"" +
                "}" +
                "]}"));
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

    public static class FakeClock implements Clock {
        private final LocalDateTime time;

        FakeClock(LocalDateTime time) {
            this.time = time;
        }

        @Override
        public LocalDateTime getCurrentTime() {
            return time;
        }
    }
}
