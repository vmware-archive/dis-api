package io.pivotal.dis;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.CheckBox;

import io.pivotal.dis.activity.DisActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static io.pivotal.dis.Macchiato.clickOn;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TestModeEspressoTest extends DisEspressoTest<DisActivity> {

    public TestModeEspressoTest() {
        super(DisActivity.class);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext()).edit();
        editor.remove("testMode");
        editor.apply();
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
}
