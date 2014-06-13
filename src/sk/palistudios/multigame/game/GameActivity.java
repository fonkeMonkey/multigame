package sk.palistudios.multigame.game;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
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
import com.google.analytics.tracking.android.EasyTracker;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.achievements.AchievementsCenterListActivity;
import sk.palistudios.multigame.customization_center.skins.SkinsCenterListActivity;
import sk.palistudios.multigame.game.minigames.IMiniGameHorizontal;
import sk.palistudios.multigame.game.minigames.IMiniGameVertical;
import sk.palistudios.multigame.game.persistence.GameSaverLoader;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.game.view.*;
import sk.palistudios.multigame.hall_of_fame.HofDatabaseCenter;
import sk.palistudios.multigame.mainMenu.DebugSettings;
import sk.palistudios.multigame.tools.SoundEffectsCenter;
import sk.palistudios.multigame.tools.Toaster;

/**
 * @author Pali
 */
public class GameActivity extends FragmentActivity implements SensorEventListener {

    public static boolean isDialogPresent = false;
    public static int dialogScore = -1;
    public static int dialogType = -1;
    public static boolean dialogIsWinner = false;
    public static int sTutorialLastLevel = 0;
    public static boolean sTutorialRestart = false;
    final private int GAME_UPDATES_PER_SECOND = 40;
    final private int GAME_SKIP_FRAMES = 1000 / GAME_UPDATES_PER_SECOND;
    final private int MAX_FRAMESKIP = 8;
    public boolean gameStopped = true;
    public Runnable mRunnableGameLoop;
    Handler displayHandler;
    Toast mToast;
    GameMusicPlayer mMusicPlayer;
    Handler mHandlerTutorial;
    Runnable mRunnableTutorial;
    Handler mGameLoopHandler;
    private SensorManager sm = null;
    private Sensor sensor = null;
    private TextView scoreView = null;
    private TextView difficultyView = null;
    private AFragmentView mFragmentViews[];
    private AFragment mFragments[];
    private boolean mTutorialMode;
    private boolean wasGameSaved;
    private boolean wasActivityPaused = false;
    private boolean wasGameLost = false;
    private int mOrientation;
    private boolean isDefaultCoordinatesSet = false;
    private float DEFAULT_AXIS_X = 0f;
    private float DEFAULT_AXIS_Y = 0f;
    private LinearLayout gameBar;
    //Gaining top on activity stack
    private View gameScoreSeparatorDown;
    private Runnable mRunnableTime;
    private Handler mTimeHandler;
    private View gameScoreSeparator;
    private boolean mStartedMusicForTutorial = false;
    private int frames = 0;
    private IMiniGameVertical minigametoSendEvents1;
    private IMiniGameHorizontal minigametoSendEvents2;
    private int score = 0;
    private int level = 1;
    private boolean closedByButton = false;

    public static void flashScreen(GameActivity game) {
        Animation animation = new AlphaAnimation(0, 1); // Change alpha
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(0);
//        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at
        LinearLayout gameLayout = (LinearLayout) game.findViewById(R.id.game);
        gameLayout.startAnimation(animation);
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initVariables();
        HofDatabaseCenter.initDB(this);

        setContentView(R.layout.game);

        initGraphics();

        initMinigames(this);

    }

    private void initVariables() {
        wasGameSaved = GameSharedPref.isGameSaved();

        GameSharedPref.setMinigamesInitialized(false);
        GameMinigamesManager.LoadMinigamesObjects(this);

        int musicID = getResources().getIdentifier(GameSharedPref.getMusicLoopChosen(), "raw", this.getPackageName());

        mMusicPlayer = new GameMusicPlayer(musicID, this);

        //I need to have direct pointer because of speed
        minigametoSendEvents1 = (IMiniGameVertical) GameMinigamesManager.getMinigamesObjects()[0];
        minigametoSendEvents2 = (IMiniGameHorizontal) GameMinigamesManager.getMinigamesObjects()[1];

        resolveOrientation();


    }

