package sk.palistudios.multigame.game;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import sk.palistudios.multigame.BaseActivity;
import sk.palistudios.multigame.MgTracker;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.achievements.AchievementsCenterListActivity;
import sk.palistudios.multigame.customization_center.skins.SkinsCenterListActivity;
import sk.palistudios.multigame.game.minigames.IMiniGameHorizontal;
import sk.palistudios.multigame.game.minigames.IMiniGameVertical;
import sk.palistudios.multigame.game.minigames.MinigamesManager;
import sk.palistudios.multigame.game.persistence.GameSaverLoader;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.game.time.GameTimeManager;
import sk.palistudios.multigame.game.view.BaseGameCanvasView;
import sk.palistudios.multigame.hall_of_fame.HofDatabaseCenter;
import sk.palistudios.multigame.mainMenu.DebugSettings;
import sk.palistudios.multigame.tools.Toaster;
import sk.palistudios.multigame.tools.sound.MusicPlayer;
import sk.palistudios.multigame.tools.sound.SoundEffectsCenter;

/**
 * @author Pali
 */
public class GameActivity extends BaseActivity implements SensorEventListener {

  public static int dialogScore = -1;
  public static int dialogType = -1;
  public static int sTutorialLastLevel = 0;
  public static int sGamesPerSession = 0;
  public static boolean isDialogPresent = false;
  public static boolean sTutorialRestart = false;
  public static boolean dialogIsWinner = false;

  private static boolean sIncreaseVolumeShown = false;
  private static boolean sRaisedVolumeForTutorialAlready = false;

  private Handler mTutorialHandler = new Handler();
  private Handler mGameLoopHandler = new Handler();
  private Handler mTimeHandler = new Handler();
  private Runnable mRunnableGameLoop = new Runnable() {
    int updates_per_refresh = 0;
    long nextUpdateGame = -1;

    public void run() {
      if (!MinigamesManager.isAllMinigamesInitialized()) {
        mGameLoopHandler.postDelayed(this, 25);
        return;
      }
      if (nextUpdateGame == -1) {
        nextUpdateGame = System.currentTimeMillis();
      }
      updates_per_refresh = 0;
      while (System.currentTimeMillis() > nextUpdateGame && updates_per_refresh < MAX_FRAMESKIP) {
        updateGame();
        mScore += mLevel * DebugSettings.SCORE_COEFFICIENT;
        nextUpdateGame += UPDATE_INTERVAL_IN_MILLIS;
        updates_per_refresh++;
      }
      refreshDisplayGame();

      if (mGameLoopHandler != null) {
      //TODO optimalizuj aby sa toto nevolalo furt dokola (kára batterku a môže spôsobavať na
      // shit devicoch, že ostatné loopy sa nebudú tak často dostavať k slovu,
      // so what ostatné stačí za sekund a tutorial ani nehovorím
        //anyway je to useless prekreslovať neupdatované polia, tu by malo byť nejaký presný čas.
        mGameLoopHandler.post(this);
      }
    }
  };;
  private Runnable mRunnableTutorial = new Runnable() {
    public void run() {
      mMusicPlayer.pauseMusic();

      if (sTutorialLastLevel != 3) {
        GameDialogs.showNextTutorialWindow(GameActivity.this, true);
      } else {
        GameDialogs.showTutorialWinnerWindow(GameActivity.this);
        sTutorialLastLevel = 0;
      }
    }
  };
  private Runnable mRunnableTime = new Runnable() {
    int milisecondsPassed = 0;
    int scoreUpdatesPerSeconds = 1;
    int refreshInterval = 1000 / scoreUpdatesPerSeconds;

    public void run() {

      if (mTimeHandler != null) {
        mTimeHandler.postDelayed(this, refreshInterval);
      }
      milisecondsPassed += refreshInterval;

      //one second passed
      if (milisecondsPassed % 1000 == 0) {
        GameTimeManager.onSecondPassed();
      }

      //increase level
      if (milisecondsPassed % (DebugSettings.SECONDS_PER_LEVEL * 1000) == 0 && !isTutorial()) {
        mLevel++;
        GameTimeManager.onLevelIncreased(GameActivity.this);
        redrawDifficultyView(String.valueOf(mLevel));
      }

    }
  };

