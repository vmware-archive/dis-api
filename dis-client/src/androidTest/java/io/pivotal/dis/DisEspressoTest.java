package io.pivotal.dis;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.espresso.NoMatchingViewException;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.CheckBox;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import io.pivotal.dis.activity.DisActivity;
import io.pivotal.dis.lines.ILinesClient;
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

  public void testShowsRefreshButtonInActionBar() {
    getActivity();
    onView(withId(R.id.refresh_disruptions)).check(matches(allOf(isDisplayed(), isClickable())));
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

  public void testProgressBarGoneAfterContentLoaded() {
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(getInstrumentation().getTargetContext(), new FakeLinesClient(Arrays.asList(new Line("Central", "Severe Delays"), new Line("District", "Part Suspended")))));
    getActivity();
    onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())));
  }

  public void testClickingRefreshButtonFetchesUpdatedDisruptions() {
    FakeLinesClient linesClient = new FakeLinesClient(Collections.<Line>emptyList());
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(getInstrumentation().getTargetContext(), linesClient));
    getActivity();
    assertHasText("No disruptions");
    linesClient.setDisruptedLines(Arrays.asList(new Line("Central", "Severe Delays"), new Line("District", "Part Suspended")));
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

  public void testShowsErrorMessageIfLoadingDisruptedLinesTimesOut() {
    ILinesClient slowLinesClient = new SlowLinesClient();
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(getInstrumentation().getTargetContext(), slowLinesClient));
    getActivity();
    assertHasText("Couldn't retrieve data from server :(");
  }

  public void ignore_testTestModeNotAvailableWhenDebugEnableTestModePropertyIsNotTrue() {
    Injector injector = DisApplication.getInjector(getInstrumentation().getTargetContext());
    Provider<Properties> provider = injector.getBinding(Key.get(Properties.class, Names.named("debug"))).getProvider();
    Properties properties = provider.get();
    properties.setProperty("debug.enable.testMode", "false");
    getActivity();
    openActionBarOverflowOrOptionsMenu(getActivity().getApplicationContext());
    try {
      onView(withText("Test mode"));
      fail("Test mode button must not be present");
    }
    catch(NoMatchingViewException e) {
      // Pass
    }
  }

  private class DisEspressoTestModule extends DisApplication.DisModule {

    private ILinesClient fakeLinesClient;

    private DisEspressoTestModule(Context context, ILinesClient fakeLinesClient) {
      super(context);
      this.fakeLinesClient = fakeLinesClient;
    }

    @Override
    protected void bindLinesClient() {
      bind(ILinesClient.class).toInstance(fakeLinesClient);
    }
  }
}
