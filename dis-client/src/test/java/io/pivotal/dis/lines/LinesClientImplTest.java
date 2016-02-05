package io.pivotal.dis.lines;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.pivotal.dis.provider.UrlProvider;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, manifest = "./src/main/AndroidManifest.xml")
public class LinesClientImplTest {

    @Test
    public void fetchDisruptedLines_passesThroughServerResponseAsAListOfLines() throws Exception {
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse()
                        .setHeader("Content-Type", "application/json;charset=UTF-8")
                        .setBody("{\"disruptions\":[" +
                                "{\"earliestEndTime\":\"14:00\"," +
                                "\"endTime\":\"15:00\"," +
                                "\"latestEndTime\":\"16:00\"," +
                                "\"line\":\"Bakerloo\"," +
                                "\"startTime\":\"10:00\"," +
                                "\"status\":\"Leaves on the platforms\"}" +
                                "]}")
        );

        mockWebServer.start();
        URL serverUrl = mockWebServer.url("").url();
        LinesClientImpl linesClient = new LinesClientImpl(new UrlProvider(RuntimeEnvironment.application, serverUrl, serverUrl));
        List<Line> lines = linesClient.fetchDisruptedLines();
        assertThat(lines.get(0), equalTo(new Line("Bakerloo", "Leaves on the platforms", "10:00", "15:00", "14:00", "16:00")));
    }

    @Test(expected = SocketTimeoutException.class)
    public void fetchDisruptedLines_ThrowsSocketTimeoutExceptionAfterSpecifiedReadTimeout() throws Exception {
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse()
                        .setHeader("Content-Type", "application/json;charset=UTF-8")
                        .setBody("{\"disruptions\":[]}")
                        .setBodyDelay(11, TimeUnit.SECONDS)
        );

        mockWebServer.start();
        URL serverUrl = mockWebServer.url("").url();
        LinesClientImpl linesClient = new LinesClientImpl(new UrlProvider(RuntimeEnvironment.application, serverUrl, serverUrl), 0, 100);
        linesClient.fetchDisruptedLines();
    }
}