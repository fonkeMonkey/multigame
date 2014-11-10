package sk.palistudios.multigame.mainMenu;

// @author Pali

import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.Session;
import sk.palistudios.multigame.BaseActivity;
import sk.palistudios.multigame.MgTracker;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.skins.SkinsCenterListActivity;
import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.GameDialogs;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.tools.Toaster;
import sk.palistudios.multigame.tools.sound.SoundEffectsCenter;

public class MainMenuActivity extends BaseActivity {

  public static boolean isThereADialogToShow = false;
  private static boolean sShowHighScoreStatus;
  private static int sShowHighScoreScore;
  private static MainMenuActivity sMainMenuInstance;
  private static boolean sFacebookShared = false;
  private Button buttonStart;
  private Button buttonCc;
  private Button buttonHof;
  private Button buttonPreferences;
  private ImageView logo;
  private int mClicksOnLogo = 0;

  public static MainMenuActivity getInstance() {
    return sMainMenuInstance;
  }

  public static void setOfferHighScore(int score) {
    sShowHighScoreStatus = true;
    sShowHighScoreScore = score;
  }

//  public static void setMainMenuFacebook(MainMenuActivity mainMenu) {
//    mainMenuFacebook = mainMenu;
//  }

  public static void setWallPostAchievementDone() {
    sFacebookShared = true;
  }

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    setContentView(R.layout.main_menu);
    setVolumeControlStream(AudioManager.STREAM_MUSIC);

    sMainMenuInstance = this;

    GameSharedPref.initSharedPref(this);
    ApplicationInitializer.initApplication(this);

    logo = (ImageView) findViewById(R.id.logo);

    buttonStart = (Button) findViewById(R.id.mainMenu_button_start);
    buttonCc = (Button) findViewById(R.id.mainMenu_button_MGC);
    buttonHof = (Button) findViewById(R.id.mainMenu_button_HOF);
    buttonPreferences = (Button) findViewById(R.id.mainMenu_button_preferences);

    findViewById(R.id.logo).setOnTouchListener(new View.OnTouchListener() {
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
  protected void onRestart() {
    super.onRestart();
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

    logo.setImageResource(SkinsCenterListActivity.getCurrentSkin(this).getLogoID());
    setMainMenuColors();

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

  private void setMainMenuColors() {
    /* Zasa hacky lebo sa mi to nechce redezignovat. */
    if (GameSharedPref.isSkinChosen("kuba")) {
      buttonStart.getBackground().setColorFilter(getResources().getColor(R.color.kubaMenu1),
          Mode.SRC);
      buttonCc.getBackground().setColorFilter(getResources().getColor(R.color.kubaMenu2), Mode.SRC);
      buttonPreferences.getBackground().setColorFilter(getResources().getColor(R.color.kubaMenu3),
          Mode.SRC);
      buttonHof.getBackground().setColorFilter(getResources().getColor(R.color.kubaMenu4),
          Mode.SRC);
    } else if (GameSharedPref.isSkinChosen("summer")) {
      buttonStart.getBackground().setColorFilter(getResources().getColor(R.color.summerMenu1),
          Mode.SRC);
      buttonCc.getBackground().setColorFilter(getResources().getColor(R.color.summerMenu2),
          Mode.SRC);
      buttonPreferences.getBackground().setColorFilter(getResources().getColor(R.color.summerMenu3),
          Mode.SRC);
      buttonHof.getBackground().setColorFilter(getResources().getColor(R.color.summerMenu4),
          Mode.SRC);
    } else if (GameSharedPref.isSkinChosen("girl_power")) {
      buttonStart.getBackground().setColorFilter(getResources().getColor(R.color.pinkyMenu1),
          Mode.SRC);
      buttonCc.getBackground().setColorFilter(getResources().getColor(R.color.pinkyMenu2),
          Mode.SRC);
      buttonPreferences.getBackground().setColorFilter(getResources().getColor(R.color.pinkyMenu3),
          Mode.SRC);
      buttonHof.getBackground().setColorFilter(getResources().getColor(R.color.pinkyMenu4),
          Mode.SRC);
    } else if (GameSharedPref.isSkinChosen("blue_sky")) {
      buttonStart.getBackground().setColorFilter(getResources().getColor(R.color.blueSkyMenu1),
          Mode.SRC);
      buttonCc.getBackground().setColorFilter(getResources().getColor(R.color.blueSkyMenu2),
          Mode.SRC);
      buttonPreferences.getBackground().setColorFilter(getResources().getColor(
          R.color.blueSkyMenu3), Mode.SRC);
      buttonHof.getBackground().setColorFilter(getResources().getColor(R.color.blueSkyMenu4),
          Mode.SRC);
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
  }

  public void startGame(View view) {
    SoundEffectsCenter.playForwardSound(MainMenuActivity.this);
    Intent intent = new Intent(this, sk.palistudios.multigame.game.GameActivity.class);
    startActivity(intent);
  }

  public void showMGC(View view) {
    SoundEffectsCenter.playForwardSound(MainMenuActivity.this);
    Intent intent = new Intent(this,
        sk.palistudios.multigame.customization_center.CustomizationCenterActivity.class);
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
      buttonStart.setText(getString(R.string.button_resume));
      return;
    }

    if (GameSharedPref.isTutorialModeActivated()) {
      if (GameActivity.sTutorialLastLevel == 0) {
        buttonStart.setText(getString(R.string.button_tutorial));

      } else {
        buttonStart.setText(getString(R.string.button_resume_tutorial));
      }
      return;
    }

    buttonStart.setText(getString(R.string.button_game));

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
