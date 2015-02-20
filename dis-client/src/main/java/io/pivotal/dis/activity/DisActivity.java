package io.pivotal.dis.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.inject.Inject;
import java.util.List;

import io.pivotal.dis.R;
import io.pivotal.dis.lines.ILinesClient;
import io.pivotal.dis.lines.LinesDataSource;

public class DisActivity extends GuiceActivity {

  @Inject
  public ILinesClient linesClient;

  public ListView lines;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dis);

    lines = (ListView) findViewById(R.id.lines);

    final LinesDataSource linesDataSource = new LinesDataSource(linesClient);

    AsyncTask<Void, Void, List<String>> displayDisruptions = new AsyncTask<Void, Void, List<String>>() {
      @Override
      protected List<String> doInBackground(Void... params) {
        try {
          return linesDataSource.getDisruptedLineNames();
        } catch (Exception e) {
          e.printStackTrace();
          throw new RuntimeException(e);
        }
      }

      @Override
      protected void onPostExecute(List<String> disruptedLines) {
        final ArrayAdapter<String> linesAdapter;
        try {
          linesAdapter = new ArrayAdapter<String>(
              DisActivity.this,
              android.R.layout.simple_list_item_1,
              disruptedLines.toArray(new String[] {})
          );

          lines.setAdapter(linesAdapter);
        } catch (Exception e) {
          e.printStackTrace();
          throw new RuntimeException(e);
        }
      }
    };

    displayDisruptions.execute();
  }
}
