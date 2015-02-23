package io.pivotal.dis.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.inject.Inject;

import io.pivotal.dis.R;
import io.pivotal.dis.lines.ILinesClient;
import io.pivotal.dis.lines.LinesDataSource;
import io.pivotal.dis.task.DisplayDisruptionsAsyncTask;

public class DisActivity extends GuiceActivity {

  @Inject
  private ILinesClient linesClient;

  private ListView disruptedLinesView;
  private LinesDataSource linesDataSource;

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.dis_activity_actions, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.refresh_disruptions:
        new DisplayDisruptionsAsyncTask(linesDataSource, disruptedLinesView).execute();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dis);

    disruptedLinesView = (ListView) findViewById(R.id.lines);
    disruptedLinesView.setEmptyView(findViewById(R.id.empty_view));

    linesDataSource = new LinesDataSource(linesClient);

    new DisplayDisruptionsAsyncTask(linesDataSource, disruptedLinesView).execute();
  }

}
