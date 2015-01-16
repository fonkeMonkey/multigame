package sk.palistudios.multigame.customization_center.achievements;

// @author Pali

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.tools.SkinManager;

public class AchievementsArrayAdapter extends ArrayAdapter<AchievementItem> {
  TextView rowViewName = null;
  TextView rowViewDescription = null;
  TextView rowViewReward = null;
  private ArrayList<AchievementItem> myItems = new ArrayList<AchievementItem>();
  private Context context;

  public AchievementsArrayAdapter(Context context, ArrayList<AchievementItem> items) {
    super(context, R.layout.list_item_achievements, items);
    this.context = context;
    myItems = items;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    View view = convertView;

    if (view == null) {
      LayoutInflater vi = (LayoutInflater) context.getSystemService(
          Context.LAYOUT_INFLATER_SERVICE);
      view = vi.inflate(R.layout.list_item_achievements, null);
    }
    rowViewName = (TextView) view.findViewById(R.id.achievement_name);
    rowViewDescription = (TextView) view.findViewById(R.id.achievement_description);
    rowViewReward = (TextView) view.findViewById(R.id.achievement_reward);
    rowViewName.setText(String.valueOf(getItem(position).getHumanName()));
    rowViewDescription.setText(String.valueOf(getItem(position).getDescription()));
    rowViewReward.setText(getContext().getResources().getString(R.string.cc_unlocks) +
        String.valueOf(getItem(position).getCorrespondingType()) + " " + String.valueOf(getItem(
        position).getCorrespondingItemHuman()));

    if (!myItems.get(position).isActive()) {
      rowViewName.setTextColor(SkinManager.getInstance().getCurrentListViewColorLocked(
          view.getResources()));
      rowViewDescription.setTextColor(SkinManager.getInstance().getCurrentListViewColorLocked(
          view.getResources()));
      rowViewReward.setTextColor(SkinManager.getInstance().getCurrentListViewColorLocked(
          view.getResources()));
    } else {
      rowViewName.setTextColor(SkinManager.getInstance().getCurrentListViewColorActive(
          view.getResources()));
      rowViewDescription.setTextColor(SkinManager.getInstance().getCurrentListViewColorActive(
          view.getResources()));
      rowViewReward.setTextColor(SkinManager.getInstance().getCurrentListViewColorActive(
          view.getResources()));
      //      rowViewName.setTextColor(SkinManager.getInstance().getCurrentListViewColorInactive(
      //          view.getResources()));
      //      rowViewDescription.setText("");
    }
    return view;
  }

  @Override
  public AchievementItem getItem(int position) {
    return myItems.get(position);
  }

  @Override
  public void add(AchievementItem item) {
    myItems.add(item);
  }

  @Override
  public boolean isEnabled(int position) {
    return false;
  }
}
