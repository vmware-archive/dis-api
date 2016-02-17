package io.pivotal.dis.ingest.job;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import io.pivotal.dis.ingest.domain.Digest;
import io.pivotal.dis.ingest.domain.DisruptedLine;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TflDigestorTest {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    public static final int LATEST_END_TIME_BUFFER = 30 + 10;
    public static final int END_TIME_BUFFER = 30;
    public static final int EARLIEST_END_TIME_BUFFER = 30 - 10;

    @Test
    public void digestTflData_returnsDisruptedLines_WithCorrectDisruptionStartTimes() throws Exception {
        LocalDateTime currentTime = LocalDateTime.now();

        String earlyTflLineStatus = loadFixture("line_mode_tube_status");
        String earlierDigest = new TflDigestor(earlyTflLineStatus,
                                                currentTime,
                                                Optional.empty())
                                        .digest();

        JsonAdapter<Digest> digestsAdapter = getDigestsAdapter();

        Digest digest = digestsAdapter.fromJson(earlierDigest);
        List<DisruptedLine> disruptions = digest.getDisruptions();

        assertThat(disruptions.size(), equalTo(1));

        DisruptedLine disruptedLine = disruptions.get(0);
        assertLineData(disruptedLine, currentTime,  "Bakerloo", "Minor Delays", "#FFFFFF", "#AE6118");

        LocalDateTime tenMinutesLater = currentTime.plusMinutes(10);
        String laterTflLineStatus = loadFixture("line_mode_tube_status_2");

        String laterDigest = new TflDigestor(laterTflLineStatus,
                                                tenMinutesLater,
                                                Optional.of(earlierDigest))
                                        .digest();

        digest = digestsAdapter.fromJson(laterDigest);
        disruptions = digest.getDisruptions();

        assertThat(disruptions.size(), equalTo(2));

        disruptedLine = disruptions.get(0);
        assertLineData(disruptedLine, currentTime, "Bakerloo", "Minor Delays", "#FFFFFF", "#AE6118");

        disruptedLine = disruptions.get(1);
        assertLineData(disruptedLine, tenMinutesLater, "Circle", "Minor Delays", "#113892", "#F8D42D");
    }

    @Test
    public void digestTflData_predictsEndTimeForEachStatus() throws Exception {
        LocalDateTime currentTime = LocalDateTime.now();

        String allStatusesJson = loadFixture("endTimeTest");

        String digestJson = new TflDigestor(allStatusesJson,
                                                currentTime,
                                                Optional.empty())
                                        .digest();


        JsonAdapter<Digest> digestsAdapter = getDigestsAdapter();

        Digest digest = digestsAdapter.fromJson(digestJson);
        List<DisruptedLine> disruptions = digest.getDisruptions();

        assertThat(disruptions.size(), equalTo(6));

        assertThat(disruptions.get(0).getEndTime(), equalTo(currentTime.plusMinutes(30).format(TIME_FORMAT)));
        assertThat(disruptions.get(1).getEndTime(), equalTo(currentTime.plusMinutes(60).format(TIME_FORMAT)));
        assertThat(disruptions.get(2).getEndTime(), equalTo(currentTime.plusDays(1).format(TIME_FORMAT)));
        assertThat(disruptions.get(3).getEndTime(), equalTo(currentTime.plusDays(1).format(TIME_FORMAT)));
        assertThat(disruptions.get(4).getEndTime(), equalTo(currentTime.plusMinutes(120).format(TIME_FORMAT)));
        assertThat(disruptions.get(5).getEndTime(), equalTo(currentTime.format(TIME_FORMAT)));
    }

    @Test
    public void digestTflData_predictsEarliestPossibleEndTimeForEachStatus() throws Exception {
        LocalDateTime currentTime = LocalDateTime.now();

        String allStatusesJson = loadFixture("endTimeTest");

        String digestJson = new TflDigestor(allStatusesJson,
                                            currentTime,
                                            Optional.empty())
                                    .digest();

        JsonAdapter<Digest> digestsAdapter = getDigestsAdapter();

        Digest digest = digestsAdapter.fromJson(digestJson);
        List<DisruptedLine> disruptions = digest.getDisruptions();

        assertThat(disruptions.size(), equalTo(6));

        assertThat(disruptions.get(0).getEarliestEndTime(), equalTo(currentTime.plusMinutes(30 - 10).format(TIME_FORMAT)));
        assertThat(disruptions.get(1).getEarliestEndTime(), equalTo(currentTime.plusMinutes(60 - 20).format(TIME_FORMAT)));
        assertThat(disruptions.get(2).getEarliestEndTime(), equalTo(currentTime.plusHours(24 - 8).format(TIME_FORMAT)));
        assertThat(disruptions.get(3).getEarliestEndTime(), equalTo(currentTime.plusHours(24 - 8).format(TIME_FORMAT)));
        assertThat(disruptions.get(4).getEarliestEndTime(), equalTo(currentTime.plusMinutes(120 - 40).format(TIME_FORMAT)));
        assertThat(disruptions.get(5).getEarliestEndTime(), equalTo(currentTime.format(TIME_FORMAT)));
    }

    @Test
    public void digestTflData_predictsLatestPossibleEndTimeForEachStatus() throws Exception {
        LocalDateTime currentTime = LocalDateTime.now();

        String allStatusesJson = loadFixture("endTimeTest");

        String digestJson = new TflDigestor(allStatusesJson,
                currentTime,
                Optional.empty())
                .digest();

        JsonAdapter<Digest> digestsAdapter = getDigestsAdapter();

        Digest digest = digestsAdapter.fromJson(digestJson);
        List<DisruptedLine> disruptions = digest.getDisruptions();

        assertThat(disruptions.size(), equalTo(6));

        assertThat(disruptions.get(0).getLatestEndTime(), equalTo(currentTime.plusMinutes(30 + 10).format(TIME_FORMAT)));
        assertThat(disruptions.get(1).getLatestEndTime(), equalTo(currentTime.plusMinutes(60 + 20).format(TIME_FORMAT)));
        assertThat(disruptions.get(2).getLatestEndTime(), equalTo(currentTime.plusHours(24 + 8).format(TIME_FORMAT)));
        assertThat(disruptions.get(3).getLatestEndTime(), equalTo(currentTime.plusHours(24 + 8).format(TIME_FORMAT)));
        assertThat(disruptions.get(4).getLatestEndTime(), equalTo(currentTime.plusMinutes(120 + 40).format(TIME_FORMAT)));
        assertThat(disruptions.get(5).getLatestEndTime(), equalTo(currentTime.format(TIME_FORMAT)));
    }

    private String loadFixture(final String name) throws IOException {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(name + ".json"));
    }

    private JsonAdapter<Digest> getDigestsAdapter() {
        Moshi moshi = new Moshi.Builder().build();
        return moshi.adapter(Digest.class);
    }

    private void assertLineData(DisruptedLine disruptedLine,
                                LocalDateTime currentTime,
                                String expectedLine,
                                String expectedStatus,
                                String expectedForegroundColor,
                                String expectedBackgroundColor) {

        assertThat(disruptedLine.getLine(), equalTo(expectedLine));

        assertThat(disruptedLine.getForegroundColor(), equalTo(expectedForegroundColor));
        assertThat(disruptedLine.getBackgroundColor(), equalTo(expectedBackgroundColor));

        assertThat(disruptedLine.getStartTime(), equalTo(currentTime.format(TIME_FORMAT)));
        assertThat(disruptedLine.getEndTime(), equalTo(currentTime.plusMinutes(END_TIME_BUFFER).format(TIME_FORMAT)));
        assertThat(disruptedLine.getEarliestEndTime(), equalTo(currentTime.plusMinutes(EARLIEST_END_TIME_BUFFER).format(TIME_FORMAT)));
        assertThat(disruptedLine.getLatestEndTime(), equalTo(currentTime.plusMinutes(LATEST_END_TIME_BUFFER).format(TIME_FORMAT)));

        assertThat(epochMillisToTimeString(disruptedLine.getStartTimestamp()), equalTo(currentTime.format(TIME_FORMAT)));
        assertThat(epochMillisToTimeString(disruptedLine.getEndTimestamp()), equalTo(currentTime.plusMinutes(END_TIME_BUFFER).format(TIME_FORMAT)));
        assertThat(epochMillisToTimeString(disruptedLine.getEarliestEndTimestamp()), equalTo(currentTime.plusMinutes(EARLIEST_END_TIME_BUFFER).format(TIME_FORMAT)));
        assertThat(epochMillisToTimeString(disruptedLine.getLatestEndTimestamp()), equalTo(currentTime.plusMinutes(LATEST_END_TIME_BUFFER).format(TIME_FORMAT)));

        assertThat(disruptedLine.getStatus(), equalTo(expectedStatus));
    }

    private String epochMillisToTimeString(Long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC).format(TIME_FORMAT);
    }

}
