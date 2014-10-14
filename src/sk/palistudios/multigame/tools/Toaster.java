package sk.palistudios.multigame.tools;

// @author Pali

import java.util.ArrayList;

import android.content.Context;
import android.widget.Toast;

public class Toaster {

  private static ArrayList<Toast> mToasts = new ArrayList<Toast>();

  public static Toast toastShort(String text, Context context) {
    int duration = Toast.LENGTH_SHORT;

    Toast toast = Toast.makeText(context, text, duration);
    toast.show();

    return toast;
  }

  public static Toast toastLong(String text, Context context) {
    int duration = Toast.LENGTH_LONG;

    Toast toast = Toast.makeText(context, text, duration);
    toast.show();

    return toast;
  }

}
