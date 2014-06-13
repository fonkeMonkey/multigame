package sk.palistudios.multigame.tools;

// @author Pali

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

public class Toaster {

    private static ArrayList<Toast> mToasts = new ArrayList<Toast>();

    public static void toastShort(String text, Activity activity) {
        Context context = activity;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

//        mToasts.add(toast);

    }

    public static Toast toastLong(String text, Activity activity) {
        Context context = activity;
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        return toast;
//        mToasts.add(toast);
    }

    //    public static void cancelAllToasts(){
//        for (Toast toast : mToasts) {
//            toast.cancel();
//        }
//        mToasts.clear();
//    }
    public static void toastLong(String text, Context mContext) {
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(mContext, text, duration);
        toast.show();
    }
}
