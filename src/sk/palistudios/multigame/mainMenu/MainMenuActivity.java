package sk.palistudios.multigame.mainMenu;

// @author Pali

import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Session;
import sk.palistudios.multigame.BaseActivity;
import sk.palistudios.multigame.MgTracker;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.CustomizationCenterActivity;
import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.GameDialogs;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.tools.DisplayHelper;
import sk.palistudios.multigame.tools.SkinManager;
import sk.palistudios.multigame.tools.Toaster;
import sk.palistudios.multigame.tools.sound.SoundEffectsCenter;

public class MainMenuActivity extends BaseActivity {

  public static boolean isThereADialogToShow = false;
  private static boolean sShowHighScoreStatus;
  private static int sShowHighScoreScore;
  private static MainMenuActivity sMainMenuInstance;
  private static boolean sFacebookShared = false;
  private LinearLayout mViewContainer;
  private TextView mTVStart;
  private ImageView mlogoView;
  private int mClicksOnLogo = 0;

  public static MainMenuActivity getInstance() {
    return sMainMenuInstance;
  }

  public static void setOfferHighScore(int score) {
    sShowHighScoreStatus = true;
    sShowHighScoreScore = score;
  }

  public static void setWallPostAchievementDone() {
    sFacebookShared = true;
  }

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    setContentView(R.layout.main_menu);
    setVolumeControlStream(AudioManager.STREAM_MUSIC);

    sMainMenuInstance = this;

    mlogoView = (ImageView) findViewById(R.id.logo);
    mTVStart = (TextView) findViewById(R.id.menu_start);
    mViewContainer = (LinearLayout) findViewById(R.id.background);

    mlogoView.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        mClicksOnLogo++;
        if (mClicksOnLogo == 7) {
          Toaster.toastShort("Tutorial completed, you cheater ;)",
              MainMenuActivity.this.getApplicationContext());
          GameSharedPref.onTutorialCompleted();
          setStartGameButtonName();
        }
        return false;
      }
    });

  }

  @Override
  public void reskinLocally(SkinManager.Skin currentSkin) {
    changeLogo(currentSkin);
  }

  private void changeLogo(SkinManager.Skin currentSkin) {
    if (DisplayHelper.getOrientation(this) == Configuration.ORIENTATION_LANDSCAPE) {
      switch (currentSkin) {
        case QUAD:
          mlogoView.setImageDrawable(getResources().getDrawable(R.drawable.logo1_lndscp));
          break;
        case THRESHOLD:
          mlogoView.setImageDrawable(getResources().getDrawable(R.drawable.logo2_lndscp));
          break;
        case DIFFUSE:
          mlogoView.setImageDrawable(getResources().getDrawable(R.drawable.logo1_lndscp));
          break;
        case CORRUPTED:
          mlogoView.setImageDrawable(getResources().getDrawable(R.drawable.logo3_lndscp));
          break;
      }
    } else {
      switch (currentSkin) {
        case QUAD:
          mlogoView.setImageDrawable(getResources().getDrawable(R.drawable.logo1_prt));
          break;
        case THRESHOLD:
          mlogoView.setImageDrawable(getResources().getDrawable(R.drawable.logo2_prt));
          break;
        case DIFFUSE:
          mlogoView.setImageDrawable(getResources().getDrawable(R.drawable.logo1_prt));
          break;
        case CORRUPTED:
          mlogoView.setImageDrawable(getResources().getDrawable(R.drawable.logo3_prt));
          break;
      }
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    /* Track tutorial exited. */
    if (GameDialogs.sTutorialRestartWindowsShownPerSession != 0) {
      MgTracker.trackTutorialRestartWindowsShownPerSession(
          GameDialogs.sTutorialRestartWindowsShownPerSession, false);
      GameDialogs.sTutorialRestartWindowsShownPerSession = 0;
    }

    setStartGameButtonName();

    if (sShowHighScoreStatus) {
      GameDialogs.showWinnerDialogAfterShareWindow(this, sShowHighScoreScore);
      sShowHighScoreStatus = false;

    }
    if (isThereADialogToShow) {
      //only works now for the ask to connect dialog
      GameDialogs.askUserToConnect(this, GameActivity.dialogIsWinner, GameActivity.dialogScore);
      isThereADialogToShow = false;
    }

    if (sFacebookShared) {
      GameSharedPref.achievementFulfilled("competitive", "blue_sky");
      Toaster.toastLong(getResources().getString(R.string.game_achievement_fulfilled_1) +
          "Competitive" + getResources().getString(R.string.game_achievement_fulfilled_2) + "skin" +
          this.getResources().getString(R.string.game_achievement_fulfilled_3), this);
    }
    sShowHighScoreStatus = false;
    sFacebookShared = false;

    GameActivity.sTutorialRestart = false;
  }

  public void startGame(View view) {
    SoundEffectsCenter.playForwardSound(MainMenuActivity.this);
    Intent intent = new Intent(this, sk.palistudios.multigame.game.GameActivity.class);
    startActivity(intent);
  }

  public void showMGC(View view) {
    SoundEffectsCenter.playForwardSound(MainMenuActivity.this);
    Intent intent = new Intent(this,
        CustomizationCenterActivity.class);
    startActivity(intent);
  }

  public void showHallOfFame(View view) {
    SoundEffectsCenter.playForwardSound(MainMenuActivity.this);
    Intent intent = new Intent(this,
        sk.palistudios.multigame.hall_of_fame.HallOfFameActivity.class);
    startActivity(intent);
  }

  public void showPreferences(View view) {
    SoundEffectsCenter.playForwardSound(MainMenuActivity.this);
    Intent intent = new Intent(this,
        sk.palistudios.multigame.preferences.PreferencesActivity.class);
    startActivity(intent);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }

  private void setStartGameButtonName() {
    if (GameSharedPref.isGameSaved()) {
      mTVStart.setText(getString(R.string.button_resume));
      return;
    }

    if (GameSharedPref.isTutorialModeActivated()) {
      if (GameActivity.sTutorialLastLevel == 0) {
        mTVStart.setText(getString(R.string.button_tutorial));

      } else {
        mTVStart.setText(getString(R.string.button_resume_tutorial));
      }
      return;
    }

    mTVStart.setText(getString(R.string.button_game));

  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
      if (GameActivity.sGamesPerSession != 0) {
        MgTracker.trackGamesPerSession(GameActivity.sGamesPerSession);
        GameActivity.sGamesPerSession = 0;
      }
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if (GameActivity.sGamesPerSession != 0) {
      MgTracker.trackGamesPerSession(GameActivity.sGamesPerSession);
      GameActivity.sGamesPerSession = 0;
    }

    /* Track tutorial exited. */
    if (GameDialogs.sTutorialRestartWindowsShownPerSession != 0) {
      MgTracker.trackTutorialRestartWindowsShownPerSession(
          GameDialogs.sTutorialRestartWindowsShownPerSession, false);
      GameDialogs.sTutorialRestartWindowsShownPerSession = 0;
    }

    SoundEffectsCenter.releaseMediaPlayer();
    sMainMenuInstance = null;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    super.onActivityResult(requestCode, resultCode, data);
    Session.getActiveSession().onActivityResult(getInstance(), requestCode, resultCode, data);
    sMainMenuInstance = null;
  }
}
