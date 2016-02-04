package io.pivotal.dis.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.net.URL;

public class UrlProvider {
  private final Context context;
  private final URL realUrl;
  private final URL testUrl;

  @Inject
  public UrlProvider(Context context,
                     @Named("realUrl") URL realUrl,
                     @Named("testUrl") URL testUrl) {
    this.context = context;
    this.realUrl = realUrl;
    this.testUrl = testUrl;
  }

  public URL getUrl() {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    if (sharedPreferences.getBoolean("testMode", false)) {
        return testUrl;
    }
    else
      return realUrl;
  }
}
