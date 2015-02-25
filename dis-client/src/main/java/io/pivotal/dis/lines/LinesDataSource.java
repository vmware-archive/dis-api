package io.pivotal.dis.lines;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LinesDataSource {

  private ILinesClient client;

  public LinesDataSource(ILinesClient client) {
    this.client = client;
  }

  public List<Line> getDisruptedLines() throws Exception {
    JSONObject response = client.fetchDisruptedLines();

    JSONArray disruptions = response.getJSONArray("disruptions");
    List<Line> lines = new ArrayList<>();
    for (int i = 0; i < disruptions.length(); i++) {
      lines.add(new Line(disruptions.getJSONObject(i).getString("line"), disruptions.getJSONObject(i).getString("status")));
    }

    return lines;
  }

}
