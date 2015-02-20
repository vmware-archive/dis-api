package io.pivotal.dis;

import android.test.suitebuilder.annotation.LargeTest;

import com.google.inject.AbstractModule;

import java.io.IOException;

import io.pivotal.dis.activity.DisActivity;
import io.pivotal.dis.lines.ILinesClient;

public class DisEspressoTest extends AndroidTest<DisActivity> {

  public DisEspressoTest() {
    super(DisActivity.class);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();

    DisApplication.overrideInjectorModule(new DisEspressoTestModule());
    getActivity();
  }

  public void testShowsDisruptedLines() throws InterruptedException, IOException {
    screen.hasText("Central");
    screen.hasText("District");
  }

  private class DisEspressoTestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(ILinesClient.class).to(FakeLinesClient.class);
    }
  }
}
