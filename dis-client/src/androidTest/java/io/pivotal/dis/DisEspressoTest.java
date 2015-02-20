package io.pivotal.dis;

import android.support.test.espresso.ViewInteraction;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import com.google.inject.AbstractModule;

import java.io.IOException;

import io.pivotal.dis.activity.DisActivity;
import io.pivotal.dis.lines.ILinesClient;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@LargeTest
public class DisEspressoTest extends ActivityInstrumentationTestCase2<DisActivity> {

  private DisEspressoTest screen = this;

  public DisEspressoTest() {
    super(DisActivity.class);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();

    DisApplication.overrideModule(new DisEspressoTestModule());
    getActivity();
  }

  public void testShowsDisruptedLines() throws InterruptedException, IOException {
    screen.hasText("Central");
    screen.hasText("District");
  }

  private void hasText(String text) {
    viewWithText(text).check(matches(isDisplayed()));
  }

  private void hasNoText(String text) {
    viewWithText(text).check(doesNotExist());
  }

  private ViewInteraction viewWithText(String text) {
    return onView(withText(text));
  }

  private class DisEspressoTestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(ILinesClient.class).to(FakeLinesClient.class);
    }
  }
}
