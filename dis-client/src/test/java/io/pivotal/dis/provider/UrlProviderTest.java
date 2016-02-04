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
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.net.MalformedURLException;
import java.net.URL;

import io.pivotal.dis.DisApplication;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, manifest = "./src/main/AndroidManifest.xml")
public class UrlProviderTest {

  @Inject
  private UrlProvider urlProvider;

  @Inject
  private Context context;

  private SharedPreferences.Editor edit;

  @Before
  public void setup() throws Exception {
    Injector injector = DisApplication.getInjector(RuntimeEnvironment.application);
    injector.injectMembers(this);

    edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
  }

  @After
  public void tearDown() {
    edit.remove("testMode")
        .apply();
  }

  @Test
  public void returnsRealUrlWhenTestModePreferenceIsNotSet() throws MalformedURLException {
    assertThat(urlProvider.getUrl(), equalTo(new URL("http://pivotal-london-dis-digest.s3.amazonaws.com/disruptions.json")));
  }

  @Test
  public void returnsRealUrlWhenTestModePreferenceIsFalse() throws MalformedURLException {
    edit.putBoolean("testMode", false)
        .apply();

    assertThat(urlProvider.getUrl(), equalTo(new URL("http://pivotal-london-dis-digest.s3.amazonaws.com/disruptions.json")));
  }

  @Test
  public void returnsTestUrlWhenTestModePreferenceIsTrue() throws MalformedURLException {
    edit.putBoolean("testMode", true)
        .apply();

    assertThat(urlProvider.getUrl(), equalTo(new URL("http://pivotal-london-dis-digest-test.s3.amazonaws.com/disruptions.json")));
  }

}