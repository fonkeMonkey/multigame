package sk.palistudios.multigame.customization_center.mgc;

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
import sk.palistudios.multigame.customization_center.skins.SkinsCenterListActivity;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.tools.SoundEffectsCenter;
import sk.palistudios.multigame.tools.Toaster;

import java.util.ArrayList;

/**
 * @author Pali
 */
public class MinigamesCenterListActivity extends BaseListActivity {

    public static char SYMBOL_MINIGAME_HORIZONTAL = '⇆';
    public static char SYMBOL_MINIGAME_TOUCH = '✋';
    public static char SYMBOL_MINIGAME_VERTICAL = '⇅';
    private static String[] tmpChosenMinigames = new String[4];
    private MgcArrayAdapter minigamesAdapter;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
//        fillAllMinigames();

//        resolveActiveMinigamesNames();

        //create the view
//        fillAdapter();
        initAdapter();

//        if(!GameSharedPref.isAchievementFulfilled("pro") && DebugSettings.adsActivated){
        setContentView(R.layout.list_layout);
//        }else{
//            setContentView(R.layout.list_layout_adfree);
//        }

        tmpChosenMinigames = GameSharedPref.getChosenMinigamesNames();

        TextView header = new TextView(this);
        CustomizationCenterActivity.addHeader(header);
        header.setText((String) getResources().getString(R.string.cc_minigames_minigame_center_name));
        header.setTextSize(35);
        header.setBackgroundColor(SkinsCenterListActivity.getCurrentSkin(this).getColorHeader());
        header.setGravity(Gravity.CENTER);

        TextView footer = new TextView(this);
        footer.setTextSize(60);
        footer.setText(" ");
        getListView().addFooterView(footer, null, false);

//        ListView listView = (ListView) inflater.inflate(R.layout.store_list, null);


        getListView().addHeaderView(header);

        setListAdapter(minigamesAdapter);

        CustomizationCenterActivity.addAdapter(minigamesAdapter);

        getListView().setClickable(true);
        char type;
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //click into list
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    return;
                }

                if (minigamesAdapter.getItem(position - 1).isLocked()) {
                    Toaster.toastLong(minigamesAdapter.getItem(position - 1).getLockedDescription(), getParent());
                    return;
                }

                String minigameToChange;

                switch (position) {
                    case 1:
                        tmpChosenMinigames[1] = "HBalance";
                        minigamesAdapter.activateItem(SYMBOL_MINIGAME_HORIZONTAL, position);
                        break;
                    case 2:
                        minigameToChange = minigamesAdapter.activateItem(SYMBOL_MINIGAME_TOUCH, position);

                        if ("TGatherer".equals(minigameToChange)) {
                            tmpChosenMinigames[2] = "TCatcher";
                            tmpChosenMinigames[3] = "TInvader";
                        }

                        if ("TInvader".equals(minigameToChange)) {
                            tmpChosenMinigames[2] = "TCatcher";
                            tmpChosenMinigames[3] = "TGatherer";
                        }

                        break;
                    case 3:
                        tmpChosenMinigames[2] = "TGatherer";
                        minigameToChange = minigamesAdapter.activateItem(SYMBOL_MINIGAME_TOUCH, position);

                        if ("TCatcher".equals(minigameToChange)) {
                            tmpChosenMinigames[2] = "TGatherer";
                            tmpChosenMinigames[3] = "TInvader";
                        }

                        if ("TInvader".equals(minigameToChange)) {
                            tmpChosenMinigames[2] = "TCatcher";
                            tmpChosenMinigames[3] = "TGatherer";
                        }


                        break;
                    case 4:
                        tmpChosenMinigames[3] = "TInvader";
                        minigamesAdapter.activateItem(SYMBOL_MINIGAME_TOUCH, position);
                        ;
                        break;
                    case 5:
                        tmpChosenMinigames[0] = "VBird";
                        minigamesAdapter.activateItem(SYMBOL_MINIGAME_VERTICAL, position);
                        break;
                    case 6:
                        tmpChosenMinigames[0] = "VBouncer";
                        minigamesAdapter.activateItem(SYMBOL_MINIGAME_VERTICAL, position);
                        ;
                        break;

                }
                GameSharedPref.SetChosenMinigamesNames(tmpChosenMinigames);
                minigamesAdapter.notifyDataSetChanged();
