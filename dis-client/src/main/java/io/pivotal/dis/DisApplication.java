package io.pivotal.dis;

import android.app.Application;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.net.MalformedURLException;
import java.net.URL;
import io.pivotal.dis.lines.ILinesClient;
import io.pivotal.dis.lines.LinesClient;

public class DisApplication extends Application {

  private static Injector injector;

  @Override
  public void onCreate() {
    super.onCreate();
  }

  public static Injector getInjector() {
    if (injector == null) injector = Guice.createInjector(new DisModule());
    return injector;
  }

  public static void overrideInjectorModule(AbstractModule module) {
    injector = Guice.createInjector(module);
  }

  private static class DisModule extends AbstractModule {
    @Override
    protected void configure() {
      try {
        bind(ILinesClient.class).toInstance(new LinesClient(new URL("http://dis-server.cfapps.io")));
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
