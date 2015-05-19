package io.pivotal.dis.ingest.service.job;

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
        assertThat(earlierDigest, equalTo("{\"disruptions\":[{\"line\":\"Bakerloo\"," +
                "\"startTime\":\"" + currentTime.format(DateTimeFormatter.ofPattern("HH:mm")) + "\"," +
                "\"status\":\"Runaway Train\"" +
                "}]}"));

        LocalDateTime tenMinutesLater = currentTime.plusMinutes(10);
        String laterTflLineStatus = loadFixture("line_mode_tube_status_2");
        String laterDigest = TflToDisTranslator.digestTflData(laterTflLineStatus, earlierDigest, tenMinutesLater);
        assertThat(laterDigest, equalTo("{\"disruptions\":[" +
                "{\"line\":\"Bakerloo\"," +
                "\"startTime\":\"" + currentTime.format(DateTimeFormatter.ofPattern("HH:mm")) + "\"," +
                "\"status\":\"Runaway Train\"" +
                "}," +
                "{\"line\":\"Piccadilly\"," +
                "\"startTime\":\"" + tenMinutesLater.format(DateTimeFormatter.ofPattern("HH:mm")) + "\"," +
                "\"status\":\"Flying Trains\"" +
                "}" +
                "]}"));
    }

    private String loadFixture(final String name) throws IOException {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(name + ".json"));
    }

}
