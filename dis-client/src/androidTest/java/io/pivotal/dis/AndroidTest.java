package io.pivotal.dis;

import android.app.Activity;
import android.support.test.espresso.ViewInteraction;
import android.test.ActivityInstrumentationTestCase2;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class AndroidTest<T extends Activity> extends ActivityInstrumentationTestCase2<T> {

  protected Screen screen = new Screen();

  public AndroidTest(Class<T> activityClass) {
    super(activityClass);
  }

  protected class Screen {
    public void hasText(String text) {
      viewWithText(text).check(matches(isDisplayed()));
    }

    public void hasNoText(String text) {
      viewWithText(text).check(doesNotExist());
    }

    public ViewInteraction viewWithText(String text) {
      return onView(withText(text));
    }
  }
}