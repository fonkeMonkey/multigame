package sk.palistudios.multigame.game.persistence;

// @author Pali

import android.app.Activity;
import android.content.SharedPreferences;

public class GameSharedPref {

    private static final String KUBA_SKIN_SET_ALREADY = "kuba_skin_set_already";
    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor editor;
    private static boolean runAlready = false;
    public boolean isGameSaved;

    public static void initSharedPref(Activity activity) {

        sharedPref = activity.getSharedPreferences("Game", 0);
        editor = sharedPref.edit();

//        if (!runAlready) {
//            debug();
//        }

    }

    //    private static void debug() {
//        setGameSaved(false);
//        runAlready = true;
//        editor.putBoolean("isMinigame1Active", true);
//        editor.putBoolean("isMinigame2Active", false);
//        editor.putBoolean("isMinigame3Active", true);
//        editor.putBoolean("isMinigame4Active", false);
//    }
    public static boolean isGameSaved() {
        return sharedPref.getBoolean("gameSaved", false);
    }

    public static void setGameSaved(boolean status) {
        editor.putBoolean("gameSaved", status);
        editor.commit();
    }

    public static boolean isMinigamesResolved() {
        return sharedPref.getBoolean("minigamesResolved", false);
    }

    public static String[] getChosenMinigamesNames() {
        String[] result = new String[4];

        result[0] = sharedPref.getString("MinigameV", null);
        result[1] = sharedPref.getString("MinigameH", null);
        result[2] = sharedPref.getString("MinigameT1", null);
        result[3] = sharedPref.getString("MinigameT2", null);

        return result;
    }

    public static void setChosenMinigamesNames(String[] miniGamesNames) {
        editor.putString("MinigameV", miniGamesNames[0]);
        editor.putString("MinigameH", miniGamesNames[1]);
        editor.putString("MinigameT1", miniGamesNames[2]);
        editor.putString("MinigameT2", miniGamesNames[3]);
        editor.putBoolean("minigamesResolved", true);
        editor.commit();
    }

    //    public static boolean setGameMode(String gameMode) {
//        boolean isNoviceModeActive = sharedPref.getBoolean("noviceModeActive", true);
//
//        if (isNoviceModeActive) {
//            editor.putBoolean("noviceModeActive", false);
//        } else {
//            editor.putBoolean("noviceModeActive", true);
//        }
//        editor.commit();
//        return sharedPref.getBoolean("noviceModeActive", true);
//    }
    public static void saveGameDetails(final int scoreToSave, final int levelToSave, final int framesToSave, final boolean[] activeMinigames) {
        GameSharedPref.editor.putInt("score", scoreToSave);
        GameSharedPref.editor.putInt("level", levelToSave);
        GameSharedPref.editor.putInt("frames", framesToSave);
        GameSharedPref.editor.putBoolean("savedMinigame1Active", activeMinigames[0]);
        GameSharedPref.editor.putBoolean("savedMinigame2Active", activeMinigames[1]);
        GameSharedPref.editor.putBoolean("savedMinigame3Active", activeMinigames[2]);
        GameSharedPref.editor.putBoolean("savedMinigame4Active", activeMinigames[3]);

        GameSharedPref.editor.commit();
    }

    public static int[] loadGameDetails() {
        int[] details = new int[3];
        details[0] = GameSharedPref.sharedPref.getInt("frames", 0);
        details[1] = GameSharedPref.sharedPref.getInt("score", 0);
        details[2] = GameSharedPref.sharedPref.getInt("level", 1);

        return details;
    }

    public static boolean[] loadMinigamesActivity() {
        boolean[] activeMinigames = new boolean[4];
        activeMinigames[0] = GameSharedPref.sharedPref.getBoolean("savedMinigame1Active", false);
        activeMinigames[1] = GameSharedPref.sharedPref.getBoolean("savedMinigame2Active", false);
        activeMinigames[2] = GameSharedPref.sharedPref.getBoolean("savedMinigame3Active", false);
        activeMinigames[3] = GameSharedPref.sharedPref.getBoolean("savedMinigame4Active", false);
        return activeMinigames;
    }

    public static boolean isTutorialModeActivated() {
        return (sharedPref.getString("game_mode", "Tutorial").compareTo("Tutorial") == 0);
    }

    public static boolean isMinigamesNamesInicialized() {
        return sharedPref.getBoolean("inicialized", false);
    }

