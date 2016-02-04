package io.pivotal.dis;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;

import java.util.Collections;

import io.pivotal.dis.lines.Line;

import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static io.pivotal.dis.Macchiato.clickOn;

public class DisEspressoTest<T extends Activity> extends ActivityInstrumentationTestCase2<T> {

    public DisEspressoTest(Class<T> activityClass) {
        super(activityClass);
    }

    @Override
    public void setUp() throws Exception {
      super.setUp();

      DisApplication.overrideInjectorModule(
              new DisEspressoTestModule(getInstrumentation().getTargetContext(),
                                        new FakeLinesClient(Collections.<Line>emptyList())));
    }

    protected SharedPreferences.Editor getEditor() {
        return getSharedPreferences().edit();
    }

    protected SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
    }

    protected void clickTestMode() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        clickOn("Test mode");
    }
}
