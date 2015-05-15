package io.pivotal.dis.ingest.service.job;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import org.joda.time.DateTime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class TflToDisTranslator {

    public static String digestTflData(String tflData, String previousDigest, LocalDateTime currentTime) throws JSONException {
        JSONArray lines = new JSONArray(tflData);
        HashMap<String, String> ongoingDisruptionStartTimes = getDisruptionStartTimes(previousDigest);
        JSONArray disruptedLines = new JSONArray();
        for (int i = 0; i < lines.length(); i++) {
            JSONObject line = lines.getJSONObject(i);
            JSONObject lineStatus = line.getJSONArray("lineStatuses").getJSONObject(0);

            String statusSeverityDescription = lineStatus.getString("statusSeverityDescription");
            if (!statusSeverityDescription.equals("Good Service")) {
                JSONObject disruptedLine = new JSONObject();

                disruptedLine.put("status", statusSeverityDescription);
                String lineName = line.getString("name");
                disruptedLine.put("line", lineName);
                String startTime = getStartTime(lineName, currentTime, ongoingDisruptionStartTimes);
                disruptedLine.put("startTime", startTime);
                disruptedLines.put(disruptedLine);
            }
        }

        JSONObject response = new JSONObject();
        response.put("disruptions", disruptedLines);
        return response.toString();
    }

    private static JSONArray getPreviousDisruptedLines(String previousDigest) throws JSONException {
        return previousDigest == null ? new JSONArray() : (new JSONObject(previousDigest)).getJSONArray("disruptions");
    }

    private static String getStartTime(String lineName, LocalDateTime currentTime, Map<String, String> ongoingDisruptionStartTimes) {
        if (ongoingDisruptionStartTimes.containsKey(lineName)) {
            return ongoingDisruptionStartTimes.get(lineName);
        } else {
            return currentTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
    }

    private static HashMap<String, String> getDisruptionStartTimes(String previousDigest) throws JSONException {
        JSONArray previousDisruptedLines = getPreviousDisruptedLines(previousDigest);
        HashMap<String, String> disruptionStartTimes = new HashMap<>();
        for (int i = 0; i < previousDisruptedLines.length(); i++) {
            JSONObject line = previousDisruptedLines.getJSONObject(i);
            disruptionStartTimes.put(line.getString("line"), line.getString("startTime"));
        }
        return disruptionStartTimes;
    }
}