    public static boolean isMinigameChosen(String minigameName) {
        if (sharedPref.getString("MinigameV", null).compareTo(minigameName) == 0) {
            return true;
        }
        if (sharedPref.getString("MinigameH", null).compareTo(minigameName) == 0) {
            return true;
        }
        if (sharedPref.getString("MinigameT1", null).compareTo(minigameName) == 0) {
            return true;
        }
        if (sharedPref.getString("MinigameT2", null).compareTo(minigameName) == 0) {
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

    public static boolean isPlayingFirstTime() {
        return sharedPref.getBoolean("firstTime", true);
    }

    public static void setPlayingFirstTimeFalse() {
        editor.putBoolean("firstTime", false);
        editor.commit();
    }

    public static String getMusicLoopChosen() {
        return sharedPref.getString("musicLoopChosen", "dst_blam");
    }

    public static void setMusicLoopChosen(String musicChosen) {
        editor.putString("musicLoopChosen", musicChosen);
        editor.commit();
    }

    public static void setSkinChosen(String skinChosen) {
        editor.putString("skinChosen", skinChosen);
        editor.commit();
    }

    public static String getChosenSkin() {
        return sharedPref.getString("skinChosen", "kuba");
    }

    public static boolean isSkinChosen(String currentSkinName) {
        if (getChosenSkin().compareTo(currentSkinName) == 0) {
            return true;
        }
        return false;
    }

    public static void unlockInitialItems() {
        editor.putBoolean("HBalance_locked", false);
        editor.putBoolean("VBird_locked", false);
        editor.putBoolean("TGatherer_locked", false);
        editor.putBoolean("TCatcher_locked", false);
//        editor.putBoolean("summer_locked", false);
        editor.putBoolean("kuba_locked", false);
        editor.putBoolean("dst_blam_locked", false);

        editor.commit();
    }

    public static boolean isItemLocked(String computerName) {
        return sharedPref.getBoolean(computerName + "_locked", true);
    }

    public static void lockItem(String itemName) {
        editor.putBoolean(itemName + "_locked", true);
        editor.commit();
    }

    public static void unlockItem(String itemName) {
        editor.putBoolean(itemName + "_locked", false);
        editor.commit();
    }

    public static boolean isAchievementFulfilled(String achievementName) {
        return sharedPref.getBoolean(achievementName, false);

    }

    public static void achievementFulfilled(String achievementName, String correspondingItem) {
        setAchievementFulfilled(achievementName);
//        String correspondingItem = resolveCorrespondingItem(achievementName);
        unlockItem(correspondingItem);
    }

    private static void setAchievementFulfilled(String achievementName) {
        editor.putBoolean(achievementName, true);
        editor.commit();
    }

    public static boolean isMusicOn() {
        return sharedPref.getBoolean("music_on", true);
    }

    public static void setMusicOn(boolean music_on) {
        editor.putBoolean("music_on", music_on);
        editor.commit();
    }

    public static boolean isSoundOn() {
        return sharedPref.getBoolean("sound_on", true);
    }

    public static void setSoundOn(boolean sound_on) {
        editor.putBoolean("sound_on", sound_on);
        editor.commit();
    }

    public static String getGameMode() {
        return sharedPref.getString("game_mode", "Tutorial");
    }

    public static void setGameMode(String game_mode) {
        editor.putString("game_mode", game_mode);
        editor.commit();

    }

    public static void clear() {
        editor.clear();
        editor.commit();
    }

    public static void onTutorialCompleted() {
        editor.putBoolean("tutorialCompleted", true);
        editor.commit();
        GameSharedPref.setGameMode("Classic");
//        ActivityUI.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
////                Toaster.toastShort("Tutorial mode deactivated.", ActivityUI);
//            }
//        });
    }

    public static boolean isTutorialCompleted() {
        return sharedPref.getBoolean("tutorialCompleted", false);
    }

    public static void increaseTimesMultigameRun() {
        int timesOld = getTimesMultigameRun();

        editor.putInt("timesAppRun", timesOld + 1);
        editor.commit();

    }

    public static int getTimesMultigameRun() {
        return sharedPref.getInt("timesAppRun", 0);
    }

    public static boolean isPremium() {
        return sharedPref.getBoolean("premium", false);
    }

    public static void upgradedToPremium() {
        editor.putBoolean("premium", true);
        editor.commit();
    }

    public static void setAdShownAlready(boolean b) {
        editor.putBoolean("ad_shown", b);
        editor.commit();
    }

    public static boolean hasAdBeenShownAlready() {
        return sharedPref.getBoolean("ad_shown", false);
    }

    public static boolean getAutoCalibrationEnabled() {
        return sharedPref.getBoolean("autocalibration", true);
    }

    public static void setAutocalibration(boolean b) {
        editor.putBoolean("autocalibration", b);
        editor.commit();
    }

    public static String getLastHofName() {
        return sharedPref.getString("last_hof_name", "");
    }

    public static void setLastHofName(String name) {
        editor.putString("last_hof_name", name);
        editor.commit();
    }

    public static boolean getDbInitialized() {
        return sharedPref.getBoolean("db_initialized", false);
    }

    public static void setDbInitialized(boolean b) {
        editor.putBoolean("db_initialized", b);
        editor.commit();
    }

    public static boolean isMinigamesInitialized() {
        return sharedPref.getBoolean("minigamesInitialized", false);
    }

    public static void setMinigamesInitialized(boolean status) {
        editor.putBoolean("minigamesInitialized", status);
        editor.commit();
    }

    public static void SetChosenMinigamesNames(String[] newActiveMinigamesNames) {
        editor.putString("MinigameV", newActiveMinigamesNames[0]);
        editor.putString("MinigameH", newActiveMinigamesNames[1]);
        editor.putString("MinigameT1", newActiveMinigamesNames[2]);
        editor.putString("MinigameT2", newActiveMinigamesNames[3]);
        editor.putBoolean("inicialized", true);
        editor.commit();
    }

    public static void initializeAllMinigamesInfo(String[] allMinigames) {
        editor.putInt("AllMinigamesCount", allMinigames.length);
        StringBuilder minigameKey = new StringBuilder();
        int rank = 1;

        for (String minigame : allMinigames) {
            minigameKey.append("AllMinigames").append(rank).append("Name");
            editor.putString(minigameKey.toString(), minigame);
            minigameKey.setLength(0);

            boolean isMinigameChosen = isMinigameChosen(minigame);
            minigameKey.append("AllMinigames").append(rank).append("Chosen");
            editor.putBoolean(minigameKey.toString(), isMinigameChosen);
            minigameKey.setLength(0);


            minigameKey.append("AllMinigames").append(rank).append("Type");
            editor.putString(minigameKey.toString(), String.valueOf(minigame.charAt(0)));
            minigameKey.setLength(0);


            rank++;
        }

        editor.commit();
    }

    //    public static void initializeMusicInfo(String[] allMusicLoopsPCNames, String[] allMusicLoopsHumanNames) {
//        editor.putInt("AllMusicLoopsCount", allMusicLoopsPCNames.length);
//        StringBuilder minigameKey = new StringBuilder();
//        int rank = 1;
//
//        for (String musicLoop : allMusicLoopsPCNames) {
//            minigameKey.append("AllMusicLoops").append(rank).append("PCName");
//            editor.putString(minigameKey.toString(), musicLoop);
//            minigameKey.setLength(0);
//
//            boolean isSoundChosen = isMusicLoopChosen(musicLoop);
//            minigameKey.append("AllMusicLoops").append(rank).append("Chosen");
//            editor.putBoolean(minigameKey.toString(), isSoundChosen);
//            minigameKey.setLength(0);
//
//            rank++;
//        }
//
//        rank = 1;
//        for (String musicLoop : allMusicLoopsHumanNames) {
//            minigameKey.append("AllMusicLoops").append(rank).append("HumanName");
//            editor.putString(minigameKey.toString(), musicLoop);
//            minigameKey.setLength(0);
//            rank++;
//        }
//
//        editor.commit();
//    }
    public static String[] getAllMinigamesNames() {
        int numberOfMinigames = sharedPref.getInt("AllMinigamesCount", -1);
        String[] allMinigamesNames = new String[numberOfMinigames];

        for (int i = 1; i <= numberOfMinigames; i++) {
            allMinigamesNames[i - 1] = sharedPref.getString("AllMinigames" + i + "Name", null);
        }

        return allMinigamesNames;
    }

    public static String[] getAllMinigamesTypes() {
        int numberOfMinigames = sharedPref.getInt("AllMinigamesCount", -1);
        String[] allMinigamesTypes = new String[numberOfMinigames];

        for (int i = 1; i <= numberOfMinigames; i++) {
            allMinigamesTypes[i] = sharedPref.getString("AllMinigames" + i + "Type", null);
        }

        return allMinigamesTypes;
    }

    //    public static String[] getAllMusicLoopsPCNames() {
//        int numberOfMusicFiles = sharedPref.getInt("AllMusicLoopsCount", -1);
//        String[] allMusicNames = new String[numberOfMusicFiles];
//
//        for (int i = 1; i <= numberOfMusicFiles; i++) {
//            allMusicNames[i - 1] = sharedPref.getString("AllMusicLoops" + i + "PCName", null);
//        }
//
//        return allMusicNames;
//    }
//
//    public static String[] getAllMusicLoopsHumanNames() {
//        int numberOfMusicFiles = sharedPref.getInt("AllMusicLoopsCount", -1);
//        String[] allMusicNames = new String[numberOfMusicFiles];
//
//        for (int i = 1; i <= numberOfMusicFiles; i++) {
//            allMusicNames[i - 1] = sharedPref.getString("AllMusicLoops" + i + "HumanName", null);
//        }
//
//        return allMusicNames;
//    }
    public static boolean isGameRunningForFirstTime() {
        return sharedPref.getBoolean("firstTime", true);
    }

    public static void setGameRunningForFirstTime(boolean status) {
        editor.putBoolean("firstTime", status);
        editor.commit();
    }

    public static int getStatsGamesPlayed() {
        return sharedPref.getInt("st_games_played", 0);
    }

    public static void StatsGamesPlayedIncrease() {
        editor.putInt("st_games_played", getStatsGamesPlayed() + 1);
        editor.commit();
    }

    public static boolean getKubaSkinSetAlready() {
        return sharedPref.getBoolean(KUBA_SKIN_SET_ALREADY, false);
    }

    public static void setKubaSkinSetAlready() {
        editor.putBoolean(KUBA_SKIN_SET_ALREADY, true);
        editor.commit();
    }
}
