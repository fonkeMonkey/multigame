package sk.palistudios.multigame;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Pali on 5. 10. 2014.
 */
public class MgTracker {
  public static boolean sGameFinishedTracked = false;

  private static final String CATEGORY_GAME_TUTORIAL = "category_game_tutorial";
  private static final String CATEGORY_GAME_CLASSIC = "category_game_classic";
  private static final String CATEGORY_CC = "category_customization_center";

  private static final String ACTION_TUTORIAL_LEVEL_STARTED = "action_tutorial_level_started";
  private static final String ACTION_TUTORIAL_WINDOW_SHOWN = "action_tutorial_window_shown";
  /* Tutorial code for the dialog window Tutorial finished. */
  public static final int LABEL_TUTORIAL_WINNER = 100;
  private static final String ACTION_TUTORIAL_RESTART_WINDOW_SHOWN =
      "action_tutorial_restart_window_shown";
  private static final String ACTION_TUTORIAL_RESTART_WINDOWS_PER_SESSION =
      "action_tutorial_restart_windows_per_session";
  private static final String ACTION_TUTORIAL_RESTARTED = "action_tutorial_restarted";
  private static final String ACTION_GAME_STARTED = "action_game_started";
  private static final String ACTION_GAME_FINISHED_TIME_PLAYED = "action_game_finished_time_played";
  private static final String ACTION_GAME_FINISHED_LEVEL = "action_game_finished_level";
  private static final String ACTION_GAME_FINISHED_SCORE = "action_game_finished_score";
  private static final String ACTION_GAMES_PER_SESSION = "action_games_per_session";
  private static final String ACTION_GAME_WINNER_BUTTON_PUSHED = "action_game_winner_button_pushed";
  private static final String ACTION_GAME_LOSER_BUTTON_PUSHED = "action_game_loser_button_pushed";
  private static final String LABEL_GAME_WINNER_OK = "action_game_winner_ok_button_pushed";
  private static final String LABEL_GAME_WINNER_RETRY = "action_game_winner_retry_button_pushed";
  private static final String LABEL_GAME_WINNER_SHARE = "action_game_winner_share_button_pushed";
  private static final String LABEL_GAME_LOSER_RETRY = "action_game_loser_retry_button_pushed";
  private static final String LABEL_GAME_LOSER_SHARE = "action_game_loser_share_button_pushed";
  private static final String LABEL_GAME_LOSER_OK = "action_game_loser_ok_button_pushed";
  private static final String ACTION_MUSIC_CHANGED = "action_music_changed";
  private static final String ACTION_SKIN_CHANGED = "action_skin_changed";
  private static final String ACTION_MINIGAME_CHANGED = "action_minigame_changed";

  private static Tracker sTracker = null;

  /* njn */
  public static void init(Context context) {
    sTracker = getTracker(context);
  }

  public static synchronized Tracker getTracker(Context context) {
    if (sTracker == null) {
      GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
      boolean isDebugBuild =
          0 != (context.getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE);
      analytics.setDryRun(isDebugBuild);
      Log.d("Analyitcs in dry mode :", String.valueOf(isDebugBuild));
      sTracker = analytics.newTracker(R.xml.global_tracker);
    }
    return sTracker;
  }