    public void initMinigames(GameActivity game) {
        for (int i = 0; i < 4; i++) {
            GameMinigamesManager.activateMinigame(game, i);
            GameTimeMaster.registerLevelChangedObserver(GameMinigamesManager.getMinigamesObjects()[i]);

        }

//        GameMinigamesManager.deactivateMinigame(game, 0);
//        GameMinigamesManager.deactivateMinigame(game, 1);
//        GameMinigamesManager.deactivateMinigame(game, 2);
//        GameMinigamesManager.deactivateMinigame(game, 3);


        for (int i = 0; i < 4; i++) {
            game.mFragmentViews[i].invalidate();
        }
    }

    private void initGraphics() {
        gameBar = (LinearLayout) findViewById(R.id.game_bar);
        gameBar.setBackgroundColor(SkinsCenterListActivity.getCurrentSkin(this).getBarBgColor());
        scoreView = (TextView) findViewById(R.id.game_score);
        scoreView.setTextColor(SkinsCenterListActivity.getCurrentSkin(this).getBarLabelColor());
        difficultyView = (TextView) findViewById(R.id.game_level);
        difficultyView.setTextColor(SkinsCenterListActivity.getCurrentSkin(this).getBarLabelColor());
        gameScoreSeparator = (View) findViewById(R.id.game_score_separator);
        gameScoreSeparator.setBackgroundColor(SkinsCenterListActivity.getCurrentSkin(this).getBarSeparatorColor());
        gameScoreSeparatorDown = (View) findViewById(R.id.game_score_separator_down);
        gameScoreSeparatorDown.setBackgroundColor(SkinsCenterListActivity.getCurrentSkin(this).getBarSeparatorColorDown());

        mFragments = new AFragment[4];
        mFragments[0] = (Fragment1) getSupportFragmentManager().findFragmentById(R.id.fragment1);
        mFragments[1] = (Fragment2) getSupportFragmentManager().findFragmentById(R.id.fragment2);
        mFragments[2] = (Fragment3) getSupportFragmentManager().findFragmentById(R.id.fragment3);
        mFragments[3] = (Fragment4) getSupportFragmentManager().findFragmentById(R.id.fragment4);

        mFragmentViews = new AFragmentView[4];
        mFragmentViews[0] = mFragments[0].getmView();
        mFragmentViews[1] = mFragments[1].getmView();
        mFragmentViews[2] = mFragments[2].getmView();
        mFragmentViews[3] = mFragments[3].getmView();

        mFragmentViews[0].setGameSaved(wasGameSaved);
        mFragmentViews[1].setGameSaved(wasGameSaved);
        mFragmentViews[2].setGameSaved(wasGameSaved);
        mFragmentViews[3].setGameSaved(wasGameSaved);


    }

    @Override
    public void onResume() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onResume();
        SoundEffectsCenter.muteSystemSounds(this, true);
        mTutorialMode = GameSharedPref.isTutorialModeActivated();