//                GameMinigamesManager.changeChosenMinigamesNames(tmpActiveMinigames);

            }
        });


    }

    //    private void resolveActiveMinigamesNames() {
//        tmpChosenMinigames = GameSharedPref.getChosenMinigamesNames();
//    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SoundEffectsCenter.playBackSound(this);
    }

    public void initAdapter() {

        ArrayList<MgcItem> items = new ArrayList<MgcItem>();

        items.add(new MgcItem(SYMBOL_MINIGAME_HORIZONTAL, "HBalance", "Balance", GameSharedPref.isMinigameChosen("HBalance")));
        items.add(new MgcItem(SYMBOL_MINIGAME_TOUCH, "TCatcher", "Catcher", GameSharedPref.isMinigameChosen("TCatcher")));
        items.add(new MgcItem(SYMBOL_MINIGAME_TOUCH, "TGatherer", "Gatherer", GameSharedPref.isMinigameChosen("TGatherer")));
        items.add(new MgcItem(SYMBOL_MINIGAME_TOUCH, "TInvader", "Invader", GameSharedPref.isMinigameChosen("TInvader"), (String) getResources().getString(R.string.cc_achievements_addicts_description) + (String) getResources().getString(R.string.cc_achievements_requirement_ending)));
        items.add(new MgcItem(SYMBOL_MINIGAME_VERTICAL, "VBird", "Bird", GameSharedPref.isMinigameChosen("VBird")));
        items.add(new MgcItem(SYMBOL_MINIGAME_VERTICAL, "VBouncer", "Bouncer", GameSharedPref.isMinigameChosen("VBouncer"), (String) getResources().getString(R.string.cc_achievements_good_start_description) + (String) getResources().getString(R.string.cc_achievements_requirement_ending)));


//        for (String musicLoopPCName : GameSharedPref.getAllMusicLoopsPCNames()) {
//            String currentMusicLoopHumanName = GameSharedPref.getAllMusicLoopsHumanNames()[i];
//            if (GameSharedPref.isMusicLoopChosen(musicLoopPCName)) {
//                items.add(new MusicItem(musicLoopPCName, currentMusicLoopHumanName, true));
//
//            } else {
//                items.add(new MusicItem(musicLoopPCName, currentMusicLoopHumanName, false));
//            }
//            i++;
//        }

        minigamesAdapter = new MgcArrayAdapter(this, items, SkinsCenterListActivity.getCurrentSkin(this));
    }
//    public void fillAdapter() {
//
//        ArrayList<MgcItem> items = new ArrayList<MgcItem>();
//
//        for (String minigame : GameSharedPref.getAllMinigamesNames()) {
//            if (isGameActive(minigame)) {
//                items.add(new MgcItem(minigame.charAt(0), minigame.substring(1), "✓"));
//
//            } else {
//                items.add(new MgcItem(minigame.charAt(0), minigame.substring(1), "X"));
//            }
//        }
//
//        minigamesAdapter = new MgcArrayAdapter(this, items);
//
//    }
//    @Override
//    protected void onPause() {
//        super.onPause();
//        GameSharedPref.SetChosenMinigamesNames(tmpChosenMinigames);
//
//    }
//    private void fillAllMinigames() {
//
//        String[] allMinigames = new String[6];
//
//        allMinigames[0] = "HBalance";
//        allMinigames[1] = "TCatcher";
//        allMinigames[2] = "TGatherer";
//        allMinigames[3] = "TInvader";
//        allMinigames[4] = "VBird";
//        allMinigames[5] = "VBouncer";
//
//
//    }
//    private boolean isGameActive(String minigame) {
//        for (String minigameActive : tmpChosenMinigames) {
//            if (minigame.compareTo(minigameActive) == 0) {
//                return true;
//            }
//
//        }
//        return false;
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
}
