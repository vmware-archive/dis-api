package io.pivotal.dis.ingest.service;

import com.amazonaws.util.json.JSONException;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import io.pivotal.dis.ingest.config.OngoingDisruptionsStore;
import io.pivotal.dis.ingest.domain.Digest;
import io.pivotal.dis.ingest.domain.DisruptedLine;
import io.pivotal.dis.ingest.domain.tfl.Line;
import io.pivotal.dis.ingest.domain.tfl.LineStatus;
import io.pivotal.dis.ingest.service.job.Clock;
import io.pivotal.dis.ingest.service.job.Ingester;
import io.pivotal.dis.ingest.service.store.FileStore;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.squareup.moshi.Types.newParameterizedType;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class IngesterTest {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final MockWebServer tflMockWebServer = new MockWebServer();
    private final MockFileStore rawFileStore = new MockFileStore();
    private final MockFileStore digestedFileStore = new MockFileStore();
    private final OngoingDisruptionsStore ongoingDisruptionsStore = new OngoingDisruptionsStore();

    @Before
    public void prepareServer() throws Exception {
        JsonAdapter<List<Line>> moshiTflLinesAdapter = moshiTflLinesAdapter();

        List<Line> tflLines = new ArrayList<>();
        tflLines.add(stubLine("Bakerloo", "Runaway Train"));

        tflMockWebServer.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setBody(moshiTflLinesAdapter.toJson(tflLines)));

        tflLines = new ArrayList<>();
        tflLines.add(stubLine("Bakerloo", "Runaway Train"));
        tflLines.add(stubLine("Circle", "Leaves on the Line"));

        tflMockWebServer.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setBody(moshiTflLinesAdapter.toJson(tflLines)));

        tflMockWebServer.play();
    }

    @Test
    public void savesTflDataToFileStore() throws IOException, JSONException {
        Clock clockNow = new FakeClock(LocalDateTime.now());

        Ingester job = createIngester(clockNow);
        job.ingest();

        assertLastRawFileCreated(clockNow);

        List<Line> lines = getLastRawFileContent();

        assertThat(lines.get(0), equalTo(stubLine("Bakerloo", "Runaway Train")));
    }

    @Test
    public void savesTflDataToFileStoreForTwoSuccessiveIngestJobs() throws IOException {
        Clock clockNow = new FakeClock(LocalDateTime.now());

        Ingester job = createIngester(clockNow);
        job.ingest();

        assertLastRawFileCreated(clockNow);

        List<Line> lines = getLastRawFileContent();

        assertThat(lines.get(0), equalTo(stubLine("Bakerloo", "Runaway Train")));


        Clock clockInTenMinutesTime = new FakeClock(LocalDateTime.now().plusMinutes(10));

        Ingester secondJob = createIngester(clockInTenMinutesTime);
        secondJob.ingest();

        assertLastRawFileCreated(clockInTenMinutesTime);

        lines = getLastRawFileContent();

        assertThat(lines.get(0), equalTo(stubLine("Bakerloo", "Runaway Train")));
        assertThat(lines.get(1), equalTo(stubLine("Circle", "Leaves on the Line")));
    }

    @Test
    public void savesTranslatedDataToFileStore() throws Exception {
        Clock clockNow = new FakeClock(LocalDateTime.now());

        Ingester job = createIngester(clockNow);
        job.ingest();

        assertDigestedFileCreated();

        Digest digest = getLastDigest();

        List<DisruptedLine> disruptions = digest.getDisruptions();

        assertThat(disruptions, hasSize(1));

        LocalDateTime currentTime = LocalDateTime.now();
        assertLineData(disruptions.get(0), currentTime, "Bakerloo", "Runaway Train");
    }

    @Test
    public void savesTranslatedDataToFileStoreForTwoSuccessiveIngestJobs() throws Exception {
        Clock clockNow = new FakeClock(LocalDateTime.now());

        Ingester job = createIngester(clockNow);
        job.ingest();

        Clock clockInTenMinutesTime = new FakeClock(LocalDateTime.now().plusMinutes(10));

        Ingester secondJob = createIngester(clockInTenMinutesTime);
        secondJob.ingest();

        assertDigestedFileCreated();

        Digest digest = getLastDigest();

        List<DisruptedLine> disruptions = digest.getDisruptions();

        assertThat(disruptions, hasSize(2));

        LocalDateTime currentTime = LocalDateTime.now();
        assertLineData(disruptions.get(0), currentTime, "Bakerloo", "Runaway Train");
        assertLineData(disruptions.get(1), currentTime.plusMinutes(10), "Circle", "Leaves on the Line");
    }


    private Line stubLine(String name, String...lineStatus) {
        Line line = new Line(name, stubLineStatuses(lineStatus));
        return line;
    }

    private List<LineStatus> stubLineStatuses(String... lineStatus) {
        return asList(lineStatus).stream()
                .map(l -> new LineStatus(l))
                .collect(toList());
    }

    private void assertLineData(DisruptedLine disruptedLine,
                                LocalDateTime currentTime,
                                String expectedLine,
                                String expectedStatus) {

        assertThat(disruptedLine.getLine(), equalTo(expectedLine));
        assertThat(disruptedLine.getStartTime(), equalTo(currentTime.format(TIME_FORMAT)));
        assertThat(disruptedLine.getEndTime(), equalTo(currentTime.format(TIME_FORMAT)));
        assertThat(disruptedLine.getStatus(), equalTo(expectedStatus));
    }

    private Ingester createIngester(Clock clock) {
        return new Ingester(
                tflMockWebServer.getUrl("/"),
                rawFileStore,
                digestedFileStore,
                clock,
                ongoingDisruptionsStore);
    }

    private JsonAdapter<List<Line>> moshiTflLinesAdapter() {
        return moshi().adapter(newParameterizedType(List.class, Line.class));
    }

    private JsonAdapter<Digest> moshiDigestAdapter() {
        return moshi().adapter(Digest.class);
    }

    private Moshi moshi() {
        return new Moshi.Builder().build();
    }

    private void assertLastRawFileCreated(Clock clock) {
        assertThat(rawFileStore.getLastName(), equalTo("tfl_api_line_mode_status_tube_" + clock.getCurrentTime().format(DATE_TIME_FORMAT) + ".json"));
    }

    private List<Line> getLastRawFileContent() throws IOException {
        String lastFileAsJson = rawFileStore.getLastFile();
        return moshiTflLinesAdapter().fromJson(lastFileAsJson);
    }

    private void assertDigestedFileCreated() {
        assertThat(digestedFileStore.getLastName(), equalTo("disruptions.json"));
    }

    private Digest getLastDigest() throws IOException {
        String lastFileAsJson = digestedFileStore.getLastFile();
        return moshiDigestAdapter().fromJson(lastFileAsJson);
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

    private static class FakeClock implements Clock {
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
