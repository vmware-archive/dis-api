package io.pivotal.dis.controller;

import io.pivotal.dis.provider.TflUrlProvider;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TflProxyController {

    @Autowired
    private TflUrlProvider tflUrlProvider;

    @RequestMapping("/lines/disruptions")
    public Map<String, List<Map<String, String>>> lineDisruptions() throws IOException {
        InputStream inputStream = tflUrlProvider.get().openConnection().getInputStream();
        String jsonString = IOUtils.toString(inputStream);
        
        JSONArray lines = new JSONArray(jsonString);
        List<Map<String, String>> disruptedLines = new ArrayList<>();

        for (int i = 0; i < lines.length(); i++) {
            JSONObject line = lines.getJSONObject(i);
            JSONObject lineStatus = line.getJSONArray("lineStatuses").getJSONObject(0);

            if (!lineStatus.get("statusSeverityDescription").equals("Good Service")) {
                HashMap<String, String> disruptedLine = new HashMap<>();
                disruptedLine.put("line", line.getString("name"));
                disruptedLines.add(disruptedLine);
            }
        }

        Map<String, List<Map<String, String>>> response = new HashMap<>();
        response.put("disruptions", disruptedLines);
        return response;
    }
}
