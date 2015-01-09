package sk.palistudios.multigame.customization_center.music;

import java.util.ArrayList;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import sk.palistudios.multigame.BaseListActivity;
import sk.palistudios.multigame.MgTracker;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.mainMenu.DebugSettings;
import sk.palistudios.multigame.tools.SkinManager;
import sk.palistudios.multigame.tools.Toaster;

/**
 * @author Pali
 */
public class MusicCenterListActivity extends BaseListActivity {

  private MusicArrayAdapter musicArrayAdapter;

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

    TextView header = new TextView(this);
    header.setText((String) getResources().getString(R.string.cc_music_music_center_name));
    header.setTextSize(35);
    header.setBackgroundColor(SkinManager.getSkinCompat(this).getColorHeader());
    header.setGravity(Gravity.CENTER);

    TextView footer = new TextView(this);
    footer.setTextSize(60);
    footer.setText(" ");
    getListView().addFooterView(footer, null, false);

    getListView().addHeaderView(header);
    setListAdapter(musicArrayAdapter);
    getListView().setClickable(true);

    getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
      //click into list
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (position == 0) {
          return;
        }

        if (musicArrayAdapter.getItem(position - 1).isLocked()) {
          Toaster.toastLong(musicArrayAdapter.getItem(position - 1).getLockedDescription(),
              getParent());
          return;
        }

        switch (position) {

          case 1:
            MgTracker.trackMusicChanged(GameSharedPref.getMusicLoopChosen(), "dst_blam");
            GameSharedPref.setMusicLoopChosen("dst_blam");
            break;

          case 2:
            MgTracker.trackMusicChanged(GameSharedPref.getMusicLoopChosen(), "dst_cv_x");
            GameSharedPref.setMusicLoopChosen("dst_cv_x");
            break;

          case 3:
            MgTracker.trackMusicChanged(GameSharedPref.getMusicLoopChosen(), "dst_cyberops");
            GameSharedPref.setMusicLoopChosen("dst_cyberops");
            break;

        }

        musicArrayAdapter.activateItem(position);
        musicArrayAdapter.notifyDataSetChanged();

      }
    });

  }

  public void initAdapter() {

    ArrayList<MusicItem> items = new ArrayList<MusicItem>();

    //        int i = 0;

    //        items.add(new MusicItem("abdessamie_beat", "Abdessamie beat",
    // GameSharedPref.isMusicLoopChosen("abdessamie_beat")));
    //        items.add(new MusicItem("jungle", "Jungle", GameSharedPref.isMusicLoopChosen
    // ("jungle"), (String) getResources().getString(R.string.cc_music_unlock_message_1)));
    items.add(new MusicItem("dst_blam", "Blam", GameSharedPref.isMusicLoopChosen(
        "dst_blam")));//, "You must reach level 10 in order to unlock this music."));
    items.add(new MusicItem("dst_cv_x", "CV X", GameSharedPref.isMusicLoopChosen("dst_cv_x"),
        (String) getResources().getString(R.string.cc_achievements_supporter_description) +
            (String) getResources().getString(
                R.string.cc_achievements_requirement_ending)));//, "You must reach level 10 in
                // order to unlock this music."));
    items.add(new MusicItem("dst_cyberops", "Cyber Ops", GameSharedPref.isMusicLoopChosen(
        "dst_cyberops"), (String) getResources().getString(
        R.string.cc_achievements_lucky_seven_description) + (String) getResources().getString(
        R.string.cc_achievements_requirement_ending)));//, "You must reach level 10 in order to
        // unlock this music."));

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

    musicArrayAdapter = new MusicArrayAdapter(this, items, SkinManager.getSkinCompat(this));
  }

  public MusicArrayAdapter getMusicArrayAdapter() {
    return musicArrayAdapter;
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
