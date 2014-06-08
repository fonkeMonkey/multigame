package sk.palistudios.multigame.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import sk.palistudios.multigame.tools.premiumUpgrade.PremiumUpgrader;

public class UpgradeToPremiumDialog extends DialogPreference {

    private static Activity mActivity = null;

    public UpgradeToPremiumDialog(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogTitle("Upgrade to Premium");

        setDialogMessage("Would you like to upgrade Multigame to Premium?");

        setPositiveButtonText("Ok");
        setNegativeButtonText("Cancel");
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);

        final String appName = "sk.palistudios.multigame";

        if (which == -1) {
            PremiumUpgrader.upgrade(PreferencesActivity.getInstance());
        }
    }
}