        if (isDialogPresent == true) {
            //MainMenu will handle it
            isDialogPresent = false;
            finish();

        } else {
//
            //register listener for accelerometer
            sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//SensorList(Sensor.TYPE_GYROSCOPE).get(0);
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

            if (mGameLoopHandler == null) {
                mGameLoopHandler = new Handler();
            }

            if (mTimeHandler == null) {
                mTimeHandler = new Handler();
            }

            if (mRunnableGameLoop == null) {

                mRunnableGameLoop = new Runnable() {
                    int loops = 0;
                    long nextUpdateGame = -1;

                    public void run() {
                        if (!GameMinigamesManager.areAllMinigamesInitialized()) {
                            mGameLoopHandler.postDelayed(this, 25);
                            return;
                        }
                        if (mGameLoopHandler != null) {
                            if (nextUpdateGame == -1) {
                                nextUpdateGame = System.currentTimeMillis();
                            }
                            loops = 0;
                            while (System.currentTimeMillis() > nextUpdateGame && loops < MAX_FRAMESKIP) {
                                updateGame();
                                nextUpdateGame += GAME_SKIP_FRAMES;
                                loops++;
                            }

                            refreshDisplayGame();
                            if (mGameLoopHandler != null) {
                                mGameLoopHandler.post(this);
                            }
                        }
                    }
                };
            }

            if (mRunnableTime == null) {
                mRunnableTime = new Runnable() {
                    private int secondsPassed = 0;

                    public void run() {
                        secondsPassed++;
                        //one second passed
                        GameTimeMaster.onSecondPassed();

                        //increase level
                        if (secondsPassed % (DebugSettings.SECONDS_PER_LEVEL) == 0) {
                            level++;
                            GameTimeMaster.onLevelIncreased(GameActivity.this);
                        }

                        if (mTimeHandler != null) {
                            mTimeHandler.postDelayed(this, 1000);
                        }

                    }
                };
            }
            wasGameSaved = GameSharedPref.isGameSaved();


            if (mTutorialMode) {
                if (sTutorialLastLevel == 0) {
                    if (!sTutorialRestart) {
                        GameUIWindows.showTutorialWindow(this);
                    } else {
                        sTutorialRestart = false;
                        GameUIWindows.showTutorialMinigameDemoWindow(this, false);
                    }
                } else {
                    if (!sTutorialRestart) {
                        GameUIWindows.showTutorialMinigameDemoWindow(this, true);
                    } else {
                        sTutorialRestart = false;
                        GameUIWindows.showTutorialMinigameDemoWindow(this, false);
                    }
                }

            } else {
//            TimeMaster.registerTimeObserver(this);
                if (wasGameSaved) {
                    mToast = Toaster.toastLong((String) getResources().getString(R.string.game_touch_resume), this);
                    wasActivityPaused = false;
                } else {
                    boolean playingFirstTime = GameSharedPref.isPlayingFirstTime();
//                    GameMinigamesManager.setAllMinigamesDifficultyForClassicGame();
                    GameMinigamesManager.setAllMinigamesDifficultyForTutorial();
                    if (playingFirstTime) {
                        Toaster.toastShort((String) getResources().getString(R.string.game_touch_save), this);
                        mToast = Toaster.toastLong((String) getResources().getString(R.string.game_touch_start), this);
                        GameSharedPref.setPlayingFirstTimeFalse();
                    } else {
                        mToast = Toaster.toastLong((String) getResources().getString(R.string.game_touch_start), this);
                    }
                }

//            }
            }
        }
    }

    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);

    }
    //Loosing top on activity stack
