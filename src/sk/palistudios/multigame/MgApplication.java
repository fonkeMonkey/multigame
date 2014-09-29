package sk.palistudios.multigame;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class MgApplication extends Application {
  private Tracker mTracker = null;

  @Override
  public void onCreate() {
    super.onCreate();
    Log.i("Multigame", "Multigame started.");
  }

  public synchronized Tracker getTracker() {
    if (mTracker == null) {
      GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
      mTracker = analytics.newTracker(R.xml.global_tracker);
    }
    return mTracker;
  }
}
