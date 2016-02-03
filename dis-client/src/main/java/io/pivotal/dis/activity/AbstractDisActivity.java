package io.pivotal.dis.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.inject.Inject;

import io.pivotal.dis.R;
import io.pivotal.dis.lines.LinesClient;
import io.pivotal.dis.lines.LinesDataSource;
import io.pivotal.dis.task.DisplayDisruptionsAsyncTask;

public abstract class AbstractDisActivity extends GuiceActivity {

  @Inject
  private LinesClient linesClient;

  @Inject
  protected SharedPreferences sharedPreferences;

  private ListView disruptedLinesView;
  private LinesDataSource linesDataSource;
  private View progressBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.dis);

    progressBar = findViewById(R.id.progress_bar);
    disruptedLinesView = (ListView) findViewById(R.id.lines);
    disruptedLinesView.setEmptyView(findViewById(R.id.empty_view));
    linesDataSource = new LinesDataSource(linesClient);

    new DisplayDisruptionsAsyncTask(linesDataSource, disruptedLinesView, progressBar).execute();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.dis_activity_actions, menu);

    showTestMode(menu);

    return true;
  }

  protected abstract void showTestMode(Menu menu);

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {

      case R.id.refresh_disruptions:
        new DisplayDisruptionsAsyncTask(linesDataSource, disruptedLinesView, progressBar).execute();
        return true;

      case R.id.test_mode:
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (item.isChecked()) {
          editor.putBoolean("testMode", false);
          editor.apply();
          item.setChecked(false);
        }
        else {
          editor.putBoolean("testMode", true);
          editor.apply();
          item.setChecked(true);
        }
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

}
