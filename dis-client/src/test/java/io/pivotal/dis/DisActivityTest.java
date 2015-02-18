package io.pivotal.dis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.pivotal.dis.activity.DisActivity;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
public class DisActivityTest {

    @Test
    public void testSomething() throws Exception {
        assertTrue(Robolectric.buildActivity(DisActivity.class).create().get() != null);
    }
}
