package io.pivotal.dis;

import com.google.inject.AbstractModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import io.pivotal.dis.activity.DisActivity;
import io.pivotal.dis.lines.ILinesClient;

public class DisEspressoTest extends AndroidTest<DisActivity> {

  public DisEspressoTest() {
    super(DisActivity.class);
  }

  public void testShowsNoDisruptions_whenThereAreNoDisruptions() {
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(new FakeLinesClient(Collections.EMPTY_LIST)));
    getActivity();

    screen.hasText("No disruptions");
  }

  public void testShowsDisruptedLines_whenThereAreDisruptions() throws InterruptedException, IOException {
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(new FakeLinesClient(Arrays.asList("Central", "District"))));
    getActivity();

    screen.hasNoText("No disruptions");

    screen.hasText("Central");
    screen.hasText("District");

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
