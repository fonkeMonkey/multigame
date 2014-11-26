package sk.palistudios.multigame;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by virdzek on 26/11/14.
 */
abstract public class MultigameDialog {
  protected Context mContext = null;
  protected Dialog mDialog = null;

  protected DialogInterface.OnClickListener defaultNegativeListener =
      new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          mDialog.dismiss();
        }
      };

  public MultigameDialog(Context context) {
    mContext = context;
  }

  public void show() {
    mDialog = createDialog(mContext);
    mDialog.show();
  }

  protected abstract Dialog createDialog(Context context);
}
