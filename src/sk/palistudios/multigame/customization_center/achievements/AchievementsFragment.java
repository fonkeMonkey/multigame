package sk.palistudios.multigame.customization_center.achievements;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.CustomizeFragment;
import sk.palistudios.multigame.tools.AchievementsHelper;
import sk.palistudios.multigame.tools.SkinManager;

/**
 * @author Pali
 */
public class AchievementsFragment extends CustomizeFragment {


  private AchievementsArrayAdapter mAchievementsAdapter;
  private ListView mListView;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    initAdapter();
  }

  public void initAdapter() {

    ArrayList<AchievementItem> items = new ArrayList<AchievementItem>(
        AchievementsHelper.getAchievements(getActivity()));
    mAchievementsAdapter = new AchievementsArrayAdapter(getActivity(), items);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.cust_achievements_layout, container,
        false);
    reskinLocally(SkinManager.getInstance().getCurrentSkin());
    return rootView;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mListView = (ListView) getActivity().findViewById(R.id.customize_listview_achievements);
    mListView.setAdapter(mAchievementsAdapter);
  }
@Override
public void reskinLocally(SkinManager.Skin currentSkin) {
  super.reskinLocally(currentSkin);
  if (isAdded()) {
    mAchievementsAdapter.notifyDataSetChanged();
  }
}
}
