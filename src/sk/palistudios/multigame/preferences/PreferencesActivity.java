package sk.palistudios.multigame.preferences;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import sk.palistudios.multigame.BaseActivity;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.mainMenu.DebugSettings;
import sk.palistudios.multigame.tools.DisplayHelper;
import sk.palistudios.multigame.tools.SkinManager;

/**
 * @author Pali
 */
public class PreferencesActivity extends BaseActivity {
  //TODO možno aby sa tie dialógy nedestroyovali onRotation
  private static SharedPreferences.Editor editor;
  private static SharedPreferences prefs;
  private PreferenceOnOffSwitcher mMusicSwitch;
  private PreferenceOnOffSwitcher mSoundSwitch;
  private PreferenceOnOffSwitcher mAutoCalibrationSwitch;
  private LinearLayout mGameModeLayout;
  private TextView mGameModeLabel;
  private CheckedTextView mGameModeClassic;
  private CheckedTextView mGameModeTutorial;
  private TextView mRateUs;
  private TextView mAbout;
  private TextView mHeader;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.preferences);

    mHeader = (TextView) findViewById(R.id.pref_header);

    mMusicSwitch = (PreferenceOnOffSwitcher) findViewById(R.id.pref_music);
    mMusicSwitch.setChecked(GameSharedPref.isMusicOn());
    mMusicSwitch.setOnCheckedChangeListener(new PreferenceOnOffSwitcher.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(PreferenceOnOffSwitcher buttonView, boolean isChecked) {
        GameSharedPref.setMusicOn(isChecked);
      }
    });

    mSoundSwitch = (PreferenceOnOffSwitcher) findViewById(R.id.pref_sound);
    mSoundSwitch.setChecked(GameSharedPref.isSoundOn());
    mSoundSwitch.setOnCheckedChangeListener(new PreferenceOnOffSwitcher.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(PreferenceOnOffSwitcher buttonView, boolean isChecked) {
        GameSharedPref.setSoundOn(isChecked);
      }
    });

    mAutoCalibrationSwitch = (PreferenceOnOffSwitcher) findViewById(R.id.pref_autocalibration);
    mAutoCalibrationSwitch.setChecked(GameSharedPref.isAutoCalibrationEnabled());
    mAutoCalibrationSwitch.setOnCheckedChangeListener(
        new PreferenceOnOffSwitcher.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(PreferenceOnOffSwitcher buttonView, boolean isChecked) {
            GameSharedPref.setAutocalibration(isChecked);
          }
        });

    mGameModeLayout = (LinearLayout) findViewById(R.id.pref_gamemode_layout);
    mGameModeLabel = (TextView) findViewById(R.id.pref_gamemode_label);
    mGameModeClassic = (CheckedTextView) findViewById(R.id.pref_classic_gamemode);
    mGameModeTutorial = (CheckedTextView) findViewById(R.id.pref_tutor_gamemode);

    boolean isTutorialCompleted =
        GameSharedPref.isTutorialCompleted() || DebugSettings.tutorialCompleted;
    if (isTutorialCompleted || DebugSettings.tutorialCompleted) {
      mGameModeLabel.setVisibility(View.VISIBLE);
      mGameModeLayout.setVisibility(View.VISIBLE);
    } else {
      mGameModeLabel.setVisibility(View.GONE);
      mGameModeLayout.setVisibility(View.GONE);
    }

    refreshGameModeStatus("Tutorial".equals(GameSharedPref.getGameMode()));
    mGameModeTutorial.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        GameSharedPref.setGameMode("Tutorial");
        refreshGameModeStatus(true);
      }
    });
    mGameModeClassic.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        GameSharedPref.setGameMode("Classic");
        refreshGameModeStatus(false);
      }
    });

    mRateUs = (TextView) findViewById(R.id.rate_us);
    mRateUs.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new RateDialog(PreferencesActivity.this).show();
      }
    });

    mAbout = (TextView) findViewById(R.id.about_us);
    mAbout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new AboutDialog(PreferencesActivity.this).show();
      }
    });
  }

  @Override
  protected void reskinLocally(SkinManager.Skin currentSkin) {
    mMusicSwitch.reskinDynamically();
    mSoundSwitch.reskinDynamically();
    mAutoCalibrationSwitch.reskinDynamically();
    mHeader.setTextColor(mHeader.getTextColors().withAlpha(DisplayHelper.ALPHA_80pc));
  }

  //TODO light a medium integruj (takisto dla obrázka aj podtitulok je light a čosi BLACK
  private void refreshGameModeStatus(boolean isTutorial) {
//    int version = Build.VERSION.SDK_INT;
    if (isTutorial) {
      mGameModeTutorial.setChecked(true);
      //      if (version >= Build.VERSION_CODES.LOLLIPOP) {
      //        mGameModeTutorial.setTypeface(Typeface.create("sans-serif-medium", Typeface
      // .NORMAL));
      //      } else {
      mGameModeTutorial.setTypeface(mGameModeTutorial.getTypeface(), Typeface.BOLD);
      //      }

      mGameModeClassic.setChecked(false);
      //      if (version >= Build.VERSION_CODES.JELLY_BEAN) {
      //        mGameModeClassic.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
      //      } else {
      mGameModeClassic.setTypeface(mGameModeClassic.getTypeface(), Typeface.NORMAL);
      //      }
    } else {
      mGameModeTutorial.setChecked(false);
      //      if (version >= Build.VERSION_CODES.JELLY_BEAN) {
      //        mGameModeTutorial.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
      //      } else {
      mGameModeTutorial.setTypeface(mGameModeTutorial.getTypeface(), Typeface.NORMAL);
      //      }

      mGameModeClassic.setChecked(true);
      //      if (version >= Build.VERSION_CODES.LOLLIPOP) {
      //        mGameModeClassic.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
      //      } else {
      mGameModeClassic.setTypeface(mGameModeClassic.getTypeface(), Typeface.BOLD);
      //      }
    }
    mGameModeClassic.invalidate();
    mGameModeTutorial.invalidate();
  }
}