  final private int GAME_UPDATES_PER_SECOND = 60;//Cell phones have seldom more fps per seconds,
  // although some have 120 now
  final private int UPDATE_INTERVAL_IN_MILLIS = 1000 / GAME_UPDATES_PER_SECOND;
  final private int MAX_FRAMESKIP = 4;
  public boolean gameStopped = true;
  private Toast mToast;
  private MusicPlayer mMusicPlayer;
  private SensorManager sm = null;
  private Sensor sensor = null;
  private TextView mScoreView = null;
  private TextView mDifficultyView = null;
  private BaseGameCanvasView mCanvases[];
  private boolean mTutorialMode;
  private boolean wasGameSaved;
  private boolean wasActivityPaused = false;
  private boolean wasGameLost = false;
  private int mOrientation;
  private boolean isDefaultCoordinatesSet = false;
  private float DEFAULT_AXIS_X = 0f;
  private float DEFAULT_AXIS_Y = 0f;
  private LinearLayout gameBar;
  private View gameScoreSeparatorDown;
  private View gameScoreSeparator;
  private boolean mStartedMusicForTutorial = false;
  private int frames = 0;
  private IMiniGameVertical minigametoSendEvents1;
  private IMiniGameHorizontal minigametoSendEvents2;
  private int mScore = 0;
  private int mLevel = 1;
  private boolean closedByButton = false;
  private long mTimeGameStarted;
  private boolean mLoseTracked = false;
  private int mBarLabelColor;
  private int mBarTextColor;
  private SpannableString mDifficultySpannable;
  private SpannableString mScoreSpannable;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    setVolumeControlStream(AudioManager.STREAM_MUSIC);
    HofDatabaseCenter.initDB(this);

    initVariables();
    setContentView(R.layout.game_layout);

