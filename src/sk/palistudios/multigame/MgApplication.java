package sk.palistudios.multigame;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class MgApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Log.i("Multigame", "Multigame started.");
  }
}
