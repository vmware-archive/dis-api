package io.pivotal.dis.ingest.job;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import io.pivotal.dis.ingest.domain.Digest;
import io.pivotal.dis.ingest.domain.DisruptedLine;
import io.pivotal.dis.ingest.job.TflDigestor;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TflDigestorTest {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

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
        assertLineData(disruptedLine, currentTime, "Bakerloo", "Minor Delays");

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
        assertLineData(disruptedLine, currentTime, "Bakerloo", "Minor Delays");

        disruptedLine = disruptions.get(1);
        assertLineData(disruptedLine, tenMinutesLater, "Piccadilly", "Minor Delays");
    }

    private void assertLineData(DisruptedLine disruptedLine,
                                LocalDateTime currentTime,
                                String expectedLine,
                                String expectedStatus) {

        assertThat(disruptedLine.getLine(), equalTo(expectedLine));
        assertThat(disruptedLine.getStartTime(), equalTo(currentTime.format(TIME_FORMAT)));
        assertThat(disruptedLine.getEndTime(), equalTo(currentTime.plusMinutes(30).format(TIME_FORMAT)));
        assertThat(disruptedLine.getStatus(), equalTo(expectedStatus));
    }

    @Test
    public void digestTflData_predictsEndTimeForEachStatus() throws Exception {
        String allStatusesJson = loadFixture("endTimeTest");

        String digestJson = new TflDigestor(allStatusesJson,
                                                LocalDateTime.now(),
                                                Optional.empty())
                                        .digest();

        LocalDateTime currentTime = LocalDateTime.now();

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

    private String loadFixture(final String name) throws IOException {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(name + ".json"));
    }

    private JsonAdapter<Digest> getDigestsAdapter() {
        Moshi moshi = new Moshi.Builder().build();
        return moshi.adapter(Digest.class);
    }

}
