package io.pivotal.dis.service;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class FakeDisruptedLinesService {
    private JSONArray disruptedLinesJson;

    public FakeDisruptedLinesService() {
        try {
            InputStream is = getClass().getResourceAsStream("/fake_response.json");
            disruptedLinesJson = new JSONArray(IOUtils.toString(is));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONArray getDisruptedLinesJson() {
        return disruptedLinesJson;
    }

    public void setDisruptedLinesJson(JSONArray disruptedLines) {
        this.disruptedLinesJson = disruptedLines;
    }
}
