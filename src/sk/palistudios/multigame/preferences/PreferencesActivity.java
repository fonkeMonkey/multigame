package sk.palistudios.multigame.preferences;

import sk.palistudios.multigame.tools.SoundEffectsCenter;
import android.app.Activity;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;
//import com.appflood.AppFlood;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Log;
import java.util.List;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.skins.SkinsCenterListActivity;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.mainMenu.GlobalSettings;
import sk.palistudios.multigame.mainMenu.MainMenuActivity;
import sk.palistudios.multigame.tools.Toaster;

/**
 *
 * @author Pali
 */
public class PreferencesActivity extends PreferenceActivity {

    private static SharedPreferences.Editor editor;
    private static SharedPreferences prefs;
    private static PreferencesActivity singleton = null;

    public static PreferencesActivity getInstance() {
        if (singleton == null) {
            return new PreferencesActivity();
        } else {
            return singleton;
        }

    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
//        getWindow().setFlags(WindoswManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        if (!GameSharedPref.isAchievementFulfilled("pro") && GlobalSettings.adsActivated) {
            setContentView(R.layout.list_layout);
//        } else {
//            setContentView(R.layout.list_layout_adfree);
//        }

        TextView footer = new TextView(this);
        footer.setTextSize(60);
        footer.setText(" ");
        getListView().addFooterView(footer, null, false);

        singleton = this;

        boolean isTutorialCompleted = GameSharedPref.isTutorialCompleted() || GlobalSettings.tutorialCompleted;

        if (isTutorialCompleted) {
            addPreferencesFromResource(R.xml.preferences);

        } else { //diferent layout withou possibility of changing game mode
            addPreferencesFromResource(R.xml.preferences_tutorial);
        }


        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();

        final Activity act = this;

        TextView header = new TextView(this);
        header.setText(getString(R.string.pref_title));
        header.setTextSize(35);
        header.setBackgroundColor(SkinsCenterListActivity.getCurrentSkin(this).getColorHeader());
        header.setGravity(Gravity.CENTER);
        getListView().addHeaderView(header);

        Preference myPref = (Preference) findPreference("MoreGamesPreference");
        if (!GameSharedPref.isAchievementFulfilled("pro") && GlobalSettings.adsActivated) {
            myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
//                    AppFlood.showPanel(act, AppFlood.PANEL_TOP);
                    return false;
                    //open browser or intent here
                }
            });
        } else {
//            myPref.setShouldDisableView(true);
            getPreferenceScreen().removePreference(myPref);
//        myPref.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SoundEffectsCenter.muteSystemSounds(this, true);
//        boolean music_on = GameSharedPref.isMusicOn();
//        boolean sound_on = GameSharedPref.isSoundOn();
        String game_mode = GameSharedPref.getGameMode();

        boolean isTutorialCompleted = GameSharedPref.isTutorialCompleted() || GlobalSettings.tutorialCompleted;


        if (isTutorialCompleted) {
            ListPreference preferenceGameMode = (ListPreference) findPreference("game_mode");
            if (game_mode.equals("Tutorial")) {
                preferenceGameMode.setValueIndex(0);
                preferenceGameMode.setSummary("Tutorial");
            } else {
                preferenceGameMode.setValueIndex(1);
                preferenceGameMode.setSummary("Classic");
            }
//            if (listPreference.getValue() == null) {
            // to ensure we don't get a null value
            // set first value by default
//            }
            preferenceGameMode.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.toString().equals("Tutorial") && GameSharedPref.isGameSaved()) {
                        Toaster.toastLong(getString(R.string.finish_saved_game), PreferencesActivity.this);
                        return true;
                    }
                    preference.setSummary(newValue.toString());
                    GameSharedPref.setGameMode(newValue.toString());
//                    editor.putString("game_mode", newValue.toString());
//                    editor.commit();
                    return true;
                }
            });
        }

        CheckBoxPreference preferenceMusic = (CheckBoxPreference) findPreference("music_on");
        preferenceMusic.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.toString().equals("true")) {
                    GameSharedPref.setMusicOn(true);
                } else {
                    GameSharedPref.setMusicOn(false);
                }
                return true;
            }
        });

        CheckBoxPreference preferenceSound = (CheckBoxPreference) findPreference("sound_on");
        preferenceSound.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.toString().equals("true")) {
                    GameSharedPref.setSoundOn(true);
                } else {
                    GameSharedPref.setSoundOn(false);
                }
                return true;
            }
        });

        CheckBoxPreference preferenceAutocalibration = (CheckBoxPreference) findPreference("autocalibration");
        preferenceAutocalibration.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.toString().equals("true")) {
                    GameSharedPref.setAutocalibration(true);
                } else {
                    GameSharedPref.setAutocalibration(false);
                }
                return true;
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//        saveSettings();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SoundEffectsCenter.playBackSound(this);
    }

    private void saveSettings() {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean music_on = prefs.getBoolean("music_on", true);
        boolean sound_on = prefs.getBoolean("sound_on", true);
        String game_mode = prefs.getString("game_mode", null);

        GameSharedPref.setMusicOn(music_on);
        GameSharedPref.setSoundOn(sound_on);
        GameSharedPref.setGameMode(game_mode);

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
