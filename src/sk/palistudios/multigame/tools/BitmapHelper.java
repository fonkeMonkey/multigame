package sk.palistudios.multigame.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by virdzek on 23/11/14.
 */
public class BitmapHelper {
  public static Bitmap decodeSampledBitmapFromResource(Context context, int resId) {
    return decodeSampledBitmapFromResource(context, resId, !MemoryUtil.isLowMemoryDevice(context));
  }

  public static Bitmap decodeSampledBitmapFromResource(Context context, int resId,
      boolean highQuality) {
    return decodeSampledBitmapFromResource(context.getResources(), resId,
        DisplayHelper.getScreenWidth(context), DisplayHelper.getScreenHeight(context), highQuality);
  }

  public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth,
      int reqHeight, boolean highQuality) {

    // First decode with inJustDecodeBounds=true to check dimensions
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeResource(res, resId, options);
    //    BitmapFactory.decodeFile(,options);

    // Calculate inSampleSize
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false;

    if (!highQuality) {
      options.inPreferredConfig = Bitmap.Config.RGB_565;
      options.inDither = true;
    }

    return BitmapFactory.decodeResource(res, resId, options);
  }

  private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
      int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

      final int halfHeight = height / 2;
      final int halfWidth = width / 2;

      // Calculate the largest inSampleSize value that is a power of 2 and keeps both
      // height and width larger than the requested height and width.
      while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
        inSampleSize *= 2;
      }
    }

    return inSampleSize;
  }
}
