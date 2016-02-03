package io.pivotal.dis;

import android.content.Context;

import io.pivotal.dis.lines.LinesClient;

class DisEspressoTestModule extends DisApplication.DisModule {

  private LinesClient fakeLinesClient;

  DisEspressoTestModule(Context context, LinesClient fakeLinesClient) {
    super(context);
    this.fakeLinesClient = fakeLinesClient;
  }

  @Override
  protected void bindLinesClient() {
    bind(LinesClient.class).toInstance(fakeLinesClient);
  }
}
