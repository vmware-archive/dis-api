package io.pivotal.dis.service;

import org.json.JSONArray;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.net.URI;

public class DisruptedLinesService {

    private final URI redisUri;

    public DisruptedLinesService(URI redisUri) {
        this.redisUri = redisUri;
    }

    public JSONArray getDisruptedLinesJson() throws IOException {
        try (Jedis jedis = new Jedis(redisUri)) {
            String lineStatus = jedis.get("line_status");
            return new JSONArray(lineStatus);
        }
    }

}
