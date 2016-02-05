package io.pivotal.dis;

import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;

import org.junit.Assert;

import java.io.FileNotFoundException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.pivotal.dis.activity.DisActivity;
import io.pivotal.dis.lines.Line;
import io.pivotal.dis.lines.LinesClient;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static io.pivotal.dis.Macchiato.assertDoesNotHaveText;
import static io.pivotal.dis.Macchiato.assertHasText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

public class DisruptionsListEspressoTest extends DisEspressoTest<DisActivity> {

    public DisruptionsListEspressoTest() {
        super(DisActivity.class);
    }

    public void testShowsNoDisruptions_whenThereAreNoDisruptions() {
        getActivity();
        assertHasText("No disruptions");
    }

    public void testShowsDisruptedLineNames_whenThereAreDisruptions() {
        mockDisruptionsWithTimes();

        getActivity();

        assertHasText("Central");
        assertHasText("District");
        assertDoesNotHaveText("No disruptions");
    }

    public void testShowsDisruptedLineStatuses_whenThereAreDisruptions() {
        mockDisruptionsWithTimes();

        getActivity();

        assertHasText("Severe Delays");
        assertHasText("Part Suspended");
        assertDoesNotHaveText("No disruptions");
    }


    public void testShowsDisruptionStartTimes_whenThereAreDisruptions() {
        mockDisruptionsWithTimes();

        getActivity();

        onView(allOf(hasSibling(withText("Part Suspended")), withId(R.id.line_disruption_started_time)))
                .check(matches(allOf(isDisplayed(), withText("Started: 14:30"))));

        assertDoesNotHaveText("No disruptions");
    }

    public void testShowsDisruptionEndTimesWithARange_whenThereAreDisruptions() {
        mockDisruptionsWithTimes();

        getActivity();

        onView(allOf(hasSibling(withText("Severe Delays")), withId(R.id.line_disruption_end_time)))
                .check(matches(allOf(isDisplayed(), withText("Ends: 13:10 - 13:50"))));

        onView(allOf(hasSibling(withText("Part Suspended")), withId(R.id.line_disruption_end_time)))
                .check(matches(allOf(isDisplayed(), withText("Ends: 15:25 - 15:35"))));
    }

    public void testDoesNotShowDisruptionStartTimes_forDisruptionsWithNoStartTime() {
        mockDisruptionsWithoutTimes();

        getActivity();

        onView(allOf(hasSibling(withText("Severe Delays")), withId(R.id.line_disruption_started_time)))
                .check(matches(allOf(not(isDisplayed()))));

        onView(allOf(hasSibling(withText("Part Suspended")), withId(R.id.line_disruption_started_time)))
                .check(matches(allOf(not(isDisplayed()))));

        assertDoesNotHaveText("No disruptions");
    }


    public void testSwipingToRefreshFetchesUpdatedDisruptions() {
        FakeLinesClient linesClient = new FakeLinesClient(Collections.<Line>emptyList());
        DisApplication.overrideInjectorModule(
                new DisEspressoTestModule(
                        getInstrumentation().getTargetContext(),
                        linesClient));

        getActivity();

        assertHasText("No disruptions");

        linesClient.setDisruptedLines(
                Arrays.asList(
                        new Line("Central", "Severe Delays"),
                        new Line("District", "Part Suspended")));

        assertHasText("No disruptions");

        refresh();

        assertHasText("Central");
        assertHasText("District");
    }


    public void testShowsErrorMessageIfLoadingDisruptedLinesTimesOut() {
        LinesClient slowLinesClient = new SlowLinesClient();

        DisApplication.overrideInjectorModule(
                new DisEspressoTestModule(
                        getInstrumentation().getTargetContext(),
                        slowLinesClient));

        getActivity();

        assertHasText("Couldn't retrieve data from server :(");
    }

    public void testDoesNotCrashWhenS3IsDown() {
        try {
            S3DownLinesClient s3DownLinesClient = new S3DownLinesClient();

            DisApplication.overrideInjectorModule(
                    new DisEspressoTestModule(
                            getInstrumentation().getTargetContext(),
                            s3DownLinesClient));

            getActivity();

            refresh();

            assertHasText("Couldn't retrieve data from server :(");

        } catch (Exception e) {
            Assert.fail("Process should not throw any exception");
        }
    }

    private void mockDisruptionsWithTimes() {
        DisApplication.overrideInjectorModule(
                new DisEspressoTestModule(
                        getInstrumentation().getTargetContext(),
                        new FakeLinesClient(
                                Arrays.asList(
                                        new Line("Central", "Severe Delays", "12:30", "13:30", "13:10", "13:50"),
                                        new Line("District", "Part Suspended", "14:30", "15:30", "15:25", "15:35")))));
    }

    private void mockDisruptionsWithoutTimes() {
        DisApplication.overrideInjectorModule(
                new DisEspressoTestModule(
                        getInstrumentation().getTargetContext(),
                        new FakeLinesClient(
                                Arrays.asList(
                                        new Line("Central", "Severe Delays"),
                                        new Line("District", "Part Suspended")))));
    }

    private void refresh() {
        onView(withId(R.id.swipe_layout)).perform(swipeDown());
    }

    private ViewAction swipeDown() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.TOP_CENTER,
                GeneralLocation.BOTTOM_CENTER, Press.FINGER);
    }


    private static class SlowLinesClient implements LinesClient {
        @Override
        public List<Line> fetchDisruptedLines() throws Exception {
            throw new SocketTimeoutException("Fetching lines timed out");
        }
    }

    private class S3DownLinesClient implements LinesClient {
        @Override
        public List<Line> fetchDisruptedLines() throws Exception {
            throw new FileNotFoundException("S3 is down");
        }
    }
}
