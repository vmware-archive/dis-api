package io.pivotal.dis.controller;

import com.jayway.jsonpath.JsonPath;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import io.pivotal.dis.config.ApplicationConfiguration;
import io.pivotal.dis.provider.TflUrlProvider;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ApplicationConfiguration.class})
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TflProxyControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private TflUrlProvider tflUrlProvider;
    private MockWebServer mockWebServer;

    @Before
    public void setup() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        mockWebServer = new MockWebServer();

        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("line_mode_tube_status.json"))
        ));

        mockWebServer.play();
        URL serverUrl = mockWebServer.getUrl("");
        tflUrlProvider.set(serverUrl);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void getLineDisruptions_returnsDisruptedLines() throws Exception {
        MvcResult result = mockMvc.perform(get("/lines/disruptions")).andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        JSONArray disruptions = new JSONObject(contentAsString).getJSONArray("disruptions");
        assertThat(disruptions.length(), equalTo(1));
        assertThat(disruptions.getJSONObject(0).getString("line"), equalTo("Bakerloo"));
    }

    @Test
    public void getLineDisruptions_returnsDisruptedLinesWithStatus() throws Exception {
        MvcResult result = mockMvc.perform(get("/lines/disruptions")).andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        JSONArray disruptions = new JSONObject(contentAsString).getJSONArray("disruptions");
        assertThat(disruptions.length(), equalTo(1));
        assertThat(disruptions.getJSONObject(0).getString("status"), equalTo("Runaway Train"));
    }
}