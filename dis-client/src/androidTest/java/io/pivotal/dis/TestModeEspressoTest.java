package io.pivotal.dis;

import android.content.SharedPreferences;
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

        SharedPreferences.Editor editor = getEditor();
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

        clickTestMode();

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(allOf(isAssignableFrom(CheckBox.class), hasSibling(withChild(withText("Test mode"))))).check(matches(isChecked()));
    }

    public void testClickingTestModeWhenCheckedUnchecksTestModeCheckboxInMenu() {
        getActivity();

        clickTestMode();
        clickTestMode();

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(allOf(isAssignableFrom(CheckBox.class), hasSibling(withChild(withText("Test mode")))))
                .check(matches(isNotChecked()));
    }

    public void testClickingTestModeWhenUncheckedSetsTestModePrefToTrue() {
        getActivity();

        clickTestMode();

        assertThat(getSharedPreferences().getBoolean("testMode", false), equalTo(true));
    }

    public void testClickingTestModeWhenCheckedSetsTestModePrefToFalse() {
        getActivity();

        clickTestMode();
        clickTestMode();

        assertThat(getSharedPreferences().getBoolean("testMode", true), equalTo(false));
    }


}
