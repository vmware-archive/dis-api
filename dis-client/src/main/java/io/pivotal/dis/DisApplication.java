package io.pivotal.dis;

import android.app.Application;
import android.content.Context;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import io.pivotal.dis.lines.ILinesClient;
import io.pivotal.dis.lines.LinesClient;

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
        bindLinesClient();
        bindDebugProperties();


      } catch (java.io.IOException e) {
        e.printStackTrace();
      }
    }

    private void bindDebugProperties() throws IOException {
      // Controls what debug options are available - see src/debug/res/raw for the version used in debug builds,
      // and src/main/res/raw for the one used in release builds.
      InputStream inputStream = context.getResources().openRawResource(R.raw.debug);
      try {
        Properties properties = new Properties();
        properties.load(new InputStreamReader(inputStream));
        bind(Properties.class).annotatedWith(Names.named("debug")).toInstance(properties);
      }
      finally {
        if (inputStream != null) inputStream.close();
      }
    }

    // Necessary to have as a separate method, since test classes want to override this
    protected void bindLinesClient() {
      bind(ILinesClient.class).to(LinesClient.class);
    }
  }
}
