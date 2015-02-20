package io.pivotal.dis.lines;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class LinesClient implements ILinesClient {
  private URL serverUrl;
  private URL disruptedLinesUrl;

  public LinesClient(URL serverUrl) throws MalformedURLException {
    this.serverUrl = serverUrl;
    this.disruptedLinesUrl = new URL(serverUrl, "/lines/disruptions");
  }

  @Override
  public JSONObject fetchDisruptedLines() throws Exception {
    InputStream inputStream = disruptedLinesUrl.openConnection().getInputStream();
    InputStreamReader reader = new InputStreamReader(inputStream);
    StringBuilder stringBuilder = new StringBuilder();
    BufferedReader bufferedReader = new BufferedReader(reader);
    String read = bufferedReader.readLine();
    while (read != null) {
      stringBuilder.append(read);
      read = bufferedReader.readLine();
    }
    String jsonString = stringBuilder.toString();
    return new JSONObject(jsonString);
  }
}
