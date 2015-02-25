package io.pivotal.dis.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import io.pivotal.dis.R;
import io.pivotal.dis.lines.LinesDataSource;

public class DisplayDisruptionsAsyncTask extends AsyncTask<Void, Void, List<String>> {
  private final LinesDataSource linesDataSource;
  private final ListView viewToUpdate;
  private ProgressDialog progressDialog;
  private boolean requestSuccessful = true;

  public DisplayDisruptionsAsyncTask(LinesDataSource linesDataSource, ListView viewToUpdate, ProgressDialog progressDialog) {
    this.linesDataSource = linesDataSource;
    this.viewToUpdate = viewToUpdate;
    this.progressDialog = progressDialog;
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    progressDialog.show();
  }

  @Override
  protected List<String> doInBackground(Void... params) {
    try {
      return linesDataSource.getDisruptedLineNames();
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
  protected void onPostExecute(List<String> disruptedLines) {
    try {
      final ArrayAdapter<String> linesAdapter = new ArrayAdapter<String>(
          viewToUpdate.getContext(),
          android.R.layout.simple_list_item_1,
          disruptedLines.toArray(new String[]{})
      );
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
      progressDialog.hide();
    }
  }
}
