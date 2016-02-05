package io.pivotal.dis.lines;

import com.google.inject.Inject;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.List;

import io.pivotal.dis.provider.UrlProvider;

public class LinesClientImpl implements LinesClient {
  public static final int DEFAULT_CONNECT_TIMEOUT_MS = 1500;
  public static final int DEFAULT_READ_TIMEOUT_MS = 10000;

  private final UrlProvider urlProvider;
  private final int connectTimeout;
  private final int readTimeout;

  @Inject
  public LinesClientImpl(UrlProvider urlProvider) {
    this(urlProvider, DEFAULT_CONNECT_TIMEOUT_MS, DEFAULT_READ_TIMEOUT_MS);
  }

  // Package-private as used only for tests
  LinesClientImpl(UrlProvider urlProvider, int connectTimeout, int readTimeout) {
    this.urlProvider = urlProvider;
    this.connectTimeout = connectTimeout;
    this.readTimeout = readTimeout;
  }

  @Override
  public List<Line> fetchDisruptedLines() throws Exception{
    String json = fetchDisruptedLinesAsJson();

    Moshi moshiBuilder = new Moshi.Builder().build();
    JsonAdapter<Digest> digestAdapter = moshiBuilder.adapter(Digest.class);

    Digest digest = digestAdapter.fromJson(json);
    return digest.getDisruptions();
  }

  private String fetchDisruptedLinesAsJson() throws Exception {
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

    return stringBuilder.toString();
  }
}
