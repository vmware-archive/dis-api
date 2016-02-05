package io.pivotal.dis.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.inject.Inject;

import io.pivotal.dis.R;
import io.pivotal.dis.lines.LinesClient;
import io.pivotal.dis.task.DisplayDisruptionsAsyncTask;

public abstract class AbstractDisActivity extends GuiceActivity implements SwipeRefreshLayout.OnRefreshListener {

  @Inject
  private LinesClient linesClient;

  @Inject
  protected SharedPreferences sharedPreferences;

  private ListView disruptedLinesView;
  private SwipeRefreshLayout swipeLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.dis);

    disruptedLinesView = (ListView) findViewById(R.id.lines);
    disruptedLinesView.setEmptyView(findViewById(R.id.empty_view));

    new DisplayDisruptionsAsyncTask(disruptedLinesView, linesClient).execute();

    swipeLayout = ((SwipeRefreshLayout) findViewById(R.id.swipe_layout));
    swipeLayout.setOnRefreshListener(this);
  }

  @Override
  public void onRefresh() {
    new DisplayDisruptionsAsyncTask(disruptedLinesView, linesClient).execute();
    swipeLayout.setRefreshing(false);
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
      case R.id.test_mode:
        handleTestMode(item);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void handleTestMode(MenuItem item) {
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
  }

}
