package sk.palistudios.multigame.game.persistence;

// @author Pali

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import sk.palistudios.multigame.MgApplication;

public class MGSettings {

  private static final String KUBA_SKIN_SET_ALREADY = "kuba_skin_set_already";
  private static final String LAST_SEEN_VERSION = "last_seen_version";
  private static SharedPreferences sSharedPreferences;
  private static SharedPreferences.Editor sEditor;

  static {
    sSharedPreferences = MgApplication.getContext().getSharedPreferences("Game", 0);
    sEditor = sSharedPreferences.edit();
  }

  public static boolean isGameSaved() {
    return sSharedPreferences.getBoolean("gameSaved", false);
  }

  public static void setGameSaved(boolean status) {
    sEditor.putBoolean("gameSaved", status);
    sEditor.commit();
  }

  public static String[] getChosenMinigamesNames() {
    String[] result = new String[4];

    result[0] = sSharedPreferences.getString("MinigameV", null);
    result[1] = sSharedPreferences.getString("MinigameH", null);
    result[2] = sSharedPreferences.getString("MinigameT1", null);
    result[3] = sSharedPreferences.getString("MinigameT2", null);

    return result;
  }

  public static int getHighestScore() {
    return sSharedPreferences.getInt("highestScore", -1);
  }

  public static void setHighestScore(int score) {
    sEditor.putInt("highestScore", score);
    sEditor.commit();
  }

  /**
   * Returns whether the new highest score was alredy submitted to online leaderboard.
   *
   * @return True if submitted, false otherwise.
   */
  public static boolean getHighestScoreSubmitted() {
    return sSharedPreferences.getBoolean("highestScoreSubmitted", false);
  }

  /**
   * Sets whether the local top score was already submitted to online leaderboard.
   *
   * @param submitted True to submitted, false otherwise.
   */
  public static void setHighestScoreSubmitted(boolean submitted) {
    sEditor.putBoolean("highestScoreSubmitted", submitted);
    sEditor.commit();
  }

  public static void saveGameDetails(final int scoreToSave, final int levelToSave,
      final int framesToSave, final boolean[] activeMinigames) {
    MGSettings.sEditor.putInt("score", scoreToSave);
    MGSettings.sEditor.putInt("level", levelToSave);
    MGSettings.sEditor.putInt("frames", framesToSave);
    MGSettings.sEditor.putBoolean("savedMinigame1Active", activeMinigames[0]);
    MGSettings.sEditor.putBoolean("savedMinigame2Active", activeMinigames[1]);
    MGSettings.sEditor.putBoolean("savedMinigame3Active", activeMinigames[2]);
    MGSettings.sEditor.putBoolean("savedMinigame4Active", activeMinigames[3]);

    MGSettings.sEditor.commit();
  }

  public static int[] loadGameDetails() {
    int[] details = new int[3];
    details[0] = MGSettings.sSharedPreferences.getInt("frames", 0);
    details[1] = MGSettings.sSharedPreferences.getInt("score", 0);
    details[2] = MGSettings.sSharedPreferences.getInt("level", 1);

    return details;
  }

  public static boolean[] getMinigamesActivityFlags() {
    boolean[] activeMinigames = new boolean[4];
    activeMinigames[0] = MGSettings.sSharedPreferences.getBoolean("savedMinigame1Active",
        false);
    activeMinigames[1] = MGSettings.sSharedPreferences.getBoolean("savedMinigame2Active",
        false);
    activeMinigames[2] = MGSettings.sSharedPreferences.getBoolean("savedMinigame3Active",
        false);
    activeMinigames[3] = MGSettings.sSharedPreferences.getBoolean("savedMinigame4Active",
        false);
    return activeMinigames;
  }

  public static boolean isTutorialModeActivated() {
    return (sSharedPreferences.getString("game_mode", "Tutorial").compareTo("Tutorial") == 0);
  }

  public static boolean isMinigameChosen(String minigameName) {
    if (sSharedPreferences.getString("MinigameV", null).compareTo(minigameName) == 0) {
      return true;
    }
    if (sSharedPreferences.getString("MinigameH", null).compareTo(minigameName) == 0) {
      return true;
    }
    if (sSharedPreferences.getString("MinigameT1", null).compareTo(minigameName) == 0) {
      return true;
    }
    if (sSharedPreferences.getString("MinigameT2", null).compareTo(minigameName) == 0) {
      return true;
    }
    return false;
  }

  public static boolean isMusicLoopChosen(String soundName) {
    if (getMusicLoopChosen().compareTo(soundName) == 0) {
      return true;
    }
    return false;
  }

