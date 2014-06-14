package sk.palistudios.multigame.mainMenu;

// @author Pali

import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.facebook.Session;
import sk.palistudios.multigame.BaseActivity;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.skins.SkinItem;
import sk.palistudios.multigame.customization_center.skins.SkinsCenterListActivity;
import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.GameDialogs;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.tools.SoundEffectsCenter;
import sk.palistudios.multigame.tools.Toaster;

public class MainMenuActivity extends BaseActivity {

    public static boolean isThereADialogToShow = false;
    private static boolean mShowHighScoreStatus;
    private static int mShowHighScoreScore;
    private static MainMenuActivity mainMenuFacebook;
    private static MainMenuActivity sMainMenuInstance;
    private static boolean mFacebookShared = false;
    private Button buttonStart;
    private Button buttonCc;
    private Button buttonHof;
    private Button buttonPreferences;
    private ImageView logo;

    public static MainMenuActivity getInstance() {
        return sMainMenuInstance;
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

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.main_menu);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        sMainMenuInstance = this;

        GameSharedPref.initSharedPref(this);
        ApplicationInitializer.initApplication(this);

        logo = (ImageView) findViewById(R.id.logo);

        buttonStart = (Button) findViewById(R.id.mainMenu_button_start);
        buttonCc = (Button) findViewById(R.id.mainMenu_button_MGC);
        buttonHof = (Button) findViewById(R.id.mainMenu_button_HOF);
        buttonPreferences = (Button) findViewById(R.id.mainMenu_button_preferences);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
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
//        buttonStart.setBackgroundColor(currentSkin.getBarBgColor());
//        buttonCc.setBackgroundColor(currentSkin.getBarLabelColor());
//        buttonPreferences.setBackgroundColor(currentSkin.getBarTextColor());
//        buttonHof.setBackgroundColor(currentSkin.getBarSeparatorColor());

//        buttonStart.setBackground(getResources().getDrawable(R.drawable.button_background_ul));
//        buttonCc.setBackground(getResources().getDrawable(R.drawable.button_background_ur));
//        buttonPreferences.setBackground(getResources().getDrawable(R.drawable.button_background_ll));
//        buttonHof.setBackground(getResources().getDrawable(R.drawable.button_background_lr));
//        buttonAbout.setBackgroundColor(currentSkin.getBarSeparatorColor());
        setMainMenuColors();

        if (mShowHighScoreStatus) {
            GameDialogs.showWinnerDialogAfterShareWindow(getInstance(), mShowHighScoreScore);
            mShowHighScoreStatus = false;
        }
        if (isThereADialogToShow) {
            //only works now for the ask to connect dialog
            GameDialogs.askUserToConnect(this, GameActivity.dialogIsWinner, GameActivity.dialogScore);
            isThereADialogToShow = false;
        }

        if (mFacebookShared) {
            GameSharedPref.achievementFulfilled("competitive", "blue_sky");
            Toaster.toastLong(getResources().getString(R.string.game_achievement_fulfilled_1) + "Competitive" + getResources().getString(R.string.game_achievement_fulfilled_2) + "skin" + this.getResources().getString(R.string.game_achievement_fulfilled_3), this);
        }
        mShowHighScoreStatus = false;
        mFacebookShared = false;

        GameActivity.sTutorialRestart = false;
    }

    private void setMainMenuColors() {
    /* Zasa hacky lebo sa mi to nechce redezignovat. */
        if (GameSharedPref.isSkinChosen("kuba")) {
            buttonStart.getBackground().setColorFilter(getResources().getColor(R.color.kubaMenu1), Mode.SRC);
            buttonCc.getBackground().setColorFilter(getResources().getColor(R.color.kubaMenu2), Mode.SRC);
            buttonPreferences.getBackground().setColorFilter(getResources().getColor(R.color.kubaMenu3), Mode.SRC);
            buttonHof.getBackground().setColorFilter(getResources().getColor(R.color.kubaMenu4), Mode.SRC);
        } else if (GameSharedPref.isSkinChosen("summer")) {
            buttonStart.getBackground().setColorFilter(getResources().getColor(R.color.summerMenu1), Mode.SRC);
            buttonCc.getBackground().setColorFilter(getResources().getColor(R.color.summerMenu2), Mode.SRC);
            buttonPreferences.getBackground().setColorFilter(getResources().getColor(R.color.summerMenu3), Mode.SRC);
            buttonHof.getBackground().setColorFilter(getResources().getColor(R.color.summerMenu4), Mode.SRC);
        } else if (GameSharedPref.isSkinChosen("girl_power")){
            buttonStart.getBackground().setColorFilter(getResources().getColor(R.color.pinkyMenu1), Mode.SRC);
            buttonCc.getBackground().setColorFilter(getResources().getColor(R.color.pinkyMenu2), Mode.SRC);
            buttonPreferences.getBackground().setColorFilter(getResources().getColor(R.color.pinkyMenu3), Mode.SRC);
            buttonHof.getBackground().setColorFilter(getResources().getColor(R.color.pinkyMenu4), Mode.SRC);
        } else if (GameSharedPref.isSkinChosen("blue_sky")){
            buttonStart.getBackground().setColorFilter(getResources().getColor(R.color.blueSkyMenu1), Mode.SRC);
            buttonCc.getBackground().setColorFilter(getResources().getColor(R.color.blueSkyMenu2), Mode.SRC);
            buttonPreferences.getBackground().setColorFilter(getResources().getColor(R.color.blueSkyMenu3), Mode.SRC);
            buttonHof.getBackground().setColorFilter(getResources().getColor(R.color.blueSkyMenu4), Mode.SRC);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public void startGame(View view) {
        SoundEffectsCenter.playForwardSound(MainMenuActivity.this);
        Intent intent = new Intent(this, sk.palistudios.multigame.game.GameActivity.class);
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

    public void showPreferences(View view) {
        SoundEffectsCenter.playForwardSound(MainMenuActivity.this);
        Intent intent = new Intent(this, sk.palistudios.multigame.preferences.PreferencesActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SoundEffectsCenter.releaseMediaPlayer();
        sMainMenuInstance = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession()
                .onActivityResult(getInstance(), requestCode, resultCode, data);
    }
}
