package io.pivotal.dis.activity;

import android.view.Menu;
import io.pivotal.dis.R;

public class DisActivity extends AbstractDisActivity {

    @Override
    protected void showTestMode(Menu menu) {
        boolean testModeEnabled = sharedPreferences.getBoolean("testMode", false);
        menu.findItem(R.id.test_mode).setChecked(testModeEnabled);
    }
}
