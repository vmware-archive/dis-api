package io.pivotal.dis;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import io.pivotal.dis.lines.ILinesClient;

public class FakeLinesClient implements ILinesClient {
  private List<String> lineNames;

  FakeLinesClient(List<String> lineNames) {
    this.lineNames = lineNames;
  }

  @Override
  public JSONObject fetchDisruptedLines() throws JSONException {
    JSONObject root = new JSONObject();
    JSONArray disruptions = new JSONArray();
    
    for (String lineName : lineNames) {
      disruptions.put(new JSONObject("{ \"line\": \"" + lineName + "\" }"));
    }

    root.put("disruptions", disruptions);
    return root;
  }
}
