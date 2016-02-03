package io.pivotal.dis.lines;

import org.json.JSONException;
import org.json.JSONObject;

public interface LinesClient {
  public JSONObject fetchDisruptedLines() throws Exception;
}
