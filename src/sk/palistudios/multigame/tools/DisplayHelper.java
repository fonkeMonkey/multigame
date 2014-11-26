package sk.palistudios.multigame.tools;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.view.Surface;
import android.view.WindowManager;

import sk.palistudios.multigame.game.GameActivity;

/**
 * Created by virdzek on 24/11/14.
 */
public class DisplayHelper {
  public static int getOrientation(Context context) {
    WindowManager windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
    Configuration config = context.getResources().getConfiguration();
    return config.orientation;
  }

  public static int getOrientationForAccelerometer(Context context) {
    WindowManager windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
    Configuration config = context.getResources().getConfiguration();

    if (Build.VERSION.SDK_INT < 8) {
      return config.orientation;
    } else {
      int rotation = windowManager.getDefaultDisplay().getRotation();

      if (((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) &&
          config.orientation == Configuration.ORIENTATION_LANDSCAPE) ||
          ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) &&
              config.orientation == Configuration.ORIENTATION_PORTRAIT)) {
        return Configuration.ORIENTATION_LANDSCAPE;
      } else {
        return Configuration.ORIENTATION_PORTRAIT;
      }
    }
  }
}
