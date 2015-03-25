package io.pivotal.dis.controller;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class TflProxyControllerTest {

    private MockWebServer mockWebServer;
    private WebTester webTester;

    @Before
    public void setup() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("line_mode_tube_status.json"))
                ));
        mockWebServer.play();

        webTester = WebTester.build(mockWebServer.getUrl(""));
    }

    @After
    public void tearDown() throws Exception {
        try {
            mockWebServer.shutdown();
        } finally {
            webTester.close();
        }
    }

    @Test
    public void getLineDisruptions_returnsDisruptedLines() throws Exception {
        String contentAsString = webTester.get("/lines/disruptions");
        JSONArray disruptions = new JSONObject(contentAsString).getJSONArray("disruptions");
        assertThat(disruptions.length(), equalTo(1));
        assertThat(disruptions.getJSONObject(0).getString("line"), equalTo("Bakerloo"));
    }

    @Test
    public void getLineDisruptions_returnsDisruptedLinesWithStatus() throws Exception {
        String contentAsString = webTester.get("/lines/disruptions");
        JSONArray disruptions = new JSONObject(contentAsString).getJSONArray("disruptions");
        assertThat(disruptions.length(), equalTo(1));
        assertThat(disruptions.getJSONObject(0).getString("status"), equalTo("Runaway Train"));
    }

}
