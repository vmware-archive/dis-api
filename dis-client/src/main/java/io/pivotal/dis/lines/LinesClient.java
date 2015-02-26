package io.pivotal.dis.lines;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class LinesClient implements ILinesClient {
  public static final int DEFAULT_CONNECT_TIMEOUT_MS = 1500;
  public static final int DEFAULT_READ_TIMEOUT_MS = 10000;

  private URL disruptedLinesUrl;
  private final int connectTimeout;
  private final int readTimeout;

  public LinesClient(URL serverUrl) throws MalformedURLException {
    this(serverUrl, DEFAULT_CONNECT_TIMEOUT_MS, DEFAULT_READ_TIMEOUT_MS);
  }

  LinesClient(URL serverUrl, int connectTimeout, int readTimeout) throws MalformedURLException {
    this.disruptedLinesUrl = new URL(serverUrl.getProtocol(), serverUrl.getHost(), serverUrl.getPort(), serverUrl.getPath() + "/lines/disruptions");
    this.connectTimeout = connectTimeout;
    this.readTimeout = readTimeout;
  }

  @Override
  public JSONObject fetchDisruptedLines() throws Exception {
    URLConnection connection = disruptedLinesUrl.openConnection();
    connection.setConnectTimeout(connectTimeout);
    connection.setReadTimeout(readTimeout);
    InputStream inputStream = connection.getInputStream();
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
