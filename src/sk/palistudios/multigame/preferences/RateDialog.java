package sk.palistudios.multigame.preferences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import sk.palistudios.multigame.MultigameDialog;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.tools.Toaster;
import sk.palistudios.multigame.tools.internet.InternetChecker;

public class RateDialog extends MultigameDialog {
  private DialogInterface.OnClickListener mPositiveListener =
      new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          final String appName = "sk.palistudios.multigame";

          if (which == -1) {
            try {
              //                mContext.startActivity(new Intent(Intent.ACTION_VIEW,
              // Uri.parse("market://details?id=" + appName)));
              if (InternetChecker.isNetworkAvailable(mContext)) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + appName));
                mContext.startActivity(intent);
                GameSharedPref.achievementFulfilled("supporter", "dst_blam");
              } else {
                Toaster.toastLong(mContext.getString(R.string.internet_no_connection), mContext);
              }
            } catch (android.content.ActivityNotFoundException anfe) {
              mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                  "http://play.google.com/store/apps/details?id=" + appName)));
            }
          }
        }
      };

  public RateDialog(Context context) {
    super(context);
  }

  @Override
  protected AlertDialog createDialog(Context context) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(mContext.getString(R.string.rate));
    builder.setMessage(mContext.getString(R.string.rate_msg));
    builder.setPositiveButton("Ok", mPositiveListener);
    builder.setNegativeButton(mContext.getString(R.string.cancel), defaultNegativeListener);
    return builder.create();
  }
}
