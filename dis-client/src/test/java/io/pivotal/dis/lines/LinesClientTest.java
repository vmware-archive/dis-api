package io.pivotal.dis.lines;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.net.SocketTimeoutException;
import java.net.URL;

import io.pivotal.dis.provider.UrlProvider;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18, manifest = "./src/main/AndroidManifest.xml")   // throws NoClassFound exception when emulateSDK > 18
public class LinesClientTest {
  @Test
  public void fetchDisruptedLines_passesThroughServerResponseAsJson() throws Exception {
    MockWebServer mockWebServer = new MockWebServer();
    mockWebServer.enqueue(new MockResponse()
        .setHeader("Content-Type", "application/json;charset=UTF-8")
        .setBody("{\"disruptions\":[]}")
        );

    mockWebServer.play();
    URL serverUrl = mockWebServer.getUrl("");
    LinesClient linesClient = new LinesClient(new UrlProvider(Robolectric.application, serverUrl, serverUrl));
    JSONObject lines = linesClient.fetchDisruptedLines();
    assertThat(lines.getJSONArray("disruptions").length(), equalTo(0));
  }

  @Test(expected = SocketTimeoutException.class)
  public void fetchDisruptedLines_ThrowsSocketTimeoutExceptionAfterSpecifiedReadTimeout() throws Exception {
    MockWebServer mockWebServer = new MockWebServer();
    mockWebServer.enqueue(new MockResponse()
            .setHeader("Content-Type", "application/json;charset=UTF-8")
            .setBody("{\"disruptions\":[]}")
            .setBodyDelayTimeMs(11000)
    );

    mockWebServer.play();
    URL serverUrl = mockWebServer.getUrl("");
    LinesClient linesClient = new LinesClient(new UrlProvider(Robolectric.application, serverUrl, serverUrl), 0, 100);
    JSONObject lines = linesClient.fetchDisruptedLines();
    assertThat(lines.getJSONArray("disruptions").length(), equalTo(0));
  }
}