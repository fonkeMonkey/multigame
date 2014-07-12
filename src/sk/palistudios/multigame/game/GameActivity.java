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
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
    private static boolean sIncreaseVolumeShown = false;
    private static boolean sRaisedVolumeForTutorialAlready = false;
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

        if (mTutorialMode) {
            setBarTextColors(scoreView, getString(R.string.score), "Tutorial");
            setBarTextColors(difficultyView, "Level: ", "Tutorial");
//            scoreView.setText(Html.fromHtml(getString(R.string.score)) + "Tutorial");
//            difficultyView.setText("Level: Tutorial");
        } else {
            setBarTextColors(scoreView, getString(R.string.score), String.valueOf(0));
            setBarTextColors(difficultyView, "Level: ", String.valueOf(1));
//            scoreView.setText(getString(R.string.score) + String.valueOf(score));
//            difficultyView.setText("Level: " + String.valueOf(level));
        }


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
                    if(SoundEffectsCenter.getCurrentVolume(this) == 0 && !sRaisedVolumeForTutorialAlready){
                        SoundEffectsCenter.raiseCurrentVolume(this);
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
//            TimeMaster.registerTimeObserver(this);
                if (wasGameSaved) {
                    mToast = Toaster.toastLong((String) getResources().getString(R.string.game_touch_resume), this);
                    wasActivityPaused = false;
                } else {
                    boolean playingFirstTime = GameSharedPref.isPlayingGameFirstTime();
//                    GameMinigamesManager.setAllMinigamesDifficultyForClassicGame();
                    GameMinigamesManager.setAllMinigamesDifficultyForTutorial();
                    if (playingFirstTime) {
                        Toaster.toastLong((String) getResources().getString(R.string.game_touch_save), this);
                        mToast = Toaster.toastLong((String) getResources().getString(R.string.game_touch_start), this);
                        if(SoundEffectsCenter.getCurrentVolume(this) == 0){
                            SoundEffectsCenter.raiseCurrentVolume(this);
                        }
                        GameSharedPref.setPlayingGameFirstTimeFalse();
                    } else {
                        mToast = Toaster.toastLong((String) getResources().getString(R.string.game_touch_start), this);
                        if(SoundEffectsCenter.getCurrentVolume(this) == 0 && !sIncreaseVolumeShown){
                            Toaster.toastLong(getString(R.string.increase_music_volume), this);
                            sIncreaseVolumeShown = true;
                        }
                    }
                }

//            }
            }
        }
    }

    public void onStart() {
        super.onStart();

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
                        GameDialogs.showNextTutorialWindow(game, true);

                    } else {
//                    GameTimeMaster.stopTimers();
                        GameDialogs.showTutorialWinnerWindow(game);
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

        if (mTutorialMode) {
            setBarTextColors(scoreView, getString(R.string.score), "Tutorial");
            setBarTextColors(difficultyView, "Level: ", "Tutorial");
//            scoreView.setText(Html.fromHtml(getString(R.string.score)) + "Tutorial");
//            difficultyView.setText("Level: Tutorial");
        } else {
            setBarTextColors(scoreView, getString(R.string.score), String.valueOf(score));
            setBarTextColors(difficultyView, "Level: ", String.valueOf(level));
//            scoreView.setText(getString(R.string.score) + String.valueOf(score));
//            difficultyView.setText("Level: " + String.valueOf(level));
        }


        for (int i = 0; i < 4; i++) {
            if (GameMinigamesManager.currentlyActiveMinigames[i] == true) {
//                GameMinigamesManager.getMinigamesObjects()[i].updateMinigame();
                mFragmentViews[i].invalidate();
            }
        }

    }

    private void setBarTextColors(TextView textView, String firstWord, String secondWord) {
        Spannable firstPart = new SpannableString(firstWord);
        int firstColor = SkinsCenterListActivity.getCurrentSkin(this).getBarLabelColor();
        firstPart.setSpan(new ForegroundColorSpan(firstColor), 0, firstPart.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(firstPart);

        Spannable secondPart = new SpannableString(secondWord);
        int secondColor = SkinsCenterListActivity.getCurrentSkin(this).getBarTextColor();
        secondPart.setSpan(new ForegroundColorSpan(secondColor), 0, secondPart.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.append(secondPart);
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
                    /* Na stojaka je to 10, opacny stojak - 10, rovina nula, ten gece nevie ale na ktoru stranu je otoceny,
                    ide to z oboch stran od 10 do -10. akuratze ked sa tocis okolo 10 on to odcitava, takze ti to akoby pretecie.
                    preto ja nastavim os ak je moc velka na 8.5 tam zvycajne sa uz tolko netocis aby ti to pretekalo cez 10
                     */
                    DEFAULT_AXIS_X = normaliseAxis(event.values[1]);
                    DEFAULT_AXIS_Y = normaliseAxis(event.values[0]);
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

            AchievementsCenterListActivity.checkAchievements(score, level, this);

            HofDatabaseCenter.getHofDb().open();

            boolean isInHallOfFame = HofDatabaseCenter.getHofDb().isInHallOfFame(score);
            HofDatabaseCenter.getHofDb().close();

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
            GameDialogs.sLostGame = true;
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

        if (Build.VERSION.SDK_INT < 8) {
            mOrientation = config.orientation;
        } else {
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
    }

    public boolean isTutorial() {
        return mTutorialMode;
    }
}
