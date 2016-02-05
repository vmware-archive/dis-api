package io.pivotal.dis.lines;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LinesDataSource {

  private LinesClient client;

  public LinesDataSource(LinesClient client) {
    this.client = client;
  }

  public List<Line> getDisruptedLines() throws Exception {
    JSONObject response = client.fetchDisruptedLines();

    JSONArray disruptions = response.getJSONArray("disruptions");
    List<Line> lines = new ArrayList<>();
    for (int i = 0; i < disruptions.length(); i++) {
      JSONObject line = disruptions.getJSONObject(i);
      if (line.has("startTime")) {
        lines.add(new Line(line.getString("line"), line.getString("status"), line.getString("startTime"), line.getString("endTime"), line.getString("earliestEndTime"), line.getString("latestEndTime")));
      } else {
        lines.add(new Line(line.getString("line"), line.getString("status")));
      }
    }
    return lines;
  }

}
