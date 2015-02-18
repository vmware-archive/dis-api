package io.pivotal.dis.activity;

import android.app.Activity;
import android.os.Bundle;

import io.pivotal.dis.R;

public class DisActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dis);
  }
}
