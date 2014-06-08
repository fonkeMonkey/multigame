package sk.palistudios.multigame.preferences;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.tools.InternetChecker;
import sk.palistudios.multigame.tools.Toaster;

public class RateDialog extends DialogPreference {

    private static Context mContext = null;

    public RateDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        setDialogTitle(mContext.getString(R.string.rate));

        setDialogMessage(mContext.getString(R.string.rate_msg));

        setPositiveButtonText("Ok");
        setNegativeButtonText(mContext.getString(R.string.cancel));
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);

        final String appName = "sk.palistudios.multigame";

        if (which == -1) {
            try {
//                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                if (InternetChecker.isNetworkAvailable(mContext)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + appName));
                    getContext().startActivity(intent);
                    GameSharedPref.achievementFulfilled("supporter", "dst_blam");
                } else {
                    Toaster.toastLong(getContext().getString(R.string.internet_no_connection), mContext);
                }
            } catch (android.content.ActivityNotFoundException anfe) {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
            }
        }
    }
}
