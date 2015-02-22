package io.pivotal.dis;

import android.support.test.espresso.ViewInteraction;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

public class Macchiato {

    public static void hasText(String text) {
        viewWithText(text).check(matches(isDisplayed()));
    }

    public static void hasNoText(String text) { viewWithText(text).check(matches(not(isDisplayed()))); }

    public static ViewInteraction viewWithText(String text) {
        return onView(withText(text));
    }

}
