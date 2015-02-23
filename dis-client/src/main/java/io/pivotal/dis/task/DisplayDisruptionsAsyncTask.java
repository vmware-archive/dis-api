package io.pivotal.dis.task;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import io.pivotal.dis.lines.LinesDataSource;

public class DisplayDisruptionsAsyncTask extends AsyncTask<Void, Void, List<String>> {
  private final LinesDataSource linesDataSource;
  private final ListView viewToUpdate;

  public DisplayDisruptionsAsyncTask(LinesDataSource linesDataSource, ListView viewToUpdate) {
    this.linesDataSource = linesDataSource;
    this.viewToUpdate = viewToUpdate;
  }

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
    try {
        final ArrayAdapter<String> linesAdapter = new ArrayAdapter<String>(
            viewToUpdate.getContext(),
            android.R.layout.simple_list_item_1,
            disruptedLines.toArray(new String[]{})
        );

        viewToUpdate.setAdapter(linesAdapter);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
