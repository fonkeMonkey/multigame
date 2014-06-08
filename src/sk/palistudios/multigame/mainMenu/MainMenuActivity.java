package sk.palistudios.multigame.mainMenu;

// @author Pali
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
//import com.appflood.AppFlood;
import com.facebook.Session;
import com.google.analytics.tracking.android.EasyTracker;
import sk.palistudios.multigame.BaseActivity;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.skins.SkinItem;
import sk.palistudios.multigame.customization_center.skins.SkinsCenterListActivity;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.tools.SoundEffectsCenter;
import sk.palistudios.multigame.game.*;
import sk.palistudios.multigame.tools.Toaster;
import android.graphics.PorterDuff.Mode;

public class MainMenuActivity extends BaseActivity {

    private static boolean mShowHighScoreStatus;
    private static int mShowHighScoreScore;
    private static MainMenuActivity mainMenuFacebook;
    private Button buttonStart;
    private Button buttonCc;
    private Button buttonHof;
    private Button buttonPreferences;
//    private Button buttonAbout;
//    private Button buttonPreferences;
    private ImageView logo;
    private static MainMenuActivity sMainMenuInstance;
    public static boolean isThereADialogToShow = false;

    public static MainMenuActivity getInstance() {
        return sMainMenuInstance;
    }
    private static boolean mFacebookShared = false;
//    public static boolean volumeSet = false;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
//        Debug.startMethodTracing("mg_1st");
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.main_menu);
//        if (!volumeSet) {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
//            SoundEffectsCenter.setVolumeBasedOnRingVolume(this);
//            volumeSet = true;
//        }

        sMainMenuInstance = this;


        GameSharedPref.initSharedPref(this);
        ApplicationInitializer.initApplication(this);

        logo = (ImageView) findViewById(R.id.logo);

        buttonStart = (Button) findViewById(R.id.mainMenu_button_start);
        buttonCc = (Button) findViewById(R.id.mainMenu_button_MGC);
        buttonHof = (Button) findViewById(R.id.mainMenu_button_HOF);
        buttonPreferences = (Button) findViewById(R.id.mainMenu_button_preferences);

//        GameFacebookShare.shareScoreToFacebook(this);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        if (!volumeSet) {
//            SoundEffectsCenter.setVolumeBasedOnRingVolume(this);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setStartGameButtonName();


        SkinItem currentSkin = SkinsCenterListActivity.getCurrentSkin(this);

//               color1 = getResources().getColor(R.color.kubaMenu1);
//            color2 = getResources().getColor(R.color.kubaMenu2);
//            color3 = getResources().getColor(R.color.kubaMenu3);
//            color4 = getResources().getColor(R.color.kubaMenu4);
        logo.setImageResource(SkinsCenterListActivity.getCurrentSkin(this).getLogoID());
//        logo.setImageResource(R.drawable.logo);
//        buttonStart.setBackgroundColor(currentSkin.getColor1());
//        buttonCc.setBackgroundColor(currentSkin.getColor2());
//        buttonPreferences.setBackgroundColor(currentSkin.getColor3());
//        buttonHof.setBackgroundColor(currentSkin.getColor4());

