package io.pivotal.dis;

import android.app.Application;

import com.google.inject.AbstractModule;

import roboguice.RoboGuice;

public class DisApplication extends Application {

  private static AbstractModule overrideModule;

  @Override
  public void onCreate() {
    super.onCreate();
  }

  public static void overrideModule(AbstractModule module) {
    overrideModule = module;
  }

  public void setupInjection() {
    if (DisApplication.overrideModule != null) {
      RoboGuice.overrideApplicationInjector(this, DisApplication.overrideModule);
    }
  }
}