    mTutorialMode = GameSharedPref.isTutorialModeActivated();
    initGraphics();
    initMinigames();
  }

  private void initVariables() {
    wasGameSaved = GameSharedPref.isGameSaved();

    MinigamesManager.loadMinigames(this);

    int musicID = getResources().getIdentifier(GameSharedPref.getMusicLoopChosen(), "raw",
        getPackageName());

    mMusicPlayer = new MusicPlayer(musicID, getApplicationContext());

    //I need to have direct pointer because of speed
    minigametoSendEvents1 = (IMiniGameVertical) MinigamesManager.getMinigames()[0];
    minigametoSendEvents2 = (IMiniGameHorizontal) MinigamesManager.getMinigames()[1];

    resolveOrientation();

  }

  public void initMinigames() {
    for (int i = 0; i < 4; i++) {
      MinigamesManager.activateMinigame(this, i);
      GameTimeManager.registerLevelChangedObserver(MinigamesManager.getMinigames()[i]);
      mCanvases[i].attachMinigame(MinigamesManager.getMinigames()[i], i);
    }

    for (int i = 0; i < 4; i++) {
      mCanvases[i].invalidate();
    }
  }

  private void initGraphics() {
    gameBar = (LinearLayout) findViewById(R.id.game_bar);
    gameBar.setBackgroundColor(SkinsCenterListActivity.getCurrentSkin(getApplicationContext()).getBarBgColor());
    mScoreView = (TextView) findViewById(R.id.game_score);
    mScoreView.setTextColor(SkinsCenterListActivity.getCurrentSkin(getApplicationContext()).getBarLabelColor());
    mDifficultyView = (TextView) findViewById(R.id.game_level);
    mDifficultyView.setTextColor(SkinsCenterListActivity.getCurrentSkin(getApplicationContext()).getBarLabelColor());
    gameScoreSeparator = (View) findViewById(R.id.game_score_separator);
    gameScoreSeparator.setBackgroundColor(SkinsCenterListActivity.getCurrentSkin(getApplicationContext())
        .getBarSeparatorColor());
    gameScoreSeparatorDown = (View) findViewById(R.id.game_score_separator_down);
    gameScoreSeparatorDown.setBackgroundColor(SkinsCenterListActivity.getCurrentSkin(getApplicationContext())
        .getBarSeparatorColorDown());

    mCanvases = new BaseGameCanvasView[4];
    mCanvases[0] = (BaseGameCanvasView) findViewById(R.id.canvas1);
    mCanvases[1] = (BaseGameCanvasView) findViewById(R.id.canvas2);
    mCanvases[2] = (BaseGameCanvasView) findViewById(R.id.canvas3);
    mCanvases[3] = (BaseGameCanvasView) findViewById(R.id.canvas4);

    mCanvases[0].setGameSaved(wasGameSaved);
    mCanvases[1].setGameSaved(wasGameSaved);
    mCanvases[2].setGameSaved(wasGameSaved);
    mCanvases[3].setGameSaved(wasGameSaved);

    mBarLabelColor = SkinsCenterListActivity.getCurrentSkin(getApplicationContext()).getBarLabelColor();
    mBarTextColor = SkinsCenterListActivity.getCurrentSkin(getApplicationContext()).getBarTextColor();
    mScoreSpannable = new SpannableString(getString(R.string.score));
    mDifficultySpannable = new SpannableString("Level: ");
    mLeftSpanFgColor = new ForegroundColorSpan(mBarLabelColor);
    mRightSpanFgColor = new ForegroundColorSpan(mBarTextColor);

    if (mTutorialMode) {
      redrawScoreView("Tutorial");
      redrawDifficultyView("Tutorial");
    } else {
      redrawScoreView(String.valueOf(0));
      redrawDifficultyView(String.valueOf(1));
    }
  }

  @Override
  public void onResume() {
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    super.onResume();

    if (isDialogPresent == true) {
      //MainMenu will handle it
      isDialogPresent = false;
      finish();

    } else {
      //register listener for accelerometer
      sm = (SensorManager) getSystemService(SENSOR_SERVICE);
      sensor = sm.getDefaultSensor(
          Sensor.TYPE_ACCELEROMETER);//SensorList(Sensor.TYPE_GYROSCOPE).get(0);
      sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

      wasGameSaved = GameSharedPref.isGameSaved();

      if (mTutorialMode) {
        if (sTutorialLastLevel == 0) {
          if (SoundEffectsCenter.getCurrentVolume(getApplicationContext()) == 0 && !sRaisedVolumeForTutorialAlready) {
            SoundEffectsCenter.raiseCurrentVolume(getApplicationContext());
          }
          if (!sTutorialRestart) {
            GameDialogs.showWelcomeTutorialWindow(this);
          } else {
            sTutorialRestart = false;
            GameDialogs.showNextTutorialWindow(this, false);
          }
        } else {
          if (!sTutorialRestart) {
            GameDialogs.showNextTutorialWindow(this, true);
          } else {
            sTutorialRestart = false;
            GameDialogs.showNextTutorialWindow(this, false);
          }
        }

      } else {
        if (wasGameSaved) {
          mToast = Toaster.toastLong(getResources().getString(R.string.game_touch_resume),
              getApplicationContext());
          wasActivityPaused = false;
          redrawScoreView(String.valueOf(mScore));
          redrawDifficultyView(String.valueOf(mLevel));
          GameSharedPref.setGameSaved(false);
        } else {
          boolean playingFirstTime = GameSharedPref.isPlayingGameFirstTime();
          MinigamesManager.setAllMinigamesDifficultyForTutorial();
          if (playingFirstTime) {
            Toaster.toastLong(getResources().getString(R.string.game_touch_save), getApplicationContext());
            mToast = Toaster.toastLong( getResources().getString(R.string.game_touch_start),
                getApplicationContext());
            if (SoundEffectsCenter.getCurrentVolume(getApplicationContext()) == 0 &&
                !sRaisedVolumeForTutorialAlready) {
              SoundEffectsCenter.raiseCurrentVolume(getApplicationContext());
            }
            GameSharedPref.setPlayingGameFirstTimeFalse();
          } else {
            mToast = Toaster.toastLong( getResources().getString(R.string.game_touch_start),
                getApplicationContext());
            if (SoundEffectsCenter.getCurrentVolume(getApplicationContext()) == 0 && !sIncreaseVolumeShown) {
              Toaster.toastLong(getString(R.string.increase_music_volume), getApplicationContext());
              sIncreaseVolumeShown = true;
            }
          }
        }
      }
    }
  }

  public void startGame() {
    MgTracker.trackGameStarted();
    mTimeGameStarted = System.currentTimeMillis();
    sGamesPerSession++;
    if (mRunnableGameLoop != null) {
      if (GameSharedPref.isMusicOn()) {
        if (!wasActivityPaused) {
          mMusicPlayer.startMusic();
        } else {
          mMusicPlayer.resumeMusic();
        }
      }
      mRunnableGameLoop.run();
      gameStopped = false;
    }
    if (mRunnableTime != null) {
      mRunnableTime.run();
    }

  }

  public void startGameTutorial() {
    stopTutorialGameLoop();

    MinigamesManager.deactivateAllMiniGames(this);
    for (int i = 0; i <= sTutorialLastLevel; i++) {
      MinigamesManager.activateMinigame(this, i);
    }
    if (mMusicPlayer != null && GameSharedPref.isMusicOn()) {
      if (!mStartedMusicForTutorial) {
        mMusicPlayer.startMusic();
        mStartedMusicForTutorial = true;
      } else {
        mMusicPlayer.resumeMusic();
      }
    }

    MinigamesManager.setAllMinigamesDifficultyForTutorial();
    gameStopped = false;
    startTutorialGameLoop();

    mTutorialHandler.postDelayed(mRunnableTutorial, DebugSettings.SECONDS_PER_LEVEL_TUTORIAL *
        1000);
  }

  public void startTutorialGameLoop() {
    if (mRunnableGameLoop != null) {
      mRunnableGameLoop.run();
    }
    if (mRunnableTime != null) {
      mRunnableTime.run();
    }
  }

  public void stopTutorialGameLoop() {
    if (mGameLoopHandler != null) {
      mGameLoopHandler.removeCallbacks(null);
    }
    if (mTimeHandler != null) {
      mTimeHandler.removeCallbacks(null);
    }
  }

  private void updateGame() {
    for (int i = 0; i < 4; i++) {
      if (MinigamesManager.getmMinigamesActivityFlags()[i] == true) {
        MinigamesManager.getMinigames()[i].updateMinigame();
      }
    }

  }

  public void flashScreen() {
    Animation animation = new AlphaAnimation(0, 1); // Change alpha
    animation.setDuration(500); // duration - half a second
    animation.setInterpolator(new LinearInterpolator());
    animation.setRepeatCount(0);
    LinearLayout gameLayout = (LinearLayout) findViewById(R.id.game_container);
    gameLayout.startAnimation(animation);
  }

  private void refreshDisplayGame() {
    if (!mTutorialMode) {
     redrawScoreView(String.valueOf(mScore));
    }

    for (int i = 0; i < 4; i++) {
      if (MinigamesManager.getmMinigamesActivityFlags()[i] == true) {
        mCanvases[i].invalidate();
      }
    }

  }

  ForegroundColorSpan mLeftSpanFgColor;
  ForegroundColorSpan mRightSpanFgColor;
  private void redrawScoreView(String score) {
    mScoreSpannable.setSpan(mLeftSpanFgColor, 0, mScoreSpannable.length(),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    mScoreView.setText(mScoreSpannable);

    Spannable secondPart = new SpannableString(score);
    secondPart.setSpan(mRightSpanFgColor, 0, score.length(),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    mScoreView.append(secondPart);
  }

  private void redrawDifficultyView(String difficulty) {
    mDifficultySpannable.setSpan(mLeftSpanFgColor, 0,
        mDifficultySpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    mDifficultyView.setText(mDifficultySpannable);

    Spannable secondPart = new SpannableString(difficulty);
    secondPart.setSpan(mRightSpanFgColor, 0, difficulty.length(),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    mDifficultyView.append(secondPart);
  }

  @Override
  public void onPause() {
    super.onPause();
    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    if (mTutorialMode) {
      GameDialogs.sLostGame = true;
      stopTutorial();
    } else {
      if (mGameLoopHandler != null && mRunnableGameLoop != null) {
        mGameLoopHandler.removeCallbacks(mRunnableGameLoop);
      }
      if (mTimeHandler != null) {
        mTimeHandler.removeCallbacks(mRunnableTime);
      }
    }

    if (!gameStopped && !closedByButton) {
      if (!mTutorialMode) {
        saveGame();
      }
    }

    closedByButton = false;
    sm.unregisterListener(this);
    mMusicPlayer.pauseMusic();
    wasActivityPaused = true;
  }

  public void onSensorChanged(SensorEvent event) {

    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

      if (!gameStopped) {
        if (!isDefaultCoordinatesSet && GameSharedPref.getAutoCalibrationEnabled()) {
                    /* Na stojaka je to 10, opacny stojak - 10, rovina nula,
                    ten gece nevie ale na ktoru stranu je otoceny,
                    ide to z oboch stran od 10 do -10. akuratze ked sa tocis okolo 10 on to
                    odcitava, takze ti to akoby pretecie.
                    preto ja nastavim os ak je moc velka na 8.5 tam zvycajne sa uz tolko netocis
                    aby ti to pretekalo cez 10
                     */
          DEFAULT_AXIS_X = normaliseAxis(event.values[1]);
          DEFAULT_AXIS_Y = normaliseAxis(event.values[0]);
          isDefaultCoordinatesSet = true;
        }

        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {

          if (MinigamesManager.isMiniGameActive(0)) {
            minigametoSendEvents1.onUserInteracted(event.values[1] - DEFAULT_AXIS_X);
          }

          if (MinigamesManager.isMiniGameActive(1)) {
            minigametoSendEvents2.onUserInteracted(-event.values[0] - DEFAULT_AXIS_Y);
          }

        } else {
          if (MinigamesManager.isMiniGameActive(0)) {
            minigametoSendEvents1.onUserInteracted(event.values[0] - DEFAULT_AXIS_Y);
          }

          if (MinigamesManager.isMiniGameActive(1)) {
            minigametoSendEvents2.onUserInteracted(event.values[1] - DEFAULT_AXIS_X);
          }

        }
      }
    }
  }

  private float normaliseAxis(float value) {
    if (value > 8.5) {
      return 8.5f;
    }
    if (value < -8.5) {
      return -8.5f;
    }
    return value;
  }

  @Override
  public boolean onTouchEvent(MotionEvent evt) {
    if (!gameStopped) {
      return true;
    }
    if (evt.getAction() == MotionEvent.ACTION_DOWN) {
      if (mToast != null) {
        mToast.cancel();
        mToast = null;
      }
      startGame();
    }
    return true;
  }

  public void onAccuracyChanged(Sensor arg0, int arg1) {
  }

  /**
   * hoňím si
   */
  public void onGameLost(int loser) {
    wasGameLost = true;
    GameSharedPref.setGameSaved(false);
    if (!isTutorial() && !mLoseTracked) {
      MgTracker.trackGameFinished((System.currentTimeMillis() - mTimeGameStarted) / 1000, mLevel,
          mScore, MinigamesManager.getMinigames()[loser].getName());
      Log.d("Minigame lost:", MinigamesManager.getMinigames()[loser].getName());
      mLoseTracked = true;
    }

    if (gameStopped != true) {
      gameStopped = true;

      if (mTutorialMode) {
        stopTutorial();

        colorFragmentGray(loser);

        GameDialogs.showTutorialLoserDialogWindow(this);
        return;
      }

      stopCurrentGame();
      colorFragmentGray(loser);

      GameSharedPref.StatsGamesPlayedIncrease();

      AchievementsCenterListActivity.checkAchievements(mScore, mLevel, getApplicationContext());

      HofDatabaseCenter.getsHofDb().open();
      boolean isInHallOfFame = HofDatabaseCenter.getsHofDb().isInHallOfFame(mScore);
      HofDatabaseCenter.getsHofDb().close();

      if (isInHallOfFame) {
        GameDialogs.showWinnerDialogWindow(this);
      } else {
        GameDialogs.showLoserDialogWindow(this);
      }

    }

  }

  private void colorFragmentGray(int loser) {

    switch (loser) {

      case 0:
        mCanvases[0].setBackgroundGray();
        break;
      case 1:
        mCanvases[1].setBackgroundGray();
        break;
      case 2:
        mCanvases[2].setBackgroundGray();
        break;
      case 3:
        mCanvases[3].setBackgroundGray();
        break;
    }

  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();

    if (mTutorialMode) {
      GameDialogs.sLostGame = true;
      stopTutorial();
    } else {
      saveGame();
    }
    closedByButton = true;
  }

  @Override
  public void onUserLeaveHint() {
    super.onUserLeaveHint();

    if (mTutorialMode) {
      stopTutorial();
    } else {
      saveGame();
    }
    closedByButton = true;
  }

  @Override
  public void onStop() {
    super.onStop();
    if (mMusicPlayer != null) {
      mMusicPlayer.stopMusic();
    }
  }



  public void stopCurrentGame() {
    sm.unregisterListener(this);

    if (mGameLoopHandler != null && mRunnableGameLoop != null) {
      mGameLoopHandler.removeCallbacks(mRunnableGameLoop);
      mRunnableGameLoop = null;
      mGameLoopHandler = null;
    }
    if (mTimeHandler != null) {
      mTimeHandler.removeCallbacks(mRunnableTime);
    }
    if (mMusicPlayer != null) {
      mMusicPlayer.pauseMusic();
    }
  }

  public void stopTutorial() {
    sm.unregisterListener(this);
    mMusicPlayer.stopMusic();

    if (mGameLoopHandler != null) {
      mGameLoopHandler.removeCallbacks(mRunnableGameLoop);
      mGameLoopHandler = null;
    }

    if (mRunnableGameLoop != null) {
      mRunnableGameLoop = null;
    }

    if (mTimeHandler != null) {
      mTimeHandler.removeCallbacks(mRunnableTime);
      mTimeHandler = null;
    }

    if (mTutorialHandler != null) {
      mTutorialHandler.removeCallbacks(mRunnableTutorial);
      mTutorialHandler = null;
    }

    if (mRunnableTutorial != null) {
      mRunnableTutorial = null;
    }

  }

  public BaseGameCanvasView[] getCanvases() {
    return mCanvases;
  }

  public int getScore() {
    return mScore;
  }

  public int getLevel() {
    return mLevel;
  }

  public void setGameDetails(int newScore, int newFrames, int newLevel) {
    mScore = newScore;
    frames = newFrames;
    mLevel = newLevel;
  }

  public int getFrames() {
    return frames;
  }

  public boolean isGameStopped() {
    return gameStopped;
  }

  private void saveGame() throws NotFoundException {
    if (!wasGameLost) {
      GameSaverLoader.saveGame(this);
      Toaster.toastShort(getResources().getString(R.string.game_game_saved), getApplicationContext());
      stopCurrentGame();
      GameSharedPref.setGameSaved(true);
      finish();
    } else {
      GameSharedPref.setGameSaved(false);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
  }

  private void resolveOrientation() {
    WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    Configuration config = getResources().getConfiguration();

    if (Build.VERSION.SDK_INT < 8) {
      mOrientation = config.orientation;
    } else {
      int rotation = windowManager.getDefaultDisplay().getRotation();

      if (((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) &&
          config.orientation == Configuration.ORIENTATION_LANDSCAPE) ||
          ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) &&
              config.orientation == Configuration.ORIENTATION_PORTRAIT)) {
        mOrientation = Configuration.ORIENTATION_LANDSCAPE;
      } else {
        mOrientation = Configuration.ORIENTATION_PORTRAIT;
      }
    }
  }

  public boolean isTutorial() {
    return mTutorialMode;
  }

  public void stopMusic() {
    if (mMusicPlayer != null) {
      mMusicPlayer.stopMusic();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mTutorialMode) {
      GameDialogs.sLostGame = true;
    }
    mMusicPlayer = null;
    for(BaseGameCanvasView canvas : mCanvases){
      canvas.detachMinigame();
    }
    mCanvases = null;
    MinigamesManager.detachGameRefFromMinigames();

    destroyThreadsSafely();
  }

  private void destroyThreadsSafely() {
    if(mGameLoopHandler != null && mRunnableGameLoop != null){
      mGameLoopHandler.removeCallbacks(mRunnableGameLoop);
    }

    if(mTimeHandler != null && mRunnableTime != null){
      mTimeHandler.removeCallbacks(mRunnableTime);
    }

    if(mTutorialHandler != null && mRunnableTutorial != null){
      mTutorialHandler.removeCallbacks(mRunnableTutorial);
    }

    mTutorialHandler =null;
    mGameLoopHandler=null;
    mTimeHandler=null;
  }
}