//        buttonStart.setBackground(getResources().getDrawable(R.drawable.button_background_ul));
//        buttonCc.setBackground(getResources().getDrawable(R.drawable.button_background_ur));
//        buttonPreferences.setBackground(getResources().getDrawable(R.drawable.button_background_ll));
//        buttonHof.setBackground(getResources().getDrawable(R.drawable.button_background_lr));
//        buttonAbout.setBackgroundColor(currentSkin.getColor4());

        /* TODO zasa hacky lebo sa mi to nechce redezignova*/
        if (GameSharedPref.isSkinChosen("kuba")) {
            buttonStart.getBackground().setColorFilter(currentSkin.getColor1(), Mode.SRC);
            buttonCc.getBackground().setColorFilter(currentSkin.getColor2(), Mode.SRC);
            buttonPreferences.getBackground().setColorFilter(currentSkin.getColor3(), Mode.SRC);
            buttonHof.getBackground().setColorFilter(currentSkin.getColor4(), Mode.SRC);

        } else if (GameSharedPref.isSkinChosen("summer"))
        {
            buttonStart.getBackground().setColorFilter(currentSkin.getColor1(), Mode.SRC);
            buttonCc.getBackground().setColorFilter(currentSkin.getColor4(), Mode.SRC);
            buttonPreferences.getBackground().setColorFilter(currentSkin.getColor2(), Mode.SRC);
            buttonHof.getBackground().setColorFilter(currentSkin.getColor3(), Mode.SRC);
        }
        else{
            buttonStart.getBackground().setColorFilter(currentSkin.getColor1(), Mode.SRC);
            buttonCc.getBackground().setColorFilter(currentSkin.getColor2(), Mode.SRC);
            buttonPreferences.getBackground().setColorFilter(currentSkin.getColor4(), Mode.SRC);
            buttonHof.getBackground().setColorFilter(currentSkin.getColor3(), Mode.SRC);
        }

        if (mShowHighScoreStatus) {
            GameUIWindows.showWinnerDialogAfterShareWindow(getInstance(), mShowHighScoreScore);
            mShowHighScoreStatus = false;
        }
        if (isThereADialogToShow) {
            //only works now for the ask to connect dialog
            GameUIWindows.askUserToConnect(this, GameActivity.dialogIsWinner, GameActivity.dialogScore);
            isThereADialogToShow = false;
        }

        if (mFacebookShared) {
            GameSharedPref.achievementFulfilled("competitive", "blue_sky");
            Toaster.toastLong(getResources().getString(R.string.game_achievement_fulfilled_1) + "Competitive" + getResources().getString(R.string.game_achievement_fulfilled_2) + "skin" + this.getResources().getString(R.string.game_achievement_fulfilled_3), this);
        }
        mShowHighScoreStatus = false;
        mFacebookShared = false;

        GameActivity.sTutorialRestart = false;
//        Debug.stopMethodTracing();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

    }

    public void startGame(View view) {
        SoundEffectsCenter.playForwardSound(MainMenuActivity.this);
//        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, sk.palistudios.multigame.game.GameActivity.class);

//        bundle.putParcelable("MainMenu", this);
//        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void showMGC(View view) {
        SoundEffectsCenter.playForwardSound(MainMenuActivity.this);
        Intent intent = new Intent(this, sk.palistudios.multigame.customization_center.CustomizationCenterActivity.class);
        startActivity(intent);
    }

    public void showHallOfFame(View view) {
        SoundEffectsCenter.playForwardSound(MainMenuActivity.this);
        Intent intent = new Intent(this, sk.palistudios.multigame.hall_of_fame.HallOfFameActivity.class);
        startActivity(intent);
    }

//    public void quitGame(View view) {
//        this.finish();
//    }
    public void showPreferences(View view) {
        SoundEffectsCenter.playForwardSound(MainMenuActivity.this);
        Intent intent = new Intent(this, sk.palistudios.multigame.preferences.PreferencesActivity.class);
        startActivity(intent);
    }

//    public void switchNoviceMode(View view) {
//        GameSharedPref.switchNoviceMode();
//        setStartGameButtonName();
//    }
//    public void openAC() {
//    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        onExit();
//        this.finish();
    }

    private void setStartGameButtonName() {
        if (GameSharedPref.isGameSaved()) {
            buttonStart.setText(getString(R.string.button_resume));
            return;
        }

        if (GameSharedPref.isTutorialModeActivated()) {
            if (GameActivity.sTutorialLastLevel == 0) {
                buttonStart.setText(getString(R.string.button_tutorial));

            } else {
                buttonStart.setText(getString(R.string.button_resume_tutorial));
            }
            return;
        }

        buttonStart.setText(getString(R.string.button_game));

    }

//    private void onExit() {
////        PremiumUpgrader.finish();
//        GameSharedPref.setAdShownAlready(false);
//    }
    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SoundEffectsCenter.releaseMediaPlayer();
        sMainMenuInstance = null;
////        if (GlobalSettings.adsActivated) {
//////            AppFlood.destroy();
////        }
//        Log.e("destroying main menu", "destroying main menu");
////        Session session = Session.getActiveSession();
////        if (session != null) {
////            session.closeAndClearTokenInformation();
//        }
//    }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession()
                .onActivityResult(getInstance(), requestCode, resultCode, data);
    }

    public static void setOfferHighScore(int score) {
        mShowHighScoreStatus = true;
        mShowHighScoreScore = score;
    }

    public static void setMainMenuFacebook(MainMenuActivity mainMenu) {
        mainMenuFacebook = mainMenu;
    }

    public static void setWallPostAchievementDone() {
        mFacebookShared = true;
    }
}
