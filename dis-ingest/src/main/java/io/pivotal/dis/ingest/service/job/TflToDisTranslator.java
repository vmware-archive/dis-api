package io.pivotal.dis.ingest.service.job;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TflToDisTranslator {

    public static String digestTflData(String tflData) throws JSONException {
        JSONArray lines = new JSONArray(tflData);
        JSONArray disruptedLines = new JSONArray();

        for (int i = 0; i < lines.length(); i++) {
            JSONObject line = lines.getJSONObject(i);
            JSONObject lineStatus = line.getJSONArray("lineStatuses").getJSONObject(0);

            String statusSeverityDescription = lineStatus.getString("statusSeverityDescription");
            if (!statusSeverityDescription.equals("Good Service")) {
                JSONObject disruptedLine = new JSONObject();
                disruptedLine.put("line", line.getString("name"));
                disruptedLine.put("status", statusSeverityDescription);
                disruptedLine.put("startTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
                disruptedLines.put(disruptedLine);
            }
        }

        JSONObject response = new JSONObject();
        response.put("disruptions", disruptedLines);
        return response.toString();
    }

}
