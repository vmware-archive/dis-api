package io.pivotal.dis.service;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import io.pivotal.dis.provider.MockTimeProvider;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class DisruptedLinesServiceTest {

    private MockWebServer mockWebServer;
    private MockTimeProvider timeProvider;
    private DisruptedLinesService service;

    @Before
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();

        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("line_mode_tube_status.json"))));
        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("another_line_mode_tube_status.json"))));
        mockWebServer.play();

        timeProvider = new MockTimeProvider();
        timeProvider.setTime(LocalDateTime.now());

        service = new DisruptedLinesService(timeProvider, mockWebServer.getUrl(""));
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void makesOnlyOneCallToTflApiForTwoSuccessiveRequests() throws Exception {
        service.getDisruptedLinesJson();
        timeProvider.addSeconds(59);
        service.getDisruptedLinesJson();
        RecordedRequest firstRequest = mockWebServer.takeRequest(1, TimeUnit.MILLISECONDS);
        assertThat(firstRequest, is(notNullValue()));
        RecordedRequest secondRequest = mockWebServer.takeRequest(1, TimeUnit.MILLISECONDS);
        assertThat(secondRequest, is(nullValue()));
    }

    @Test
    public void returnsSameJsonIfRequestsAreLessThanOneMinuteApart() throws Exception {
        JSONArray firstJson = service.getDisruptedLinesJson();
        timeProvider.addSeconds(59);
        JSONArray secondJson = service.getDisruptedLinesJson();

        assertThat(firstJson, equalTo(secondJson));
    }

    @Test
    public void callsTflApiSecondTimeIfRequestsAreOverMinuteApart() throws Exception {
        service.getDisruptedLinesJson();
        timeProvider.addSeconds(61);
        service.getDisruptedLinesJson();
        RecordedRequest firstRequest = mockWebServer.takeRequest(1, TimeUnit.MILLISECONDS);
        assertThat(firstRequest, is(notNullValue()));
        RecordedRequest secondRequest = mockWebServer.takeRequest(1, TimeUnit.MILLISECONDS);
        assertThat(secondRequest, is(notNullValue()));
    }

    @Test
    public void returnsDifferentJsonIfRequestsAreOverMinuteApart() throws Exception {
        JSONArray firstJson = service.getDisruptedLinesJson();
        timeProvider.addSeconds(61);
        JSONArray secondJson = service.getDisruptedLinesJson();

        assertThat(firstJson, not(equalTo(secondJson)));
    }

}