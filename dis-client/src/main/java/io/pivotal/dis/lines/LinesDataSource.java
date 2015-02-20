package io.pivotal.dis.lines;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LinesDataSource {

  private ILinesClient client;

  public LinesDataSource(ILinesClient client) {
    this.client = client;
  }

  public List<String> getDisruptedLineNames() throws JSONException {
    JSONObject response = client.fetchDisruptedLines();

    JSONArray disruptions = response.getJSONArray("disruptions");
    List<String> names = new ArrayList<String>();
    for (int i = 0; i < disruptions.length(); i++) {
      names.add(disruptions.getJSONObject(i).getString("line"));
    }

    return names;
  }
}
