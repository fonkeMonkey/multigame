package sk.palistudios.multigame.tools;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Created by virdzek on 24/11/14.
 */
public class DisplayHelper {
  public static final int ALPHA_80pc = 204;
  public static final int ALPHA_20pc = 51;

  public static int getOrientation(Context context) {
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

  public static int getScreenWidth(Context context) {
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();

    if (Build.VERSION.SDK_INT >= 13) {
      Point size = new Point();
      display.getSize(size);
      return size.x;
    } else {
      return display.getWidth();  // deprecated
    }
  }

  public static int getScreenHeight(Context context) {
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();

    if (Build.VERSION.SDK_INT >= 13) {
      Point size = new Point();
      display.getSize(size);
      return size.y;
    } else {
      return display.getHeight();  // deprecated
    }
  }
}
