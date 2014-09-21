package sk.palistudios.multigame.mainMenu;

// @author Pali

import android.content.Context;
import android.os.AsyncTask;

import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.hall_of_fame.HallOfFameActivity;
import sk.palistudios.multigame.hall_of_fame.HofDatabaseCenter;
import sk.palistudios.multigame.tools.sound.SoundEffectsCenter;

public class ApplicationInitializer {
  public static void initApplication(Context context) {

        /* Init db in async task asap. */
    if (GameSharedPref.getDbInitialized() != true) {
      initDatabase(context);
    }

        /* Fallback for old devices with other theme than Kuba. */
    if (GameSharedPref.getKubaSkinSetAlready()) {
      GameSharedPref.unlockItem("kuba");
      GameSharedPref.lockItem("summer");
      GameSharedPref.setSkinChosen("kuba");
      GameSharedPref.setKubaSkinSetAlready();
    }

    SoundEffectsCenter.init(context);
    GameSharedPref.increaseTimesMultigameRun();

    boolean firstTime = GameSharedPref.isAppRunningForFirstTime();
    if (firstTime) {
      initCustomizationItems();
      initActiveMinigames();
      initAllMinigames();

      GameSharedPref.setAppRunningForFirstTime(false);
    }

        /* One time run for update/fresh installs. */
    boolean shouldRunUpdateCode = GameSharedPref.isUpdateOrFreshInstall(context);
    if (shouldRunUpdateCode) {
      GameSharedPref.unlockItem("kuba");
    }
    GameSharedPref.setLastSeenVersion(context);

    if (DebugSettings.debugFirstRun) {
      GameSharedPref.clear();
      clearDatabase(context);

      GameSharedPref.setGameSaved(false);
      initActiveMinigames();
      initAllMinigames();

      initMusicLoops();
      initSkins();
      initCustomizationItems();

      initDatabase(context);

      GameSharedPref.setAppRunningForFirstTime(false);

    }

    if (DebugSettings.unlockAllItems) {
      unlockItemsAll();
    }

    if (DebugSettings.tutorialCompleted) {
      GameSharedPref.onTutorialCompleted();
    }

    if (DebugSettings.debugInit) {
      DebugSettings.debugInit(context);
    }
  }

  private static void initActiveMinigames() {
    String[] tmpChosenMinigames = new String[4];

    tmpChosenMinigames[0] = "VBird";
    tmpChosenMinigames[1] = "HBalance";
    tmpChosenMinigames[2] = "TCatcher";
    tmpChosenMinigames[3] = "TGatherer";
    GameSharedPref.SetChosenMinigamesNames(tmpChosenMinigames);
  }

  private static void initAllMinigames() {
    String[] allMinigames = new String[6];

    allMinigames[0] = "HBalance";
    allMinigames[1] = "TCatcher";
    allMinigames[2] = "TGatherer";
    allMinigames[3] = "TInvader";
    allMinigames[4] = "VBird";
    allMinigames[5] = "VBouncer";

    GameSharedPref.initializeAllMinigamesInfo(allMinigames);
  }

  private static void initDatabase(final Context context) {
    new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground(Void... params) {
        HofDatabaseCenter hofDb = new HofDatabaseCenter(context);
        hofDb.fillDbFirstTime();
        GameSharedPref.setDbInitialized(true);
        return null;
      }

      @Override
      protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (HallOfFameActivity.mRingProgressDialog != null) {
          HallOfFameActivity.mRingProgressDialog.dismiss();
          HallOfFameActivity.mRingProgressDialog = null;
        }
      }
    }.execute();

  }

  private static void initMusicLoops() {
    GameSharedPref.setMusicLoopChosen("dst_blam");
  }

  private static void initSkins() {
    GameSharedPref.setSkinChosen("kuba");
  }

  private static void initCustomizationItems() {
    GameSharedPref.unlockInitialItems();
  }

  private static void unlockItemsAll() {
    GameSharedPref.unlockItem("HBalance");
    GameSharedPref.unlockItem("VBird");
    GameSharedPref.unlockItem("VBouncer");
    GameSharedPref.unlockItem("TGatherer");
    GameSharedPref.unlockItem("TCatcher");
    GameSharedPref.unlockItem("TInvader");
    GameSharedPref.unlockItem("summer");
    GameSharedPref.unlockItem("girl_power");
    GameSharedPref.unlockItem("blue_sky");
    GameSharedPref.unlockItem("dst_cyberops");
    GameSharedPref.unlockItem("dst_blam");
    GameSharedPref.unlockItem("dst_cv_x");
  }

  private static void clearDatabase(Context context) {
    HofDatabaseCenter hofDb = new HofDatabaseCenter(context);
    hofDb.open();
    hofDb.deleteAll();
    hofDb.close();
  }
}
