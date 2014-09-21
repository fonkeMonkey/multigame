package sk.palistudios.multigame;

import android.app.Application;
import android.util.Log;

public class MgApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Log.i("Multigame", "Multigame started.");
  }
}
