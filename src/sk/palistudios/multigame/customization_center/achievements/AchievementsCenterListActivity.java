package sk.palistudios.multigame.customization_center.achievements;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
import sk.palistudios.multigame.BaseListActivity;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.CustomizationCenterActivity;
import sk.palistudios.multigame.customization_center.skins.SkinsCenterListActivity;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.mainMenu.DebugSettings;
import sk.palistudios.multigame.tools.SoundEffectsCenter;

import java.util.ArrayList;

/**
 * @author Pali
 */
public class AchievementsCenterListActivity extends BaseListActivity {

    public static ArrayList<AchievementItem> achievements = new ArrayList<AchievementItem>();
    private static AchievementsArrayAdapter achievementsAdapter;

    public static void checkAchievements(int score, int level, Activity act) {

        initAchievements(act);

        for (AchievementItem ach : achievements) {
            ach.checkAchievementFullfiled(score, level, act);
        }

    }

    public static void initAchievements(Context context) {

        if (achievements.isEmpty()) {
            achievements.add(new AchievementItem("good_start", (String) context.getResources().getString(R.string.cc_achievements_good_start_name), (String) context.getResources().getString(R.string.cc_achievements_good_start_description),
                    "SCORE", 5000, "VBouncer", (String) context.getResources().getString(R.string.cc_minigames_bouncer_name),
                    GameSharedPref.isAchievementFulfilled("good_start"), (String) context.getResources().getString(R.string.cc_minigames_minigame_name)));

            achievements.add(new AchievementItem("lucky_seven", (String) context.getResources().getString(R.string.cc_achievements_lucky_seven_name), (String) context.getResources().getString(R.string.cc_achievements_lucky_seven_description),
                    "LEVEL", 7, "dst_cyberops", (String) context.getResources().getString(R.string.cc_music_blam_name),
                    GameSharedPref.isAchievementFulfilled("lucky_seven"), (String) context.getResources().getString(R.string.cc_music_music_name)));

            achievements.add(new AchievementItem("magic_ten", (String) context.getResources().getString(R.string.cc_achievements_magin_ten_name), (String) context.getResources().getString(R.string.cc_achievements_magin_ten_description),
                    "LEVEL", 10, "girl_power", (String) context.getResources().getString(R.string.cc_skins_girl_power_name),
                    GameSharedPref.isAchievementFulfilled("magic_ten"), (String) context.getResources().getString(R.string.cc_skins_skin_name)));

            achievements.add(new AchievementItem("addict", (String) context.getResources().getString(R.string.cc_achievements_addict_name), (String) context.getResources().getString(R.string.cc_achievements_addicts_description),
                    "GAMES", 10, "TInvader", (String) context.getResources().getString(R.string.cc_minigames_invader_name),
                    GameSharedPref.isAchievementFulfilled("addict"), (String) context.getResources().getString(R.string.cc_minigames_minigame_name)));

            achievements.add(new AchievementItem("supporter", (String) context.getResources().getString(R.string.cc_achievements_supporter_name), (String) context.getResources().getString(R.string.cc_achievements_supporter_description),
                    "RATE", -1, "dst_cv_x", (String) context.getResources().getString(R.string.cc_music_cv_x_name),
                    GameSharedPref.isAchievementFulfilled("supporter"), (String) context.getResources().getString(R.string.cc_music_music_name)));

            achievements.add(new AchievementItem("competitive", (String) context.getResources().getString(R.string.cc_achievements_competitive_name), (String) context.getResources().getString(R.string.cc_achievements_competitive_description),
                    "SHARE", -1, "blue_sky", (String) context.getResources().getString(R.string.cc_skins_blue_sky_name),
                    GameSharedPref.isAchievementFulfilled("competitive"), (String) context.getResources().getString(R.string.cc_skins_skin_name)));

            achievements.add(new AchievementItem("champion", (String) context.getResources().getString(R.string.cc_achievements_champion_name), (String) context.getResources().getString(R.string.cc_achievements_champion_description),
                    "SCORE", 10001, "summer", (String) context.getResources().getString(R.string.cc_skins_summer_name),
                    GameSharedPref.isAchievementFulfilled("champion"), (String) context.getResources().getString(R.string.cc_skins_skin_name)));
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if (!GameSharedPref.isAchievementFulfilled("pro") && DebugSettings.adsActivated) {
            setContentView(R.layout.list_layout);
        } else {
            setContentView(R.layout.list_layout_adfree);
        }

        initAchievements(this);

        initAdapter();

        TextView header = new TextView(this);
        header.setText(getResources().getString(R.string.cc_achievements_achievements_center_name));
        header.setTextSize(35);
        header.setBackgroundColor(SkinsCenterListActivity.getCurrentSkin(this).getColorHeader());
        header.setGravity(Gravity.CENTER);

        getListView().addHeaderView(header);

        TextView footer = new TextView(this);
        footer.setTextSize(60);
        footer.setText(" ");
        getListView().addFooterView(footer, null, false);


        setListAdapter(achievementsAdapter);
        CustomizationCenterActivity.addAdapter(achievementsAdapter);

        getListView().setClickable(true);

    }

    public void initAdapter() {

        ArrayList<AchievementItem> items = new ArrayList<AchievementItem>(achievements);
        achievementsAdapter = new AchievementsArrayAdapter(this, items, SkinsCenterListActivity.getCurrentSkin(this));
    }

    public AchievementsArrayAdapter getAchievementsArrayAdapter() {
        return achievementsAdapter;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SoundEffectsCenter.playBackSound(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
