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
    public void digestTflData_returnsDisruptedLines() throws Exception {
        String tflLineStatus = loadFixture("line_mode_tube_status");

        String disLineStatus = TflToDisTranslator.digestTflData(tflLineStatus);

        LocalDateTime currentTime = LocalDateTime.now();

        assertThat(disLineStatus, equalTo("{\"disruptions\":[{\"line\":\"Bakerloo\"," +
                "\"startTime\":\"" +
                currentTime.format(DateTimeFormatter.ofPattern("HH:mm")) +
                "\"," +
                "\"status\":\"Runaway Train\"" +
                "}]}"));
    }

    private String loadFixture(final String name) throws IOException {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(name + ".json"));
    }

}
