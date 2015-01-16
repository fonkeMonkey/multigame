package sk.palistudios.multigame.customization_center.music;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import sk.palistudios.multigame.MgTracker;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.CustomizeFragment;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.tools.SkinManager;

/**
 * @author Pali
 */
public class MusicFragment extends CustomizeFragment {

  private MusicArrayAdapter mMusicArrayAdapter;
  private ListView mListView;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    initAdapter();
  }

  public void initAdapter() {

    ArrayList<MusicItem> items = new ArrayList<MusicItem>();
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
    mMusicArrayAdapter = new MusicArrayAdapter(getActivity(), items);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.cust_music_layout, container, false);
    reskinLocally(SkinManager.getInstance().getCurrentSkin());
    return rootView;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mListView = (ListView) getActivity().findViewById(R.id.customize_listview_music);
    mListView.setAdapter(mMusicArrayAdapter);

    initListeners();
  }

  private void initListeners() {
    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {

          case 0:
            MgTracker.trackMusicChanged(GameSharedPref.getMusicLoopChosen(), "dst_blam");
            GameSharedPref.setMusicLoopChosen("dst_blam");
            break;

          case 1:
            MgTracker.trackMusicChanged(GameSharedPref.getMusicLoopChosen(), "dst_cv_x");
            GameSharedPref.setMusicLoopChosen("dst_cv_x");
            break;

          case 2:
            MgTracker.trackMusicChanged(GameSharedPref.getMusicLoopChosen(), "dst_cyberops");
            GameSharedPref.setMusicLoopChosen("dst_cyberops");
            break;

        }

        mMusicArrayAdapter.activateItem(position);
        mMusicArrayAdapter.notifyDataSetChanged();
      }
    });
  }

  @Override
  public void reskinLocally(SkinManager.Skin currentSkin) {
    super.reskinLocally(currentSkin);
    if (isAdded()) {
      mMusicArrayAdapter.notifyDataSetChanged();
    }
  }
}
