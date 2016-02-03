package io.pivotal.dis.ingest.service.job;
import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TflToDisTranslatorTest {

    @Test
    public void digestTflData_returnsDisruptedLinesWithCorrectDisruptionStartTimes() throws Exception {
        String earlyTflLineStatus = loadFixture("line_mode_tube_status");
        String earlierDigest = TflToDisTranslator.digestTflData(earlyTflLineStatus, null, LocalDateTime.now());

        LocalDateTime currentTime = LocalDateTime.now();

        JSONObject digestJson = new JSONObject(earlierDigest);
        JSONArray disruptionsList = digestJson.getJSONArray("disruptions");

        assertThat(disruptionsList.length(), equalTo(1));
        assertThat(disruptionsList.getJSONObject(0).get("line"), equalTo("Bakerloo"));
        assertThat(disruptionsList.getJSONObject(0).get("startTime"), equalTo(currentTime.format(DateTimeFormatter.ofPattern("HH:mm"))));
        assertThat(disruptionsList.getJSONObject(0).get("endTime"), equalTo(currentTime.plusMinutes(30).format(DateTimeFormatter.ofPattern("HH:mm"))));
        assertThat(disruptionsList.getJSONObject(0).get("status"), equalTo("Minor Delays"));

        LocalDateTime tenMinutesLater = currentTime.plusMinutes(10);
        String laterTflLineStatus = loadFixture("line_mode_tube_status_2");
        String laterDigest = TflToDisTranslator.digestTflData(laterTflLineStatus, earlierDigest, tenMinutesLater);

        digestJson = new JSONObject(laterDigest);
        disruptionsList = digestJson.getJSONArray("disruptions");

        assertThat(disruptionsList.length(), equalTo(2));

        assertThat(disruptionsList.getJSONObject(0).get("line"), equalTo("Bakerloo"));
        assertThat(disruptionsList.getJSONObject(0).get("startTime"), equalTo(currentTime.format(DateTimeFormatter.ofPattern("HH:mm"))));
        assertThat(disruptionsList.getJSONObject(0).get("endTime"), equalTo(currentTime.plusMinutes(30).format(DateTimeFormatter.ofPattern("HH:mm"))));
        assertThat(disruptionsList.getJSONObject(0).get("status"), equalTo("Minor Delays"));

        assertThat(disruptionsList.getJSONObject(1).get("line"), equalTo("Piccadilly"));
        assertThat(disruptionsList.getJSONObject(1).get("startTime"), equalTo(tenMinutesLater.format(DateTimeFormatter.ofPattern("HH:mm"))));
        assertThat(disruptionsList.getJSONObject(1).get("endTime"), equalTo(tenMinutesLater.plusMinutes(30).format(DateTimeFormatter.ofPattern("HH:mm"))));
        assertThat(disruptionsList.getJSONObject(1).get("status"), equalTo("Minor Delays"));
    }

    @Test
    public void digestTflData_predictsEndTimeForEachStatus() throws IOException, JSONException {
        String allStatusesJson = loadFixture("endTimeTest");
        String digest = TflToDisTranslator.digestTflData(allStatusesJson, null, LocalDateTime.now());

        LocalDateTime currentTime = LocalDateTime.now();

        JSONObject digestJson = new JSONObject(digest);
        JSONArray disruptionsList = digestJson.getJSONArray("disruptions");

        assertThat(disruptionsList.length(), equalTo(6));
        assertThat(disruptionsList.getJSONObject(0).get("endTime"), equalTo(currentTime.plusMinutes(30).format(DateTimeFormatter.ofPattern("HH:mm"))));
        assertThat(disruptionsList.getJSONObject(1).get("endTime"), equalTo(currentTime.plusMinutes(60).format(DateTimeFormatter.ofPattern("HH:mm"))));
        assertThat(disruptionsList.getJSONObject(2).get("endTime"), equalTo(currentTime.plusDays(1).format(DateTimeFormatter.ofPattern("HH:mm"))));
        assertThat(disruptionsList.getJSONObject(3).get("endTime"), equalTo(currentTime.plusDays(1).format(DateTimeFormatter.ofPattern("HH:mm"))));
        assertThat(disruptionsList.getJSONObject(4).get("endTime"), equalTo(currentTime.plusMinutes(120).format(DateTimeFormatter.ofPattern("HH:mm"))));
        assertThat(disruptionsList.getJSONObject(5).get("endTime"), equalTo(currentTime.format(DateTimeFormatter.ofPattern("HH:mm"))));
    }

    private String loadFixture(final String name) throws IOException {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(name + ".json"));
    }

}
