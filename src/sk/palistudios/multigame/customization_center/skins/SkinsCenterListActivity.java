package sk.palistudios.multigame.customization_center.skins;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;
import sk.palistudios.multigame.BaseListActivity;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.CustomizationCenterActivity;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.mainMenu.DebugSettings;
import sk.palistudios.multigame.tools.SoundEffectsCenter;
import sk.palistudios.multigame.tools.Toaster;

import java.util.ArrayList;

/**
 * @author Pali
 */
public class SkinsCenterListActivity extends BaseListActivity {

    private SkinsArrayAdapter skinAdapter;

    public static SkinItem getCurrentSkin(Context context) {
        String currentSkinComputerName = GameSharedPref.getChosenSkin();
        String humanName = null;

        int color1 = 0;
        int color2 = 0;
        int color3 = 0;
        int color4 = 0;
        int color5 = 0;
        int colorAlt = 0;
        int colorHeader = 0;
        int colorChosen = 0;
        int logoID = 0;

        if (currentSkinComputerName.compareTo("summer") == 0) {
            color1 = context.getResources().getColor(R.color.summerMenu1);
            color2 = context.getResources().getColor(R.color.summerMenu2);
            color3 = context.getResources().getColor(R.color.summerMenu3);
            color4 = context.getResources().getColor(R.color.summerMenu4);
            color5 = context.getResources().getColor(R.color.summerMenu4);
//            colorAlt = context.getResources().getColor(R.color.summerAlt);
            colorHeader = context.getResources().getColor(R.color.summerHeader);
            colorChosen = context.getResources().getColor(R.color.summerChosen);
            logoID = R.drawable.logo_summer;
            humanName = "Summer";
        }

        if (currentSkinComputerName.compareTo("kuba") == 0) {
            color1 = context.getResources().getColor(R.color.kuba_top_bar_bg);
            color2 = context.getResources().getColor(R.color.kuba_top_bar_label);
            color3 = context.getResources().getColor(R.color.kuba_top_bar_number);
            color4 = context.getResources().getColor(R.color.kuba_top_bar_separator);
            color5 = context.getResources().getColor(R.color.kuba_top_bar_separator_down);
//            colorMain = context.getResources().getColor(R.color.kubaMain);
//            colorAlt = context.getResources().getColor(R.color.kubaAlt);
            colorHeader = context.getResources().getColor(R.color.kubaHeader);
            colorChosen = context.getResources().getColor(R.color.kubaChosen);
            logoID = R.drawable.logo;
            humanName = "Default";
        }

        if (currentSkinComputerName.compareTo("girl_power") == 0) {
            color1 = context.getResources().getColor(R.color.pinkyMenu1);
            color2 = context.getResources().getColor(R.color.pinkyMenu2);
            color3 = context.getResources().getColor(R.color.pinkyMenu3);
            color4 = context.getResources().getColor(R.color.pinkyMenu4);
            color5 = context.getResources().getColor(R.color.pinkyMenu4);
//            colorMain = context.getResources().getColor(R.color.pinkyMain);
//            colorAlt = context.getResources().getColor(R.color.pinkyAlt);
            colorHeader = context.getResources().getColor(R.color.pinkyHeader);
            colorChosen = context.getResources().getColor(R.color.pinkyChosen);
            logoID = R.drawable.logo_pinky;
            humanName = "Girl Power";
        }

        if (currentSkinComputerName.compareTo("blue_sky") == 0) {
            color1 = context.getResources().getColor(R.color.blueSkyMenu1);
            color2 = context.getResources().getColor(R.color.blueSkyMenu2);
            color3 = context.getResources().getColor(R.color.blueSkyMenu3);
            color4 = context.getResources().getColor(R.color.blueSkyMenu4);
            color5 = context.getResources().getColor(R.color.blueSkyMenu4);
//            colorMain = context.getResources().getColor(R.color.blueSkyMain);
//            colorAlt = context.getResources().getColor(R.color.blueSkyAlt);
            colorHeader = context.getResources().getColor(R.color.blueSkyHeader);
            colorChosen = context.getResources().getColor(R.color.blueSkyChosen);
            logoID = R.drawable.logo_blue_sky;
            humanName = "Blue Sky";
        }

        return new SkinItem(currentSkinComputerName, humanName, color1, color2, color3, color4, color5, colorHeader, colorChosen, logoID);
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

        initAdapter();


        final TextView header = new TextView(this);
        CustomizationCenterActivity.addHeader(header);
        header.setText((String) getResources().getString(R.string.cc_skins_skin_center_name));
        header.setTextSize(35);
        header.setBackgroundColor(SkinsCenterListActivity.getCurrentSkin(this).getColorHeader());
        header.setGravity(Gravity.CENTER);

        getListView().addHeaderView(header);

        TextView footer = new TextView(this);
        footer.setTextSize(60);
        footer.setText(" ");
        getListView().addFooterView(footer, null, false);

        setListAdapter(skinAdapter);
        CustomizationCenterActivity.addAdapter(skinAdapter);

//        CustomizationCenter.setSkinArrayAdapter(skinAdapter);

        final Activity act = this;

        getListView().setClickable(true);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //click into list
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    return;
                }

                if (skinAdapter.getItem(position - 1).isLocked()) {
                    Toaster.toastLong(skinAdapter.getItem(position - 1).getLockedDescription(), getParent());
                    return;
                }

                switch (position) {
                    case 1:
                        GameSharedPref.setSkinChosen("kuba");
                        break;
                    case 2:
                        GameSharedPref.setSkinChosen("summer");
                        break;
                    case 3:
                        GameSharedPref.setSkinChosen("girl_power");
                        break;

                    case 4:
                        GameSharedPref.setSkinChosen("blue_sky");
                        break;

                }
                header.setBackgroundColor(getCurrentSkin(act).colorHeader);
                notifyOtherTabs();
                skinAdapter.activateItem(position);
                skinAdapter.notifyDataSetChanged();
            }
        });


    }

    public void initAdapter() {

        ArrayList<SkinItem> items = new ArrayList<SkinItem>();

//        items.add(new SkinItem("Summer", 0, 0, 0, 0, 0, 0, 0, 0));
//        items.add(new SkinItem("Pastel", 0, 0, 0, 0, 0, 0, 0, 0));
//        items.add(new SkinItem("GirlPower", 0, 0, 0, 0, 0, 0, 0, 0));
        items.add(new SkinItem("kuba", "Kuba"));
        items.add(new SkinItem("summer", "Summer", (String) getResources().getString(R.string.cc_achievements_champion_description) + (String) getResources().getString(R.string.cc_achievements_requirement_ending)));
//        items.add(new SkinItem("pastel","Pastel","You must dance in order to unlock this skin"));
        items.add(new SkinItem("girl_power", "Girl Power", (String) getResources().getString(R.string.cc_achievements_magin_ten_description) + (String) getResources().getString(R.string.cc_achievements_requirement_ending)));
        items.add(new SkinItem("blue_sky", "Blue Sky", (String) getResources().getString(R.string.cc_achievements_competitive_description) + (String) getResources().getString(R.string.cc_achievements_requirement_ending)));

        skinAdapter = new SkinsArrayAdapter(this, items, SkinsCenterListActivity.getCurrentSkin(this));
    }

    private void notifyOtherTabs() {
        CustomizationCenterActivity.changeSkinForAllTabs(getCurrentSkin(this));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SoundEffectsCenter.playBackSound(this);
    }

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
}
