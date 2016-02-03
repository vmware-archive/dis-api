package io.pivotal.dis;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import java.util.Collections;

import io.pivotal.dis.lines.Line;

public class DisEspressoTest<T extends Activity> extends ActivityInstrumentationTestCase2<T> {

    public DisEspressoTest(Class<T> activityClass) {
        super(activityClass);
    }

    @Override
    public void setUp() throws Exception {
      super.setUp();
      DisApplication.overrideInjectorModule(new DisEspressoTestModule(getInstrumentation().getTargetContext(),
              new FakeLinesClient(Collections.<Line>emptyList())));
    }
}
