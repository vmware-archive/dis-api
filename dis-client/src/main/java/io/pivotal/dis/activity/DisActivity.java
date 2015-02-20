package io.pivotal.dis.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.inject.Inject;

import org.json.JSONException;

import io.pivotal.dis.DisApplication;
import io.pivotal.dis.R;
import io.pivotal.dis.lines.ILinesClient;
import io.pivotal.dis.lines.LinesDataSource;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class DisActivity extends RoboActivity {

  @Inject
  public ILinesClient linesClient;

  @InjectView(R.id.lines)
  public ListView lines;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    ((DisApplication) getApplication()).setupInjection();

    super.onCreate(savedInstanceState);
    setContentView(R.layout.dis);

    final LinesDataSource linesDataSource = new LinesDataSource(linesClient);

    final ArrayAdapter<String> linesAdapter;
    try {
      linesAdapter = new ArrayAdapter<String>(
          this,
          android.R.layout.simple_list_item_1,
          linesDataSource.getDisruptedLineNames().toArray(new String[] {})
      );

      lines.setAdapter(linesAdapter);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
