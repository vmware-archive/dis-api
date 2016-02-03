package io.pivotal.dis;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.test.ActivityInstrumentationTestCase2;

import io.pivotal.dis.activity.DisActivity;
import io.pivotal.dis.lines.LinesClient;
import io.pivotal.dis.lines.Line;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Collections;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static io.pivotal.dis.Macchiato.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class DisruptionsListEspressoTest extends DisEspressoTest<DisActivity> {

  public DisruptionsListEspressoTest() {
    super(DisActivity.class);
  }

  public void testShowsNoDisruptions_whenThereAreNoDisruptions() {
    getActivity();
    assertHasText("No disruptions");
  }

  public void testShowsDisruptedLineNames_whenThereAreDisruptions() {
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(getInstrumentation().getTargetContext(),
            new FakeLinesClient(Arrays.asList(new Line("Central", "Severe Delays"), new Line("District", "Part Suspended")))));
    getActivity();

    assertHasText("Central");
    assertHasText("District");
    assertDoesNotHaveText("No disruptions");
  }

  public void testShowsDisruptedLineStatuses_whenThereAreDisruptions() {
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(getInstrumentation().getTargetContext(),
            new FakeLinesClient(Arrays.asList(new Line("Central", "Severe Delays"), new Line("District", "Part Suspended")))));
    getActivity();

    assertHasText("Severe Delays");
    assertHasText("Part Suspended");
    assertDoesNotHaveText("No disruptions");
  }

  public void testShowsDisruptionStartTimes_whenThereAreDisruptions() {
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(getInstrumentation().getTargetContext(),
            new FakeLinesClient(Arrays.asList(new Line("Central", "Severe Delays", "12:30"), new Line("District", "Part Suspended", "14:30")))));
    getActivity();

    onView(allOf(hasSibling(withText("Severe Delays")), withId(R.id.line_disruption_started_time)))
            .check(matches(allOf(isDisplayed(), withText("Started: 12:30"))));

    onView(allOf(hasSibling(withText("Part Suspended")), withId(R.id.line_disruption_started_time)))
            .check(matches(allOf(isDisplayed(), withText("Started: 14:30"))));

    assertDoesNotHaveText("No disruptions");
  }


  public void testDoesNotShowDisruptionStartTimes_forDisruptionsWithNoStartTime() {
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(getInstrumentation().getTargetContext(),
            new FakeLinesClient(Arrays.asList(new Line("Central", "Severe Delays"), new Line("District", "Part Suspended")))));
    getActivity();

    onView(allOf(hasSibling(withText("Severe Delays")), withId(R.id.line_disruption_started_time)))
            .check(matches(allOf(not(isDisplayed()))));

    onView(allOf(hasSibling(withText("Part Suspended")), withId(R.id.line_disruption_started_time)))
            .check(matches(allOf(not(isDisplayed()))));

    assertDoesNotHaveText("No disruptions");
  }



  public void testSwipingToRefreshFetchesUpdatedDisruptions() {
    FakeLinesClient linesClient = new FakeLinesClient(Collections.<Line>emptyList());
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(getInstrumentation().getTargetContext(), linesClient));
    getActivity();
    assertHasText("No disruptions");
    linesClient.setDisruptedLines(Arrays.asList(new Line("Central", "Severe Delays"), new Line("District", "Part Suspended")));
    assertHasText("No disruptions");

    onView(withId(R.id.swipe_layout)).perform(swipeDown());

    assertHasText("Central");
    assertHasText("District");
  }

  public ViewAction swipeDown() {
    return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.TOP_CENTER,
            GeneralLocation.BOTTOM_CENTER, Press.FINGER);
  }

  public void testShowsErrorMessageIfLoadingDisruptedLinesTimesOut() {
    LinesClient slowLinesClient = new SlowLinesClient();
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(getInstrumentation().getTargetContext(), slowLinesClient));
    getActivity();
    assertHasText("Couldn't retrieve data from server :(");
  }

  private class SlowLinesClient implements LinesClient {
    @Override
    public JSONObject fetchDisruptedLines() throws Exception {
      throw new SocketTimeoutException("Fetching lines timed out");
    }
  }
}
