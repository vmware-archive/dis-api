package io.pivotal.dis;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

import java.net.URL;

import io.pivotal.dis.lines.LinesClient;
import io.pivotal.dis.lines.LinesClientImpl;

public class DisApplication extends Application {

  private static Injector injector;

  @Override
  public void onCreate() {
    super.onCreate();
  }

  public static Injector getInjector(Context context) {
    if (injector == null) injector = Guice.createInjector(new DisModule(context));
    return injector;
  }

  public static void overrideInjectorModule(AbstractModule... modules) {
    injector = Guice.createInjector(modules);
  }

  public static class DisModule extends AbstractModule {
    private final Context context;

    public DisModule(Context context) {

      this.context = context;
    }

    @Override
    protected void configure() {
      try {
        bind(Context.class).toInstance(context);
        bind(URL.class).annotatedWith(Names.named("realUrl")).toInstance(new URL("http://pivotal-london-dis-digest.s3.amazonaws.com/disruptions.json"));
        bind(URL.class).annotatedWith(Names.named("testUrl")).toInstance(new URL("http://pivotal-london-dis-digest-test.s3.amazonaws.com/disruptions.json"));
        bind(SharedPreferences.class).toInstance(PreferenceManager.getDefaultSharedPreferences(context));
        bindLinesClient();
      } catch (java.io.IOException e) {
        e.printStackTrace();
      }
    }

    // Necessary to have as a separate method, since test classes want to override this
    protected void bindLinesClient() {
      bind(LinesClient.class).to(LinesClientImpl.class);
    }
  }
}
