package io.pivotal.dis.lines;

import com.google.inject.Inject;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;

import io.pivotal.dis.provider.UrlProvider;

public class LinesClient implements ILinesClient {
  public static final int DEFAULT_CONNECT_TIMEOUT_MS = 1500;
  public static final int DEFAULT_READ_TIMEOUT_MS = 10000;

  private final UrlProvider urlProvider;
  private final int connectTimeout;
  private final int readTimeout;

  @Inject
  public LinesClient(UrlProvider urlProvider) {
    this(urlProvider, DEFAULT_CONNECT_TIMEOUT_MS, DEFAULT_READ_TIMEOUT_MS);
  }

  // Package-private as used only for tests
  LinesClient(UrlProvider urlProvider, int connectTimeout, int readTimeout) {
    this.urlProvider = urlProvider;
    this.connectTimeout = connectTimeout;
    this.readTimeout = readTimeout;
  }

  @Override
  public JSONObject fetchDisruptedLines() throws Exception {
    URLConnection connection = urlProvider.getUrl().openConnection();
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
