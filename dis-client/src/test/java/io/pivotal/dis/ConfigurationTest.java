package io.pivotal.dis;

import android.content.SharedPreferences;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.net.MalformedURLException;
import java.net.URL;

import io.pivotal.dis.lines.LinesClient;
import io.pivotal.dis.lines.LinesClientImpl;
import roboguice.RoboGuice;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, manifest = "./src/main/AndroidManifest.xml")
public class ConfigurationTest {

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    @Named("realUrl")
    URL apiUrl;

    @Inject
    @Named("testUrl")
    URL testUrl;

    @Inject
    LinesClient linesClient;

    @Before
    public void setup() {
        Guice.createInjector(new DisApplication.DisModule(RuntimeEnvironment.application)).injectMembers(this);
    }

    @Test
    public void providesSharedPreferences() {
        assertThat(sharedPreferences, notNullValue());
    }

    @Test
    public void providesUrls() throws MalformedURLException {
        assertThat(apiUrl, equalTo(new URL("http://pivotal-london-dis-digest.s3.amazonaws.com/disruptions.json")));
        assertThat(testUrl, equalTo(new URL("http://pivotal-london-dis-digest-test.s3.amazonaws.com/disruptions.json")));
    }

    @Test
    public void bindsLinesClient() {
        assertThat(linesClient, instanceOf(LinesClientImpl.class));
    }
}