package io.pivotal.dis;

import android.test.ActivityInstrumentationTestCase2;

import com.google.inject.AbstractModule;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import io.pivotal.dis.activity.DisActivity;
import io.pivotal.dis.lines.ILinesClient;

import static io.pivotal.dis.Macchiato.hasNoText;
import static io.pivotal.dis.Macchiato.hasText;

public class DisEspressoTest extends ActivityInstrumentationTestCase2<DisActivity> {

  public DisEspressoTest() {
    super(DisActivity.class);
  }

  public void testShowsNoDisruptions_whenThereAreNoDisruptions() {
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(new FakeLinesClient(Collections.EMPTY_LIST)));
    getActivity();

    hasText("No disruptions");
  }

  public void testShowsDisruptedLines_whenThereAreDisruptions() throws InterruptedException, IOException {
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(new FakeLinesClient(Arrays.asList("Central", "District"))));
    getActivity();

    hasNoText("No disruptions");

    hasText("Central");
    hasText("District");

  }

  private class DisEspressoTestModule extends AbstractModule {
    private FakeLinesClient fakeLinesClient;

    private DisEspressoTestModule(FakeLinesClient fakeLinesClient) {
      this.fakeLinesClient = fakeLinesClient;
    }

    @Override
    protected void configure() {
      bind(ILinesClient.class).toInstance(fakeLinesClient);
    }
  }
}
