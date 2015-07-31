package sk.palistudios.multigame;

// @author Pali

import android.content.Context;
import android.os.AsyncTask;

import sk.palistudios.multigame.game.persistence.MGSettings;
import sk.palistudios.multigame.hall_of_fame.HallOfFameActivity;
import sk.palistudios.multigame.hall_of_fame.HofDatabaseCenter;
import sk.palistudios.multigame.mainMenu.DebugSettings;
import sk.palistudios.multigame.tools.sound.SoundEffectsCenter;

public class ApplicationInitializer {
  public static void initApplication(Context context) {
    MgTracker.init(context);

        /* Init db in async task asap. */
    if (MGSettings.getDbInitialized() != true) {
      initDatabase(context);
    }

        /* Fallback for old devices with other theme than Kuba. */
    if (MGSettings.getKubaSkinSetAlready()) {
      MGSettings.unlockItem("kuba");
      MGSettings.lockItem("summer");
      MGSettings.setSkinChosen("kuba");
      MGSettings.setKubaSkinSetAlready();
    }

    SoundEffectsCenter.init(context);
    MGSettings.increaseTimesMultigameRun();

    boolean firstTime = MGSettings.isAppRunningForFirstTime();
    if (firstTime) {
      initCustomizationItems();
      initActiveMinigames();
      initAllMinigames();

      MGSettings.setAppRunningForFirstTime(false);
    }

        /* One time run for update/fresh installs. */
    boolean shouldRunUpdateCode = MGSettings.isUpdateOrFreshInstall(context);
    if (shouldRunUpdateCode) {
      MGSettings.unlockItem("kuba");
    }
    MGSettings.setLastSeenVersion(context);

    if (DebugSettings.debugFirstRun) {
      MGSettings.clear();
      clearDatabase(context);

      MGSettings.setGameSaved(false);
      initActiveMinigames();
      initAllMinigames();

      initMusicLoops();
      initSkins();
      initCustomizationItems();

      initDatabase(context);

      MGSettings.setAppRunningForFirstTime(false);

    }

    if (DebugSettings.unlockAllItems) {
      MGSettings.unlockItemsAll();
    }

    if (DebugSettings.tutorialCompleted) {
      MGSettings.onTutorialCompleted();
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
    MGSettings.SetChosenMinigamesNames(tmpChosenMinigames);
  }

  private static void initAllMinigames() {
    String[] allMinigames = new String[6];

    allMinigames[0] = "HBalance";
    allMinigames[1] = "TCatcher";
    allMinigames[2] = "TGatherer";
    allMinigames[3] = "TInvader";
    allMinigames[4] = "VBird";
    allMinigames[5] = "VBouncer";

    MGSettings.initializeAllMinigamesInfo(allMinigames);
  }

  private static void initDatabase(final Context context) {
    new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground(Void... params) {
        HofDatabaseCenter hofDb = new HofDatabaseCenter(context);
        hofDb.fillDbFirstTime();
        MGSettings.setDbInitialized(true);
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
    MGSettings.setMusicLoopChosen("dst_blam");
  }

  private static void initSkins() {
    MGSettings.setSkinChosen("kuba");
  }

  private static void initCustomizationItems() {
    MGSettings.unlockInitialItems();
  }

  private static void clearDatabase(Context context) {
    HofDatabaseCenter hofDb = new HofDatabaseCenter(context);
    hofDb.open();
    hofDb.deleteAll();
    hofDb.close();
  }
}
