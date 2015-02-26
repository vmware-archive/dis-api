package io.pivotal.dis;

import android.support.test.espresso.ViewInteraction;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

public class Macchiato {

  public static void assertHasText(String text) {
    viewWithText(text).check(matches(isDisplayed()));
  }

  public static void assertDoesNotHaveText(String text) {
    viewWithText(text).check(matches(not(isDisplayed())));
  }

  private static ViewInteraction viewWithId(int id) {
    return onView(withId(id));
  }

  private static ViewInteraction viewWithText(String text) {
    return onView(withText(text));
  }

  public static ViewInteraction clickOn(int id) {
    return viewWithId(id).perform(click());
  }

  public static ViewInteraction clickOn(String text) {
    return viewWithText(text).perform(click());
  }
}
