package sk.palistudios.multigame.preferences;

import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v7.appcompat.BuildConfig;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import sk.palistudios.multigame.MultigameDialog;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.persistence.MGSettings;
import sk.palistudios.multigame.tools.Toaster;

public class AboutDialog extends MultigameDialog {
  private int mCheater;
  private boolean mPaliStudiosClicked = false;

  public AboutDialog(Context context) {
    super(context);
  }

  @Override
  protected Dialog createDialog(Context context) {
    Dialog dialog = new Dialog(context);
    dialog.setTitle(mContext.getString(R.string.pref_about));
    dialog.setContentView(R.layout.about);

    String versionName = "1.0";
    try {
      versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(),
          0).versionName;
    } catch (NameNotFoundException ex) {
      Logger.getLogger(AboutDialog.class.getName()).log(Level.SEVERE, null, ex);
    }
    TextView aboutTextView = (TextView) dialog.findViewById(R.id.about_text);
    aboutTextView.setText("Multigame v. " + versionName + "\n©Pali Studios\n");

    aboutTextView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mPaliStudiosClicked && BuildConfig.DEBUG) {
          mPaliStudiosClicked = false;
        } else {
          mPaliStudiosClicked = true;
        }
      }
    });

    ImageView aboutLogo = (ImageView) dialog.findViewById(R.id.about_logo);
    aboutLogo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mPaliStudiosClicked) {
          mCheater++;
          if (mCheater == 7) {
            Toaster.toastLong("Stuff is unlocked.", mContext);
            MGSettings.unlockItemsAll();
          }
        } else {
          mPaliStudiosClicked = false;
        }
      }
    });
    return dialog;
  }
}