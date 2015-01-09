package sk.palistudios.multigame.customization_center.skins;

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
import sk.palistudios.multigame.customization_center.CustomizationCenterActivity;
import sk.palistudios.multigame.customization_center.CustomizeFragment;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.tools.SkinManager;

/**
 * @author Pali
 */
public class SkinsFragment extends CustomizeFragment {

  public enum Skins {
    QUAD("kuba"), CORRUPTED("blue_sky"), THRESHOLD("summer"), DIFFUSE("girl_power");

    private String mCompatName;

    Skins(String value) {
      mCompatName = value;
    }

    public String getCompatName() {
      return mCompatName;
    }
  }

  private SkinsArrayAdapter mSkinAdapter;
  private ListView mListView;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    initAdapter();
  }

  public void initAdapter() {
    ArrayList<SkinItem> items = new ArrayList<SkinItem>();
    items.add(new SkinItem("kuba", "QUAD"));
    items.add(new SkinItem("summer", "THRESHOLD", (String) getResources().getString(
        R.string.cc_achievements_champion_description) + (String) getResources().getString(
        R.string.cc_achievements_requirement_ending)));
    items.add(new SkinItem("girl_power", "DIFFUSE", (String) getResources().getString(
        R.string.cc_achievements_magin_ten_description) + (String) getResources().getString(
        R.string.cc_achievements_requirement_ending)));
    items.add(new SkinItem("blue_sky", "CORRUPTED", (String) getResources().getString(
        R.string.cc_achievements_competitive_description) + (String) getResources().getString(
        R.string.cc_achievements_requirement_ending)));
    mSkinAdapter = new SkinsArrayAdapter(getActivity(), items);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.cust_skin_layout, container, false);
    reskinLocally(SkinManager.getInstance().getCurrentSkin());
    return rootView;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mListView = (ListView) getActivity().findViewById(R.id.customize_listview);
    mListView.setAdapter(mSkinAdapter);

    initListeners();
  }

  private void initListeners() {
    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (position == mSkinAdapter.getActiveItem()) {
          return;
        }

        switch (position) {
          case 0:
            MgTracker.trackSkinChanged(GameSharedPref.getChosenSkin(), "QUAD");
            GameSharedPref.setSkinChosen("kuba");
            break;
          case 1:
            MgTracker.trackSkinChanged(GameSharedPref.getChosenSkin(), "THRESHOLD");
            GameSharedPref.setSkinChosen("summer");
            break;
          case 2:
            MgTracker.trackSkinChanged(GameSharedPref.getChosenSkin(), "DIFFUSE");
            GameSharedPref.setSkinChosen("girl_power");
            break;
          case 3:
            MgTracker.trackSkinChanged(GameSharedPref.getChosenSkin(), "CORRUPTED");
            GameSharedPref.setSkinChosen("blue_sky");
            break;
        }
        mSkinAdapter.activateItem(position);
        mSkinAdapter.notifyDataSetChanged();
        if (getActivity() instanceof CustomizationCenterActivity) {
          //Ugly as fuck, startup ftw
          SkinManager.reskin(getActivity(), (ViewGroup) ((ViewGroup) (getActivity().findViewById(
              android.R.id.content))).getChildAt(0));
          ((CustomizationCenterActivity) getActivity()).reskinLocally(SkinManager.getInstance()
              .getCurrentSkin());
        }
      }
    });
  }
}
