package sk.palistudios.multigame;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class MgApplication extends Application {
  public static Context sContext = null;

  @Override
  public void onCreate() {
    super.onCreate();
    sContext = this;
    ApplicationInitializer.initApplication(this);
    Log.i("Multigame", "Multigame started.");
  }

  public static Context getContext() {
    return sContext;
  }
}
