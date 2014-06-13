package sk.palistudios.multigame.customization_center;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.achievements.AchievementsCenterListActivity;
import sk.palistudios.multigame.customization_center.mgc.MinigamesCenterListActivity;
import sk.palistudios.multigame.customization_center.moreGames.MoreGamesCenterActivity;
import sk.palistudios.multigame.customization_center.music.MusicCenterListActivity;
import sk.palistudios.multigame.customization_center.skins.SkinItem;
import sk.palistudios.multigame.customization_center.skins.SkinsCenterListActivity;
import sk.palistudios.multigame.mainMenu.DebugSettings;
import sk.palistudios.multigame.tools.SoundEffectsCenter;

import java.util.ArrayList;

//import com.appflood.AppFlood;

public class CustomizationCenterActivity extends TabActivity {
    // TabSpec Names

    private static ArrayList<TextView> headers = new ArrayList<TextView>();
    private static ArrayList<IAdapter> adapters = new ArrayList<IAdapter>();
    private String MINIGAMES_SPEC;
    private String SKINS_SPEC;
    private String MUSIC_SPEC;
    private String ACHIEVEMENTS_SPEC;
    private String MORE_GAMES_SPEC;

    public static void changeSkinForAllTabs(SkinItem currentSkin) {
        //zmen header
        for (TextView header : headers) {
            header.setBackgroundColor(currentSkin.getColorHeader());
        }

        for (IAdapter adapter : adapters) {
            adapter.setColorChosen(currentSkin.getColorChosen());
            adapter.notifyDataSetChanged();
        }

    }

    //    public static void setMusicArrayAdapter(MusicArrayAdapter musicArrayAdapter) {
//        CustomizationCenter.musicArrayAdapter = musicArrayAdapter;
//    }
//
//    public static void setSkinArrayAdapter(SkinsArrayAdapter skinArrayAdapter) {
//        CustomizationCenter.skinArrayAdapter = skinArrayAdapter;
//    }
//
//    public static void setMgcArrayAdapter(MgcArrayAdapter mgcArrayAdapter) {
//        CustomizationCenter.mgcArrayAdapter = mgcArrayAdapter;
//    }
//
//    public static void setAchievementsArrayAdapter(AchievementsArrayAdapter achievementsArrayAdapter) {
//        CustomizationCenter.achievementsArrayAdapter = achievementsArrayAdapter;
//    }
    public static void addAdapter(IAdapter adapter) {
        adapters.add(adapter);
    }

    public static void addHeader(TextView header) {
        headers.add(header);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        MINIGAMES_SPEC = (String) getResources().getString(R.string.cc_minigames_tab_name);
        SKINS_SPEC = (String) getResources().getString(R.string.cc_skins_tab_name);
        ACHIEVEMENTS_SPEC = (String) getResources().getString(R.string.cc_achievements_tab_name);
        MUSIC_SPEC = (String) getResources().getString(R.string.cc_music_tab_name);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.customization_center);


        TabHost tabHost = getTabHost();

        TabSpec minigamesTab = tabHost.newTabSpec(MINIGAMES_SPEC);
//        gamesTab.setIndicator(MINIGAMES_SPEC, getResources().getDrawable(R.drawable.icon_inbox));
        minigamesTab.setIndicator(MINIGAMES_SPEC);
        Intent minigamesIntent = new Intent(this, MinigamesCenterListActivity.class);
        minigamesTab.setContent(minigamesIntent);

        TabSpec skinsTab = tabHost.newTabSpec(SKINS_SPEC);
        skinsTab.setIndicator(SKINS_SPEC);
        Intent skinsIntent = new Intent(this, SkinsCenterListActivity.class);
        skinsTab.setContent(skinsIntent);

        // Profile Tab
        TabSpec MusicTab = tabHost.newTabSpec(MUSIC_SPEC);
//        SoundsTab.setIndicator(SOUNDS_SPEC, getResources().getDrawable(R.drawable.icon_profile));
        MusicTab.setIndicator(MUSIC_SPEC);
        Intent musicIntent = new Intent(this, MusicCenterListActivity.class);
        MusicTab.setContent(musicIntent);

        TabSpec achievementsTab = tabHost.newTabSpec(ACHIEVEMENTS_SPEC);
//        SoundsTab.setIndicator(SOUNDS_SPEC, getResources().getDrawable(R.drawable.icon_profile));
        achievementsTab.setIndicator(ACHIEVEMENTS_SPEC);
        Intent achievementsIntent = new Intent(this, AchievementsCenterListActivity.class);
        achievementsTab.setContent(achievementsIntent);

        if (DebugSettings.adsActivated) {
            MORE_GAMES_SPEC = (String) getResources().getString(R.string.cc_more_games_tab_name);
            TabSpec moreGamesTab = tabHost.newTabSpec(MORE_GAMES_SPEC);
            //        SoundsTab.setIndicator(SOUNDS_SPEC, getResources().getDrawable(R.drawable.icon_profile));
            moreGamesTab.setIndicator(MORE_GAMES_SPEC);
            Intent moreGamesIntent = new Intent(this, MoreGamesCenterActivity.class);
            moreGamesTab.setContent(moreGamesIntent);
            tabHost.addTab(moreGamesTab); // Adding Profile tab

        }

        // Adding all TabSpec to TabHost
        tabHost.addTab(minigamesTab); // Adding Inbox tab
        tabHost.addTab(skinsTab); // Adding Outbox tab
        tabHost.addTab(MusicTab); // Adding Profile tab
        tabHost.addTab(achievementsTab); // Adding Profile tab

        tabHost.setCurrentTab(0);


        final Activity act = this;

        for (int i = 0; i < 4; i++) {
            final int rank = i;
            getTabWidget().getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    getTabHost().setCurrentTab(rank);
                    SoundEffectsCenter.playTabSound(CustomizationCenterActivity.this);
                }
            });

        }

        if (DebugSettings.adsActivated) {
            getTabWidget().getChildAt(4).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getTabHost().getCurrentTabTag().equals(MORE_GAMES_SPEC)) {
                        getTabHost().setCurrentTab(4);
                    }
//                AppFlood.showList(act, AppFlood.LIST_GAME);
                    SoundEffectsCenter.playTabSound(CustomizationCenterActivity.this);
//                AppFlood.showPanel(act, AppFlood.PANEL_PORTRAIT);
                }
            });
        }
        getTabWidget().setBackgroundColor(Color.BLACK);
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1) {
            getTabWidget().setStripEnabled(false);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        SoundEffectsCenter.muteSystemSounds(this, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        headers.clear();
        adapters.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SoundEffectsCenter.playBackSound(this);
    }

    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        SoundEffectsCenter.muteSystemSounds(this, false);
        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }
}