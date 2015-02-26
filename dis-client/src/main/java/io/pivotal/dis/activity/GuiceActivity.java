package io.pivotal.dis.activity;

import android.app.Activity;
import android.os.Bundle;

import io.pivotal.dis.DisApplication;

public class GuiceActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    DisApplication.getInjector(this.getApplicationContext()).injectMembers(this);
  }
}
