package io.pivotal.dis.controller;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.embedded.RedisServer;

import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class TflProxyControllerTest {

    private WebTester webTester;
    private RedisServer redisServer;

    @Before
    public void setup() throws Exception {
        redisServer = new RedisServer();
        redisServer.start();

        String tflLineStatusJson = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("line_mode_tube_status.json"));

        URI redisUri = new URI("redis://localhost:" + redisServer.getPort());
        try (Jedis jedis = new Jedis(redisUri)) {
            jedis.set("line_status", tflLineStatusJson);
        }

        webTester = WebTester.build(redisUri);
    }

    @After
    public void tearDown() throws Exception {
        try {
            redisServer.stop();
        } finally {
            webTester.close();
        }
    }

    @Test
    public void getLineDisruptions_returnsDisruptedLineWithStatus() throws Exception {
        String contentAsString = webTester.get("/lines/disruptions");
        JSONArray disruptions = new JSONObject(contentAsString).getJSONArray("disruptions");
        assertThat(disruptions.length(), equalTo(1));
        assertThat(disruptions.getJSONObject(0).getString("line"), equalTo("Bakerloo"));
        assertThat(disruptions.getJSONObject(0).getString("status"), equalTo("Runaway Train"));
    }

}
