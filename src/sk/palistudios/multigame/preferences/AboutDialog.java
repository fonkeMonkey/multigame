package sk.palistudios.multigame.preferences;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import sk.palistudios.multigame.R;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AboutDialog extends DialogPreference {

    private static Context mContext = null;

    public AboutDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        setDialogTitle(mContext.getString(R.string.pref_about));

        setDialogLayoutResource(R.layout.about);

        setNegativeButtonText(null);
    }

    @Override
    protected void onBindDialogView(View view) {

        String versionName = "1.0";
        try {
            versionName = mContext.getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (NameNotFoundException ex) {
            Logger.getLogger(AboutDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

        TextView aboutTextView = (TextView) view.findViewById(R.id.about_text);
        aboutTextView.setText("Multigame v. " + versionName + "\n©Pali Studios\n");

        super.onBindDialogView(view);
    }
}