  public static String getMusicLoopChosen() {
    return sSharedPreferences.getString("musicLoopChosen", "dst_blam");
  }

  public static void setMusicLoopChosen(String musicChosen) {
    sEditor.putString("musicLoopChosen", musicChosen);
    sEditor.commit();
  }

  public static void setSkinChosen(String skinChosen) {
    sEditor.putString("skinChosen", skinChosen);
    sEditor.commit();
  }

  public static String getChosenSkin() {
    return sSharedPreferences.getString("skinChosen", "kuba");
  }

  public static boolean isSkinChosen(String currentSkinName) {
    if (getChosenSkin().compareTo(currentSkinName) == 0) {
      return true;
    }
    return false;
  }

  public static void unlockInitialItems() {
    sEditor.putBoolean("HBalance_locked", false);
    sEditor.putBoolean("VBird_locked", false);
    sEditor.putBoolean("TGatherer_locked", false);
    sEditor.putBoolean("TCatcher_locked", false);
    sEditor.putBoolean("kuba_locked", false);
    sEditor.putBoolean("dst_blam_locked", false);

    sEditor.commit();
  }

  public static boolean isItemLocked(String computerName) {
    return sSharedPreferences.getBoolean(computerName + "_locked", true);
  }

  public static void lockItem(String itemName) {
    sEditor.putBoolean(itemName + "_locked", true);
    sEditor.commit();
  }

  public static void unlockItem(String itemName) {
    sEditor.putBoolean(itemName + "_locked", false);
    sEditor.commit();
  }

  public static boolean isAchievementFulfilled(String achievementName) {
    return sSharedPreferences.getBoolean(achievementName, false);

  }

  public static void achievementFulfilled(String achievementName, String correspondingItem) {
    setAchievementFulfilled(achievementName);
    unlockItem(correspondingItem);
  }

  private static void setAchievementFulfilled(String achievementName) {
    sEditor.putBoolean(achievementName, true);
    sEditor.commit();
  }

  public static boolean isMusicOn() {
    return sSharedPreferences.getBoolean("music_on", true);
  }

  public static void setMusicOn(boolean music_on) {
    sEditor.putBoolean("music_on", music_on);
    sEditor.commit();
  }

  public static boolean isSoundOn() {
    return sSharedPreferences.getBoolean("sound_on", true);
  }

  public static void setSoundOn(boolean sound_on) {
    sEditor.putBoolean("sound_on", sound_on);
    sEditor.commit();
  }

  public static String getGameMode() {
    return sSharedPreferences.getString("game_mode", "Tutorial");
  }

  public static void setGameMode(String game_mode) {
    sEditor.putString("game_mode", game_mode);
    sEditor.commit();

  }

  public static void clear() {
    sEditor.clear();
    sEditor.commit();
  }

  public static void onTutorialCompleted() {
    sEditor.putBoolean("tutorialCompleted", true);
    sEditor.commit();
    MGSettings.setGameMode("Classic");
  }

  public static boolean isTutorialCompleted() {
    return sSharedPreferences.getBoolean("tutorialCompleted", false);
  }

  public static void increaseTimesMultigameRun() {
    int timesOld = getTimesMultigameRun();

    sEditor.putInt("timesAppRun", timesOld + 1);
    sEditor.commit();

  }

  public static int getTimesMultigameRun() {
    return sSharedPreferences.getInt("timesAppRun", 0);
  }

  public static boolean isPremium() {
    return sSharedPreferences.getBoolean("premium", false);
  }

  public static void upgradedToPremium() {
    sEditor.putBoolean("premium", true);
    sEditor.commit();
  }

  public static void setAdShownAlready(boolean b) {
    sEditor.putBoolean("ad_shown", b);
    sEditor.commit();
  }

  public static boolean hasAdBeenShownAlready() {
    return sSharedPreferences.getBoolean("ad_shown", false);
  }

  public static boolean isAutoCalibrationEnabled() {
    return sSharedPreferences.getBoolean("autocalibration", true);
  }

  public static void setAutocalibration(boolean b) {
    sEditor.putBoolean("autocalibration", b);
    sEditor.commit();
  }

  public static String getLastHofName() {
    return sSharedPreferences.getString("last_hof_name", "");
  }

  public static void setLastHofName(String name) {
    sEditor.putString("last_hof_name", name);
    sEditor.commit();
  }

  public static boolean getDbInitialized() {
    return sSharedPreferences.getBoolean("db_initialized", false);
  }

  public static void setDbInitialized(boolean b) {
    sEditor.putBoolean("db_initialized", b);
    sEditor.commit();
  }

