package sk.palistudios.multigame.game.persistence;

// @author Pali

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class GameSharedPref {

  private static final String KUBA_SKIN_SET_ALREADY = "kuba_skin_set_already";
  private static final String LAST_SEEN_VERSION = "last_seen_version";
  private static SharedPreferences mSharedPreferences;
  private static SharedPreferences.Editor mEditor;
  private static boolean runAlready = false;
  public boolean isGameSaved;

  public static void initSharedPref(Context context) {
    mSharedPreferences = context.getSharedPreferences("Game", 0);
    mEditor = mSharedPreferences.edit();
  }
  public static boolean isGameSaved() {
    return mSharedPreferences.getBoolean("gameSaved", false);
  }

  public static void setGameSaved(boolean status) {
    mEditor.putBoolean("gameSaved", status);
    mEditor.commit();
  }

  public static boolean isMinigamesResolved() {
    return mSharedPreferences.getBoolean("minigamesResolved", false);
  }

  public static String[] getChosenMinigamesNames() {
    String[] result = new String[4];

    result[0] = mSharedPreferences.getString("MinigameV", null);
    result[1] = mSharedPreferences.getString("MinigameH", null);
    result[2] = mSharedPreferences.getString("MinigameT1", null);
    result[3] = mSharedPreferences.getString("MinigameT2", null);

    return result;
  }

  public static void setChosenMinigamesNames(String[] miniGamesNames) {
    mEditor.putString("MinigameV", miniGamesNames[0]);
    mEditor.putString("MinigameH", miniGamesNames[1]);
    mEditor.putString("MinigameT1", miniGamesNames[2]);
    mEditor.putString("MinigameT2", miniGamesNames[3]);
    mEditor.putBoolean("minigamesResolved", true);
    mEditor.commit();
  }

  //    public static boolean setGameMode(String gameMode) {
  //        boolean isNoviceModeActive = mSharedPreferences.getBoolean("noviceModeActive", true);
  //
  //        if (isNoviceModeActive) {
  //            mEditor.putBoolean("noviceModeActive", false);
  //        } else {
  //            mEditor.putBoolean("noviceModeActive", true);
  //        }
  //        mEditor.commit();
  //        return mSharedPreferences.getBoolean("noviceModeActive", true);
  //    }
  public static void saveGameDetails(final int scoreToSave, final int levelToSave,
      final int framesToSave, final boolean[] activeMinigames) {
    GameSharedPref.mEditor.putInt("score", scoreToSave);
    GameSharedPref.mEditor.putInt("level", levelToSave);
    GameSharedPref.mEditor.putInt("frames", framesToSave);
    GameSharedPref.mEditor.putBoolean("savedMinigame1Active", activeMinigames[0]);
    GameSharedPref.mEditor.putBoolean("savedMinigame2Active", activeMinigames[1]);
    GameSharedPref.mEditor.putBoolean("savedMinigame3Active", activeMinigames[2]);
    GameSharedPref.mEditor.putBoolean("savedMinigame4Active", activeMinigames[3]);

    GameSharedPref.mEditor.commit();
  }

  public static int[] loadGameDetails() {
    int[] details = new int[3];
    details[0] = GameSharedPref.mSharedPreferences.getInt("frames", 0);
    details[1] = GameSharedPref.mSharedPreferences.getInt("score", 0);
    details[2] = GameSharedPref.mSharedPreferences.getInt("level", 1);

    return details;
  }

  public static boolean[] loadMinigamesActivity() {
    boolean[] activeMinigames = new boolean[4];
    activeMinigames[0] = GameSharedPref.mSharedPreferences.getBoolean("savedMinigame1Active", false);
    activeMinigames[1] = GameSharedPref.mSharedPreferences.getBoolean("savedMinigame2Active", false);
    activeMinigames[2] = GameSharedPref.mSharedPreferences.getBoolean("savedMinigame3Active", false);
    activeMinigames[3] = GameSharedPref.mSharedPreferences.getBoolean("savedMinigame4Active", false);
    return activeMinigames;
  }

  public static boolean isTutorialModeActivated() {
    return (mSharedPreferences.getString("game_mode", "Tutorial").compareTo("Tutorial") == 0);
  }

  public static boolean isMinigamesNamesInicialized() {
    return mSharedPreferences.getBoolean("inicialized", false);
  }

  public static boolean isMinigameChosen(String minigameName) {
    if (mSharedPreferences.getString("MinigameV", null).compareTo(minigameName) == 0) {
      return true;
    }
    if (mSharedPreferences.getString("MinigameH", null).compareTo(minigameName) == 0) {
      return true;
    }
    if (mSharedPreferences.getString("MinigameT1", null).compareTo(minigameName) == 0) {
      return true;
    }
    if (mSharedPreferences.getString("MinigameT2", null).compareTo(minigameName) == 0) {
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
    return mSharedPreferences.getString("musicLoopChosen", "dst_blam");
  }

  public static void setMusicLoopChosen(String musicChosen) {
    mEditor.putString("musicLoopChosen", musicChosen);
    mEditor.commit();
  }

  public static void setSkinChosen(String skinChosen) {
    mEditor.putString("skinChosen", skinChosen);
    mEditor.commit();
  }

  public static String getChosenSkin() {
    return mSharedPreferences.getString("skinChosen", "kuba");
  }

  public static boolean isSkinChosen(String currentSkinName) {
    if (getChosenSkin().compareTo(currentSkinName) == 0) {
      return true;
    }
    return false;
  }

  public static void unlockInitialItems() {
    mEditor.putBoolean("HBalance_locked", false);
    mEditor.putBoolean("VBird_locked", false);
    mEditor.putBoolean("TGatherer_locked", false);
    mEditor.putBoolean("TCatcher_locked", false);
    //        mEditor.putBoolean("summer_locked", false);
    mEditor.putBoolean("kuba_locked", false);
    mEditor.putBoolean("dst_blam_locked", false);

    mEditor.commit();
  }

  public static boolean isItemLocked(String computerName) {
    return mSharedPreferences.getBoolean(computerName + "_locked", true);
  }

  public static void lockItem(String itemName) {
    mEditor.putBoolean(itemName + "_locked", true);
    mEditor.commit();
  }

  public static void unlockItem(String itemName) {
    mEditor.putBoolean(itemName + "_locked", false);
    mEditor.commit();
  }

  public static boolean isAchievementFulfilled(String achievementName) {
    return mSharedPreferences.getBoolean(achievementName, false);

  }

  public static void achievementFulfilled(String achievementName, String correspondingItem) {
    setAchievementFulfilled(achievementName);
    //        String correspondingItem = resolveCorrespondingItem(achievementName);
    unlockItem(correspondingItem);
  }

  private static void setAchievementFulfilled(String achievementName) {
    mEditor.putBoolean(achievementName, true);
    mEditor.commit();
  }

  public static boolean isMusicOn() {
    return mSharedPreferences.getBoolean("music_on", true);
  }

  public static void setMusicOn(boolean music_on) {
    mEditor.putBoolean("music_on", music_on);
    mEditor.commit();
  }

  public static boolean isSoundOn() {
    return mSharedPreferences.getBoolean("sound_on", true);
  }

  public static void setSoundOn(boolean sound_on) {
    mEditor.putBoolean("sound_on", sound_on);
    mEditor.commit();
  }

  public static String getGameMode() {
    return mSharedPreferences.getString("game_mode", "Tutorial");
  }

  public static void setGameMode(String game_mode) {
    mEditor.putString("game_mode", game_mode);
    mEditor.commit();

  }

  public static void clear() {
    mEditor.clear();
    mEditor.commit();
  }

  public static void onTutorialCompleted() {
    mEditor.putBoolean("tutorialCompleted", true);
    mEditor.commit();
    GameSharedPref.setGameMode("Classic");
    //        ActivityUI.runOnUiThread(new Runnable() {
    //            @Override
    //            public void run() {
    ////                Toaster.toastShort("Tutorial mode deactivated.", ActivityUI);
    //            }
    //        });
  }

  public static boolean isTutorialCompleted() {
    return mSharedPreferences.getBoolean("tutorialCompleted", false);
  }

  public static void increaseTimesMultigameRun() {
    int timesOld = getTimesMultigameRun();

    mEditor.putInt("timesAppRun", timesOld + 1);
    mEditor.commit();

  }

  public static int getTimesMultigameRun() {
    return mSharedPreferences.getInt("timesAppRun", 0);
  }

  public static boolean isPremium() {
    return mSharedPreferences.getBoolean("premium", false);
  }

  public static void upgradedToPremium() {
    mEditor.putBoolean("premium", true);
    mEditor.commit();
  }

  public static void setAdShownAlready(boolean b) {
    mEditor.putBoolean("ad_shown", b);
    mEditor.commit();
  }

  public static boolean hasAdBeenShownAlready() {
    return mSharedPreferences.getBoolean("ad_shown", false);
  }

  public static boolean getAutoCalibrationEnabled() {
    return mSharedPreferences.getBoolean("autocalibration", true);
  }

  public static void setAutocalibration(boolean b) {
    mEditor.putBoolean("autocalibration", b);
    mEditor.commit();
  }

  public static String getLastHofName() {
    return mSharedPreferences.getString("last_hof_name", "");
  }

  public static void setLastHofName(String name) {
    mEditor.putString("last_hof_name", name);
    mEditor.commit();
  }

  public static boolean getDbInitialized() {
    return mSharedPreferences.getBoolean("db_initialized", false);
  }

  public static void setDbInitialized(boolean b) {
    mEditor.putBoolean("db_initialized", b);
    mEditor.commit();
  }

  public static boolean isMinigamesInitialized() {
    return mSharedPreferences.getBoolean("minigamesInitialized", false);
  }

  public static void setMinigamesInitialized(boolean status) {
    mEditor.putBoolean("minigamesInitialized", status);
    mEditor.commit();
  }

  public static void SetChosenMinigamesNames(String[] newActiveMinigamesNames) {
    mEditor.putString("MinigameV", newActiveMinigamesNames[0]);
    mEditor.putString("MinigameH", newActiveMinigamesNames[1]);
    mEditor.putString("MinigameT1", newActiveMinigamesNames[2]);
    mEditor.putString("MinigameT2", newActiveMinigamesNames[3]);
    mEditor.putBoolean("inicialized", true);
    mEditor.commit();
  }

  public static void initializeAllMinigamesInfo(String[] allMinigames) {
    mEditor.putInt("AllMinigamesCount", allMinigames.length);
    StringBuilder minigameKey = new StringBuilder();
    int rank = 1;

    for (String minigame : allMinigames) {
      minigameKey.append("AllMinigames").append(rank).append("Name");
      mEditor.putString(minigameKey.toString(), minigame);
      minigameKey.setLength(0);

      boolean isMinigameChosen = isMinigameChosen(minigame);
      minigameKey.append("AllMinigames").append(rank).append("Chosen");
      mEditor.putBoolean(minigameKey.toString(), isMinigameChosen);
      minigameKey.setLength(0);

      minigameKey.append("AllMinigames").append(rank).append("Type");
      mEditor.putString(minigameKey.toString(), String.valueOf(minigame.charAt(0)));
      minigameKey.setLength(0);

      rank++;
    }

    mEditor.commit();
  }

  //    public static void initializeMusicInfo(String[] allMusicLoopsPCNames,
  // String[] allMusicLoopsHumanNames) {
  //        mEditor.putInt("AllMusicLoopsCount", allMusicLoopsPCNames.length);
  //        StringBuilder minigameKey = new StringBuilder();
  //        int rank = 1;
  //
  //        for (String musicLoop : allMusicLoopsPCNames) {
  //            minigameKey.append("AllMusicLoops").append(rank).append("PCName");
  //            mEditor.putString(minigameKey.toString(), musicLoop);
  //            minigameKey.setLength(0);
  //
  //            boolean isSoundChosen = isMusicLoopChosen(musicLoop);
  //            minigameKey.append("AllMusicLoops").append(rank).append("Chosen");
  //            mEditor.putBoolean(minigameKey.toString(), isSoundChosen);
  //            minigameKey.setLength(0);
  //
  //            rank++;
  //        }
  //
  //        rank = 1;
  //        for (String musicLoop : allMusicLoopsHumanNames) {
  //            minigameKey.append("AllMusicLoops").append(rank).append("HumanName");
  //            mEditor.putString(minigameKey.toString(), musicLoop);
  //            minigameKey.setLength(0);
  //            rank++;
  //        }
  //
  //        mEditor.commit();
  //    }
  public static String[] getAllMinigamesNames() {
    int numberOfMinigames = mSharedPreferences.getInt("AllMinigamesCount", -1);
    String[] allMinigamesNames = new String[numberOfMinigames];

    for (int i = 1; i <= numberOfMinigames; i++) {
      allMinigamesNames[i - 1] = mSharedPreferences.getString("AllMinigames" + i + "Name", null);
    }

    return allMinigamesNames;
  }

  public static String[] getAllMinigamesTypes() {
    int numberOfMinigames = mSharedPreferences.getInt("AllMinigamesCount", -1);
    String[] allMinigamesTypes = new String[numberOfMinigames];

    for (int i = 1; i <= numberOfMinigames; i++) {
      allMinigamesTypes[i] = mSharedPreferences.getString("AllMinigames" + i + "Type", null);
    }

    return allMinigamesTypes;
  }

  //    public static String[] getAllMusicLoopsPCNames() {
  //        int numberOfMusicFiles = mSharedPreferences.getInt("AllMusicLoopsCount", -1);
  //        String[] allMusicNames = new String[numberOfMusicFiles];
  //
  //        for (int i = 1; i <= numberOfMusicFiles; i++) {
  //            allMusicNames[i - 1] = mSharedPreferences.getString("AllMusicLoops" + i + "PCName", null);
  //        }
  //
  //        return allMusicNames;
  //    }
  //
  //    public static String[] getAllMusicLoopsHumanNames() {
  //        int numberOfMusicFiles = mSharedPreferences.getInt("AllMusicLoopsCount", -1);
  //        String[] allMusicNames = new String[numberOfMusicFiles];
  //
  //        for (int i = 1; i <= numberOfMusicFiles; i++) {
  //            allMusicNames[i - 1] = mSharedPreferences.getString("AllMusicLoops" + i + "HumanName",
  // null);
  //        }
  //
  //        return allMusicNames;
  //    }
  public static boolean isAppRunningForFirstTime() {
    return mSharedPreferences.getBoolean("firstTime", true);
  }

  public static void setAppRunningForFirstTime(boolean status) {
    mEditor.putBoolean("firstTime", status);
    mEditor.commit();
  }

  public static boolean isPlayingGameFirstTime() {
    return mSharedPreferences.getBoolean("firstTimePlayingGame", true);
  }

  public static void setPlayingGameFirstTimeFalse() {
    mEditor.putBoolean("firstTimePlayingGame", false);
    mEditor.commit();
  }

  public static int getStatsGamesPlayed() {
    return mSharedPreferences.getInt("st_games_played", 0);
  }

  public static void StatsGamesPlayedIncrease() {
    mEditor.putInt("st_games_played", getStatsGamesPlayed() + 1);
    mEditor.commit();
  }

  public static boolean getKubaSkinSetAlready() {
    return mSharedPreferences.getBoolean(KUBA_SKIN_SET_ALREADY, false);
  }

  public static void setKubaSkinSetAlready() {
    mEditor.putBoolean(KUBA_SKIN_SET_ALREADY, true);
    mEditor.commit();
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
    return mSharedPreferences.getInt(LAST_SEEN_VERSION, 0);
  }

  public static void setLastSeenVersion(Context context) {
    int version = -1;
    try {
      PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
      version = pInfo.versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    mEditor.putInt(LAST_SEEN_VERSION, version);
    mEditor.commit();
  }

}
