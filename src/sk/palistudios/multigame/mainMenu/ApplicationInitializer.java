package sk.palistudios.multigame.mainMenu;

// @author Pali
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Debug;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.hall_of_fame.HofDatabaseCenter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sk.palistudios.multigame.hall_of_fame.HallOfFameActivity;
import sk.palistudios.multigame.tools.SoundEffectsCenter;

public class ApplicationInitializer {

    private static Context mContext;
    private static boolean mHaveSetKubaSkin;

    public static void initApplication(Context context) {
//        Debug.startMethodTracing("trace");
        mContext = context;

        /* Init db in async task asap. */
        if (GameSharedPref.getDbInitialized() != true) {
            initDatabase(context);
        }

        if (GlobalSettings.adsActivated) {
//            AppFlood.initialize(act, "hAJJS1O9Vi8OCkso", "gUz8uarc1d0dL5242e44c", AppFlood.AD_ALL);
//            cacheAdmob(act);
        }



        SoundEffectsCenter.init(context);

        if (GlobalSettings.logFacebookHash) {

            PackageInfo info;
            try {
                info = mContext.getPackageManager().getPackageInfo("sk.palistudios.multigame", PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    MessageDigest md;
                    md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
//                String something = new String());
                    //String something = new String(Base64.encodeBytes(md.digest()));
//                Log.e("hash key", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
            } catch (NameNotFoundException e1) {
//            Log.e("name not found", e1.toString());
            } catch (NoSuchAlgorithmException e) {
//            Log.e("no such an algorithm", e.toString());
            } catch (Exception e) {
//            Log.e("exception", e.toString());
            }
        }

//        context = act.getApplicationContext();
//        ApplicationInitializer.sActivity = act;
//        act.setVolumeControlStream(AudioManager.STREAM_MUSIC);


        //Ad
//        boolean hasAdBeenShownAlready = GameSharedPref.hasAdBeenShownAlready();
        GameSharedPref.increaseTimesMultigameRun();
//        if (GameSharedPref.getTimesMultigameRun() >= 3 && InternetChecker.isNetworkAvailable(act)
//                && !GameSharedPref.isPremium() && !GameSharedPref.isAchievementFulfilled("pro")
//                && !hasAdBeenShownAlready && GlobalSettings.adsActivated) {
////            AppFlood.showFullScreen(act);
//            GameSharedPref.setAdShownAlready(true);
//        }



        initCustomizationItems();

        boolean firstTime = GameSharedPref.isGameRunningForFirstTime();
        if (firstTime) {
//            GameSharedPref.clear();
//            clearDatabase(act);

//            GameSharedPref.setGameSaved(false);
            initActiveMinigames();
            initAllMinigames();

            initAchievements(mContext);


            GameSharedPref.setGameRunningForFirstTime(false);
//            runOnce = true;
        }

        if (GlobalSettings.debugFirstRun) {
            GameSharedPref.clear();
            clearDatabase(mContext);

            GameSharedPref.setGameSaved(false);
            initActiveMinigames();
            initAllMinigames();

            initMusicLoops();
            initSkins();
            initAchievements(mContext);
            initCustomizationItems();

            initDatabase(mContext);

            GameSharedPref.setGameRunningForFirstTime(false);

        }

        if (GlobalSettings.unlockAllItems) {
            unlockItemsAll();
        }

        if (GlobalSettings.tutorialCompleted) {
            GameSharedPref.onTutorialCompleted();
//            GameSharedPref.setGameMode("Classic");
        }
//        GameFacebookShare.logout(context);
//        Debug.stopMethodTracing();
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
//        GameSharedPref.unlockItem("HBalance");
//        GameSharedPref.unlockItem("VBird");
//        GameSharedPref.unlockItem("TGatherer");
//        GameSharedPref.unlockItem("TCatcher");
//        GameSharedPref.unlockItem("summer");
//        GameSharedPref.unlockItem("dst_blam");
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
//        GameSharedPref.unlockItem("abdessamie_beat");
//        GameSharedPref.unlockItem("jungle");
        GameSharedPref.unlockItem("dst_cyberops");
        GameSharedPref.unlockItem("dst_blam");
        GameSharedPref.unlockItem("dst_cv_x");
    }

    private static void initAchievements(Context context) {
//        AchievementsCenter.initAchievements(context);
    }

    private static void clearDatabase(Context context) {
        HofDatabaseCenter hofDb = new HofDatabaseCenter(context);
        hofDb.open();
        hofDb.deleteAll();
        hofDb.close();
    }

    private static void cacheAdmob(Activity act) {
//        AdView adView = new AdView(act, AdSize.BANNER,
//                "ca-app-pub-5314490326517173/8380820043");
//        adView.setFocusable(false);
//        AdRequest adRequest = new AdRequest();
//        adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
//        adRequest.addTestDevice("0123456789ABCDEF");
//        adView.loadAd(adRequest);
    }

    private static void setThingsBackwardsCompatible() {
//        try {
//            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
//            int version = pInfo.versionCode;
//            if(mHaveSetKubaSkin == false && version == 10 ){
//                GameSharedPref.get
//            }
//        
//        } catch (NameNotFoundException ex) {
//            Logger.getLogger(ApplicationInitializer.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    }
}