  public static void trackTutorialLevelStarted(int levelNo) {
    sTracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_GAME_TUTORIAL).setAction(
        ACTION_TUTORIAL_LEVEL_STARTED).setLabel(String.valueOf(levelNo)).build());
  }

  public static void trackTutorialWindowShown(int levelNo) {
    sTracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_GAME_TUTORIAL).setAction(
        ACTION_TUTORIAL_LEVEL_STARTED).setLabel(String.valueOf(levelNo)).build());
    Log.d("tutorial window, number :", String.valueOf(levelNo));
  }

  public static void trackTutorialRestartWindowsShown() {
    sTracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_GAME_TUTORIAL).setAction(
        ACTION_TUTORIAL_RESTART_WINDOW_SHOWN).build());
  }

  /* Sent when tutorial is either completed or exited. (then it is sent after check in the main
  Menu. - EXITED means also interrupted by call. */
  public static void trackTutorialRestartWindowsShownPerSession(int noOfWindows, boolean finished) {
    sTracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_GAME_TUTORIAL).setAction(
        ACTION_TUTORIAL_RESTART_WINDOWS_PER_SESSION).setLabel(String.valueOf(
        finished ? noOfWindows : -noOfWindows)).build());
    Log.d("Tutorial restarts per session", String.valueOf(noOfWindows));
  }

  public static void trackTutorialRestarted(int sTutorialLastLevel) {
    sTracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_GAME_TUTORIAL).setAction(
        ACTION_TUTORIAL_RESTARTED).setValue(sTutorialLastLevel).build());
  }

  public static void trackGameStarted() {
    sTracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_GAME_CLASSIC).setAction(
        ACTION_GAME_STARTED).build());
    sGameFinishedTracked = false;
  }

  public static void trackGameFinished(long timePlayed, int level, int score) {
    if (sGameFinishedTracked != true) {
      sGameFinishedTracked = true;
      sTracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_GAME_CLASSIC).setAction(
          ACTION_GAME_FINISHED_TIME_PLAYED).setValue(timePlayed).build());
      sTracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_GAME_CLASSIC).setAction(
          ACTION_GAME_FINISHED_LEVEL).setValue(level).build());
      sTracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_GAME_CLASSIC).setAction(
          ACTION_GAME_FINISHED_SCORE).setValue(score).build());
    }
  }

  public static void trackGamesPerSession(int times) {
    sTracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_GAME_CLASSIC).setAction(
        ACTION_GAMES_PER_SESSION).setValue(times).build());
    Log.d("Games per session", String.valueOf(times));
  }

  public static void trackGameWinnerOkButtonPushed() {
    sTracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_GAME_CLASSIC).setAction(
        ACTION_GAME_WINNER_BUTTON_PUSHED).setLabel(LABEL_GAME_WINNER_OK).build());
  }

  public static void trackGameWinnerRetryButtonPushed() {
    sTracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_GAME_CLASSIC).setAction(
        ACTION_GAME_WINNER_BUTTON_PUSHED).setLabel(LABEL_GAME_WINNER_RETRY).build());
  }

  public static void trackGameWinnerShareButtonPushed() {
    sTracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_GAME_CLASSIC).setAction(
        ACTION_GAME_WINNER_BUTTON_PUSHED).setLabel(LABEL_GAME_WINNER_SHARE).build());
  }

  public static void trackGameLoserRetryButtonPushed() {
    sTracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_GAME_CLASSIC).setAction(
        ACTION_GAME_LOSER_BUTTON_PUSHED).setLabel(LABEL_GAME_LOSER_RETRY).build());
    Log.d("retry pushed", "njn");
  }

  public static void trackGameLoserShareButtonPushed() {
    sTracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_GAME_CLASSIC).setAction(
        ACTION_GAME_LOSER_BUTTON_PUSHED).setLabel(LABEL_GAME_LOSER_SHARE).build());

  }

  public static void trackGameLoserOkButtonPushed() {
    sTracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_GAME_CLASSIC).setAction(
        ACTION_GAME_LOSER_BUTTON_PUSHED).setLabel(LABEL_GAME_LOSER_OK).build());

  }

  public static void trackMusicChanged(String oldMusic, String newMusic) {
    sTracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_CC).setAction(
        ACTION_MUSIC_CHANGED).setLabel(oldMusic + " -> " + newMusic).build());
  }

  public static void trackSkinChanged(String oldSkin, String newSkin) {
    sTracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_CC).setAction(
        ACTION_SKIN_CHANGED).setLabel(oldSkin + " -> " + newSkin).build());
  }

  public static void trackMinigameChanged(String newMinigame) {
    sTracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_CC).setAction(
        ACTION_MINIGAME_CHANGED).setLabel(newMinigame).build());
  }

}