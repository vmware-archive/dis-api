package io.pivotal.dis;

import org.json.JSONException;
import org.json.JSONObject;

import io.pivotal.dis.lines.ILinesClient;

public class FakeLinesClient implements ILinesClient {
  @Override
  public JSONObject fetchDisruptedLines() throws JSONException {
    return new JSONObject("{ \"disruptions\": [ { \"line\": \"Central\" }, { \"line\": \"District\" } ] }");
  }
}
