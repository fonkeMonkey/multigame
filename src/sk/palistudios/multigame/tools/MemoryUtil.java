package sk.palistudios.multigame.tools;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

/**
 * Created by virdzek on 20/01/15.
 */
public class MemoryUtil {
  public static boolean isLowMemoryDevice(Context context) {
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    int memoryClass = am.getMemoryClass();
    Log.d("MemoryUtil", "memoryClass:" + Integer.toString(memoryClass));
    return (memoryClass <= 32) ? true : false;
  }
}