//    private boolean onPauseRunAlready = false;

    public void startGame() {
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

        final GameActivity game = this;
        GameMinigamesManager.deactivateAllMiniGames(this);
        for (int i = 0; i <= sTutorialLastLevel; i++) {
            GameMinigamesManager.activateMinigame(game, i);
        }
//        GameTimeMaster.startTimers(getSeconds(), this);

        if (mMusicPlayer != null && GameSharedPref.isMusicOn()) {
            if (!mStartedMusicForTutorial) {
                mMusicPlayer.startMusic();
                mStartedMusicForTutorial = true;
            } else {
                mMusicPlayer.resumeMusic();
            }
        }

        GameMinigamesManager.setAllMinigamesDifficultyForTutorial();
        gameStopped = false;
        startTutorialGameLoop();
        if (mRunnableTutorial == null) {

            mRunnableTutorial = new Runnable() {
                public void run() {
                    mMusicPlayer.pauseMusic();
                    if (mGameLoopHandler != null) {
                        mGameLoopHandler.removeCallbacks(null);
                    }
                    if (mTimeHandler != null) {
                        mTimeHandler.removeCallbacks(mRunnableTime);
                    }

                    if (sTutorialLastLevel != 3) {

//                    GameTimeMaster.stopTimers();
                        GameUIWindows.showTutorialMinigameDemoWindow(game, true);

                    } else {
//                    GameTimeMaster.stopTimers();
                        GameUIWindows.showTutorialEndWindow(game);
                        sTutorialLastLevel = 0;
                    }
                }
            };
        }
        if (mHandlerTutorial == null) {
            mHandlerTutorial = new Handler();
        }

        mHandlerTutorial.postDelayed(mRunnableTutorial, DebugSettings.SECONDS_PER_LEVEL_TUTORIAL
                * 1000);
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
            if (GameMinigamesManager.currentlyActiveMinigames[i] == true) {
                GameMinigamesManager.getMinigamesObjects()[i].updateMinigame();
            }
        }

    }

    private void refreshDisplayGame() {
        score += level * DebugSettings.SCORE_COEFICIENT;
//        frames++;

        if (mTutorialMode) {
            scoreView.setText(getString(R.string.score) + "Tutorial");
            difficultyView.setText("Level: Tutorial");
        } else {
            scoreView.setText(getString(R.string.score) + String.valueOf(score));
            difficultyView.setText("Level: " + String.valueOf(level));
        }


        for (int i = 0; i < 4; i++) {
            if (GameMinigamesManager.currentlyActiveMinigames[i] == true) {
//                GameMinigamesManager.getMinigamesObjects()[i].updateMinigame();
                mFragmentViews[i].invalidate();
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (mTutorialMode) {
            GameUIWindows.sLostGame = true;
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
//            onPauseRunAlready = true;
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
                    DEFAULT_AXIS_X = event.values[1];
                    DEFAULT_AXIS_Y = event.values[0];
                    isDefaultCoordinatesSet = true;
                }

                if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {

                    if (GameMinigamesManager.isMiniGameActive(0)) {
                        minigametoSendEvents1.onUserInteracted(event.values[1] - DEFAULT_AXIS_X);
                    }

                    if (GameMinigamesManager.isMiniGameActive(1)) {
                        minigametoSendEvents2.onUserInteracted(-event.values[0] - DEFAULT_AXIS_Y);
                    }

                } else {
                    if (GameMinigamesManager.isMiniGameActive(0)) {
                        minigametoSendEvents1.onUserInteracted(event.values[0] - DEFAULT_AXIS_Y);
                    }

                    if (GameMinigamesManager.isMiniGameActive(1)) {
                        minigametoSendEvents2.onUserInteracted(event.values[1] - DEFAULT_AXIS_X);
                    }

                }
            }
        }
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

        if (gameStopped != true) {
            gameStopped = true;

            if (mTutorialMode) {
                stopTutorial();

                colorFragmentGray(loser);

                GameUIWindows.showTutorialLoserDialogWindow(this);
                return;
            }

            stopCurrentGame();
            colorFragmentGray(loser);

            GameSharedPref.StatsGamesPlayedIncrease();

            AchievementsCenterListActivity.checkAchievements(score, level, this);

            HofDatabaseCenter.getHofDb().open();

            boolean isInHallOfFame = HofDatabaseCenter.getHofDb().isInHallOfFame(score);
            HofDatabaseCenter.getHofDb().close();

            if (isInHallOfFame) {
                GameUIWindows.showWinnerDialogWindow(this);
            } else {
                GameUIWindows.showLoserDialogWindow(this);
            }

        }


    }

    private void colorFragmentGray(int loser) {

        switch (loser) {

            case 0:
                mFragmentViews[0].setBackgroundGray();
                break;
            case 1:
                mFragmentViews[1].setBackgroundGray();
                break;
            case 2:
                mFragmentViews[2].setBackgroundGray();
                break;
            case 3:
                mFragmentViews[3].setBackgroundGray();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        if(DebugSettings.tutorialCompleted){
//            mTutorialMode = false;
//        }else{
//            mTutorialMode = GameSharedPref.isTutorialModeActivated();
//        }

        if (mTutorialMode) {
//            Toaster.toastLong(getString(R.string.exit_tutorial), this);
            GameUIWindows.sLostGame = true;
            stopTutorial();
        } else {
            saveGame();
//            GameSharedPref.setGameSaved(true);
//            wasGameSaved = true;
        }
        closedByButton = true;
        finish();
    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
//            closedByButton = true;
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();

//        mTutorialMode = GameSharedPref.isTutorialModeActivated();

        if (mTutorialMode) {
//            Toaster.toastLong(getString(R.string.exit_tutorial), this);
            stopTutorial();
        } else {
            saveGame();
//            GameSharedPref.setGameSaved(true);
//            wasGameSaved = true;
        }
        closedByButton = true;
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        SoundEffectsCenter.muteSystemSounds(this, false);
        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
        if (mMusicPlayer != null) {
            mMusicPlayer.stopMusic();

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTutorialMode) {
            GameUIWindows.sLostGame = true;
        }
//        sTutorialRestart = false;
        mMusicPlayer = null;
//        musicPlayer.stopMusic();
//        musicPlayer = null;
//        isDialogPresent = false;

    }

    public void stopCurrentGame() {
        sm.unregisterListener(this);
//        GameTimeMaster.stopTimers();

        if (mGameLoopHandler != null && mRunnableGameLoop != null) {
            mGameLoopHandler.removeCallbacks(mRunnableGameLoop);
            mRunnableGameLoop = null;
            mGameLoopHandler = null;
        }
        if (mTimeHandler != null) {
            mTimeHandler.removeCallbacks(mRunnableTime);
        }
//        GameMinigamesManager.stopAllMiniGames(this);
        GameSharedPref.setMinigamesInitialized(false);
        if (mMusicPlayer != null) {
            mMusicPlayer.pauseMusic();
        }

        //        GameMinigamesManager.setMinigamesInitialized(false, this);
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

//        GameTimeMaster.stopTimers();
        if (mHandlerTutorial != null) {
            mHandlerTutorial.removeCallbacks(mRunnableTutorial);
            mHandlerTutorial = null;

        }

        if (mRunnableTutorial != null) {
            mRunnableTutorial = null;
        }

        GameSharedPref.setMinigamesInitialized(false);
//        musicPlayer.pauseMusic();

    }

    public AFragmentView[] getmFragmentViews() {
        return mFragmentViews;
    }

    public AFragment[] getmFragments() {
        return mFragments;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public void setGameDetails(int newScore, int newFrames, int newLevel) {
        score = newScore;
        frames = newFrames;
        level = newLevel;
    }

    //    public void setMainMenuActivity(Activity mainMenu) {
//        mainMenuActivity = mainMenu;
//    }
    public int getFrames() {
        return frames;
    }

    public boolean isGameStopped() {
        return gameStopped;
    }

    private void saveGame() throws NotFoundException {
        if (!wasGameLost) {
            GameSaverLoader.saveGame(this);
            Toaster.toastShort((String) getResources().getString(R.string.game_game_saved), this);
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
        Session.getActiveSession()
                .onActivityResult(this, requestCode, resultCode, data);
    }

    private void resolveOrientation() {
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Configuration config = getResources().getConfiguration();
        int rotation = windowManager.getDefaultDisplay().getRotation();

        if (((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
                && config.orientation == Configuration.ORIENTATION_LANDSCAPE)
                || ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)
                && config.orientation == Configuration.ORIENTATION_PORTRAIT)) {
            mOrientation = Configuration.ORIENTATION_LANDSCAPE;
        } else {
            mOrientation = Configuration.ORIENTATION_PORTRAIT;
        }
    }

    public boolean isTutorial() {
        return mTutorialMode;
    }
}