  public static void SetChosenMinigamesNames(String[] newActiveMinigamesNames) {
    sEditor.putString("MinigameV", newActiveMinigamesNames[0]);
    sEditor.putString("MinigameH", newActiveMinigamesNames[1]);
    sEditor.putString("MinigameT1", newActiveMinigamesNames[2]);
    sEditor.putString("MinigameT2", newActiveMinigamesNames[3]);
    sEditor.putBoolean("inicialized", true);
    sEditor.commit();
  }

  public static void initializeAllMinigamesInfo(String[] allMinigames) {
    sEditor.putInt("AllMinigamesCount", allMinigames.length);
    StringBuilder minigameKey = new StringBuilder();
    int rank = 1;

    for (String minigame : allMinigames) {
      minigameKey.append("AllMinigames").append(rank).append("Name");
      sEditor.putString(minigameKey.toString(), minigame);
      minigameKey.setLength(0);

      boolean isMinigameChosen = isMinigameChosen(minigame);
      minigameKey.append("AllMinigames").append(rank).append("Chosen");
      sEditor.putBoolean(minigameKey.toString(), isMinigameChosen);
      minigameKey.setLength(0);

      minigameKey.append("AllMinigames").append(rank).append("Type");
      sEditor.putString(minigameKey.toString(), String.valueOf(minigame.charAt(0)));
      minigameKey.setLength(0);

      rank++;
    }

    sEditor.commit();
  }
  public static String[] getAllMinigamesNames() {
    int numberOfMinigames = sSharedPreferences.getInt("AllMinigamesCount", -1);
    String[] allMinigamesNames = new String[numberOfMinigames];

    for (int i = 1; i <= numberOfMinigames; i++) {
      allMinigamesNames[i - 1] = sSharedPreferences.getString("AllMinigames" + i + "Name", null);
    }

    return allMinigamesNames;
  }

  public static String[] getAllMinigamesTypes() {
    int numberOfMinigames = sSharedPreferences.getInt("AllMinigamesCount", -1);
    String[] allMinigamesTypes = new String[numberOfMinigames];

    for (int i = 1; i <= numberOfMinigames; i++) {
      allMinigamesTypes[i] = sSharedPreferences.getString("AllMinigames" + i + "Type", null);
    }

    return allMinigamesTypes;
  }
  public static boolean isAppRunningForFirstTime() {
    return sSharedPreferences.getBoolean("firstTime", true);
  }

  public static void setAppRunningForFirstTime(boolean status) {
    sEditor.putBoolean("firstTime", status);
    sEditor.commit();
  }

  public static boolean isPlayingGameFirstTime() {
    return sSharedPreferences.getBoolean("firstTimePlayingGame", true);
  }

  public static void setPlayingGameFirstTimeFalse() {
    sEditor.putBoolean("firstTimePlayingGame", false);
    sEditor.commit();
  }

  public static int getStatsGamesPlayed() {
    return sSharedPreferences.getInt("st_games_played", 0);
  }

  public static void StatsGamesPlayedIncrease() {
    sEditor.putInt("st_games_played", getStatsGamesPlayed() + 1);
    sEditor.commit();
  }

  public static boolean getKubaSkinSetAlready() {
    return sSharedPreferences.getBoolean(KUBA_SKIN_SET_ALREADY, false);
  }

  public static void setKubaSkinSetAlready() {
    sEditor.putBoolean(KUBA_SKIN_SET_ALREADY, true);
    sEditor.commit();
  }

  public static boolean isUpdateOrFreshInstall(Context context) {
    int oldVersion = getLastSeenVersion();
    int currentVersion = -1;
    try {
      PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
      currentVersion = pInfo.versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    if (oldVersion == -1 || currentVersion == -1) {
            /* Should not happen ever. */
      return false;
    }

    if (oldVersion == 0) {
      return true;
    }
    if (oldVersion == currentVersion) {
      return false;
    }
    return true;
  }

  private static int getLastSeenVersion() {
    return sSharedPreferences.getInt(LAST_SEEN_VERSION, 0);
  }

  public static void setLastSeenVersion(Context context) {
    int version = -1;
    try {
      PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
      version = pInfo.versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    sEditor.putInt(LAST_SEEN_VERSION, version);
    sEditor.commit();
  }

  public static void unlockItemsAll() {
    unlockItem("HBalance");
    unlockItem("VBird");
    unlockItem("VBouncer");
    unlockItem("TGatherer");
    unlockItem("TCatcher");
    unlockItem("TInvader");
    unlockItem("summer");
    unlockItem("girl_power");
    unlockItem("blue_sky");
    unlockItem("dst_cyberops");
    unlockItem("dst_blam");
    unlockItem("dst_cv_x");
  }

}
