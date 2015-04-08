package io.pivotal.dis.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.inject.Inject;
import com.google.inject.Injector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.net.MalformedURLException;
import java.net.URL;

import io.pivotal.dis.DisApplication;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18, manifest = "./src/main/AndroidManifest.xml")
public class UrlProviderTest {

  @Inject
  private UrlProvider urlProvider;

  @Inject
  private Context context;

  @Before
  public void setup() throws Exception {
    Injector injector = DisApplication.getInjector(Robolectric.application);
    injector.injectMembers(this);
  }

  @After
  public void tearDown() {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor edit = preferences.edit();
    edit.remove("testMode");
    edit.apply();
  }

  @Test
  public void returnsRealUrlWhenTestModePreferenceIsNotSet() throws MalformedURLException {
    assertThat(urlProvider.getUrl(), equalTo(new URL("http://pivotal-london-dis-digest.s3.amazonaws.com/disruptions.json")));
  }

  @Test
  public void returnsRealUrlWhenTestModePreferenceIsFalse() throws MalformedURLException {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor edit = preferences.edit();
    edit.putBoolean("testMode", false);
    edit.apply();

    assertThat(urlProvider.getUrl(), equalTo(new URL("http://pivotal-london-dis-digest.s3.amazonaws.com/disruptions.json")));
  }

  @Test
  public void returnsTestUrlWhenTestModePreferenceIsTrue() throws MalformedURLException {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor edit = preferences.edit();
    edit.putBoolean("testMode", true);
    edit.apply();

    assertThat(urlProvider.getUrl(), equalTo(new URL("http://pivotal-london-dis-digest-test.s3.amazonaws.com/disruptions.json")));
  }
}