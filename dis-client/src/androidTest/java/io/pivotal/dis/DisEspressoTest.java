package io.pivotal.dis;

import android.test.ActivityInstrumentationTestCase2;

import com.google.inject.AbstractModule;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Collections;

import io.pivotal.dis.activity.DisActivity;
import io.pivotal.dis.lines.ILinesClient;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static io.pivotal.dis.Macchiato.assertDoesNotHaveText;
import static io.pivotal.dis.Macchiato.assertHasText;
import static io.pivotal.dis.Macchiato.clickOn;
import static org.hamcrest.Matchers.allOf;

public class DisEspressoTest extends ActivityInstrumentationTestCase2<DisActivity> {

  public DisEspressoTest() {
    super(DisActivity.class);
  }

  public void testShowsNoDisruptions_whenThereAreNoDisruptions() {
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(new FakeLinesClient(Collections.EMPTY_LIST)));
    getActivity();
    assertHasText("No disruptions");
  }

  public void testShowsDisruptedLines_whenThereAreDisruptions() throws InterruptedException, IOException {
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(new FakeLinesClient(Arrays.asList("Central", "District"))));
    getActivity();

    assertDoesNotHaveText("No disruptions");

    assertHasText("Central");
    assertHasText("District");

  }

  public void testShowsRefreshButtonInActionBar() throws InterruptedException {
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(new FakeLinesClient(Collections.<String>emptyList())));
    getActivity();
    onView(withId(R.id.refresh_disruptions)).check(matches(allOf(isDisplayed(), isClickable())));
  }

  public void testClickingRefreshButtonFetchesUpdatedDisruptions() {
    FakeLinesClient linesClient = new FakeLinesClient(Collections.EMPTY_LIST);
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(linesClient));
    getActivity();
    assertHasText("No disruptions");
    linesClient.setDisruptedLines(Arrays.asList("Central", "District"));
    assertHasText("No disruptions");
    clickOn(R.id.refresh_disruptions);
    assertHasText("Central");
    assertHasText("District");
  }

  private class SlowLinesClient implements ILinesClient {
    @Override
    public JSONObject fetchDisruptedLines() throws Exception {
      throw new SocketTimeoutException("Fetching lines timed out");
    }
  }

  public void testShowsErrorMessageIfLoadingDisruptedLinesTimesOut() throws Exception {
    ILinesClient slowLinesClient = new SlowLinesClient();
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(slowLinesClient));
    getActivity();
    assertHasText("Refresh failed");
  }

  private class DisEspressoTestModule extends AbstractModule {

    private ILinesClient fakeLinesClient;

    private DisEspressoTestModule(ILinesClient fakeLinesClient) {
      this.fakeLinesClient = fakeLinesClient;
    }

    @Override
    protected void configure() {
      bind(ILinesClient.class).toInstance(fakeLinesClient);
    }
  }
}
