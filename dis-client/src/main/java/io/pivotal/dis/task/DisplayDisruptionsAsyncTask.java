package io.pivotal.dis.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.pivotal.dis.R;
import io.pivotal.dis.lines.Line;
import io.pivotal.dis.lines.LinesDataSource;

public class DisplayDisruptionsAsyncTask extends AsyncTask<Void, Void, List<Map<String, String>>> {
  private final LinesDataSource linesDataSource;
  private final ListView viewToUpdate;
  private View progressBar;
  private boolean requestSuccessful = true;

  public DisplayDisruptionsAsyncTask(LinesDataSource linesDataSource, ListView viewToUpdate, View progressBar) {
    this.linesDataSource = linesDataSource;
    this.viewToUpdate = viewToUpdate;
    this.progressBar = progressBar;
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override
  protected List<Map<String, String>> doInBackground(Void... params) {
    try {
      List<Line> disruptedLines = linesDataSource.getDisruptedLines();
      List<Map<String, String>> disruptedLinesForDisplay = new ArrayList<>();
      for (Line line : disruptedLines) {
        Map<String, String> map = new HashMap<>();
        map.put("name", line.getName());
        map.put("status", line.getStatus());
        disruptedLinesForDisplay.add(map);
      }
      return disruptedLinesForDisplay;
    } catch (SocketTimeoutException e) {
      requestSuccessful = false;
      return Collections.emptyList();
    } catch (UnknownHostException e) {
      requestSuccessful = false;
      return Collections.emptyList();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void onPostExecute(List<Map<String, String>> disruptedLines) {
    try {
      final SimpleAdapter linesAdapter = new SimpleAdapter(viewToUpdate.getContext(),
          disruptedLines,
          R.layout.line_view,
          new String[]{"name", "status"},
          new int[]{R.id.line_name, R.id.line_status});
      if (requestSuccessful) {
        viewToUpdate.setAdapter(linesAdapter);
        Activity activity = (Activity) viewToUpdate.getContext();
        TextView emptyListView = (TextView) activity.findViewById(R.id.empty_view);
        emptyListView.setText(activity.getString(R.string.no_disruptions));
      } else {
        Activity activity = (Activity) viewToUpdate.getContext();
        TextView emptyListView = (TextView) activity.findViewById(R.id.empty_view);
        emptyListView.setText(activity.getString(R.string.refresh_failed));
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    finally {
      progressBar.setVisibility(View.GONE);
    }
  }
}
