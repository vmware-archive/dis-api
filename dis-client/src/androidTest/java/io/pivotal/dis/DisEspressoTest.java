package io.pivotal.dis;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.CheckBox;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import io.pivotal.dis.activity.AbstractDisActivity;
import io.pivotal.dis.activity.DisActivity;
import io.pivotal.dis.lines.LinesClient;
import io.pivotal.dis.lines.Line;
import org.json.JSONObject;

import javax.inject.Provider;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static io.pivotal.dis.Macchiato.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class DisEspressoTest extends ActivityInstrumentationTestCase2<DisActivity> {

  public DisEspressoTest() {
    super(DisActivity.class);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(getInstrumentation().getTargetContext(),
            new FakeLinesClient(Collections.<Line>emptyList())));
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext()).edit();
    editor.remove("testMode");
    editor.apply();
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

  public void testShowsTestModeButtonInActionBar() {
    getActivity();
    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    onView(withText("Test mode")).check(matches(allOf(isDisplayed())));
  }

  public void testClickingTestModeWhenUncheckedChecksTestModeCheckboxInMenu() {
    getActivity();
    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    clickOn("Test mode");
    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    onView(allOf(isAssignableFrom(CheckBox.class), hasSibling(withChild(withText("Test mode"))))).check(matches(isChecked()));
  }

  public void testClickingTestModeWhenCheckedUnchecksTestModeCheckboxInMenu() {
    getActivity();
    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    clickOn("Test mode");
    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    clickOn("Test mode");
    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    onView(allOf(isAssignableFrom(CheckBox.class), hasSibling(withChild(withText("Test mode"))))).check(matches(isNotChecked()));
  }

  public void testClickingTestModeWhenUncheckedSetsTestModePrefToTrue() {
    getActivity();
    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    clickOn("Test mode");
    assertThat(PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext()).getBoolean("testMode", false), equalTo(true));
  }

  public void testClickingTestModeWhenCheckedSetsTestModePrefToFalse() {
    getActivity();
    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    clickOn("Test mode");
    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    clickOn("Test mode");
    assertThat(PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext()).getBoolean("testMode", true), equalTo(false));
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

  public static ViewAction swipeDown() {
    return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.TOP_CENTER,
            GeneralLocation.BOTTOM_CENTER, Press.FINGER);
  }

  private class SlowLinesClient implements LinesClient {
    @Override
    public JSONObject fetchDisruptedLines() throws Exception {
      throw new SocketTimeoutException("Fetching lines timed out");
    }
  }

  public void testShowsErrorMessageIfLoadingDisruptedLinesTimesOut() {
    LinesClient slowLinesClient = new SlowLinesClient();
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(getInstrumentation().getTargetContext(), slowLinesClient));
    getActivity();
    assertHasText("Couldn't retrieve data from server :(");
  }

  private class DisEspressoTestModule extends DisApplication.DisModule {

    private LinesClient fakeLinesClient;

    private DisEspressoTestModule(Context context, LinesClient fakeLinesClient) {
      super(context);
      this.fakeLinesClient = fakeLinesClient;
    }

    @Override
    protected void bindLinesClient() {
      bind(LinesClient.class).toInstance(fakeLinesClient);
    }
  }
}
