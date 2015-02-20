package io.pivotal.dis.lines;

import org.json.JSONException;
import org.json.JSONObject;

public interface ILinesClient {
  public JSONObject fetchDisruptedLines() throws Exception;
}
