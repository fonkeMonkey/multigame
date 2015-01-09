package sk.palistudios.multigame.customization_center.achievements;

// @author Pali

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.IAdapter;
import sk.palistudios.multigame.customization_center.skins.SkinItem;

public class AchievementsArrayAdapter extends ArrayAdapter<AchievementItem> implements IAdapter {

  private final int colorDisabled;
  TextView rowViewName = null;
  TextView rowViewDescription = null;
  TextView rowViewReward = null;
  private ArrayList<AchievementItem> myItems = new ArrayList<AchievementItem>();
  private Context context;
  private int colorChosen;

  public AchievementsArrayAdapter(Context context, ArrayList<AchievementItem> items,
      SkinItem currentSkin) {
    super(context, R.layout.achievement_listitem, items);
    this.context = context;
    myItems = items;
    colorChosen = currentSkin.getBarSeparatorColor();
    colorDisabled = context.getResources().getColor(R.color.listview_inactive_item);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    View view = convertView;

    if (view == null) {
      LayoutInflater vi = (LayoutInflater) context.getSystemService(
          Context.LAYOUT_INFLATER_SERVICE);
      view = vi.inflate(R.layout.achievement_listitem, null);
    }
    rowViewName = (TextView) view.findViewById(R.id.achievement_name);
    rowViewDescription = (TextView) view.findViewById(R.id.achievement_description);
    rowViewReward = (TextView) view.findViewById(R.id.achievement_reward);
    //        TextView textView3 = (TextView) view.findViewById(R.id.mgc_status);
    //
    rowViewName.setText(String.valueOf(getItem(position).getHumanName()));
    rowViewDescription.setText(String.valueOf(getItem(position).getDescription()));
    rowViewReward.setText(getContext().getResources().getString(R.string.cc_unlocks) +
        String.valueOf(getItem(position).getCorrespondingType()) + " " + String.valueOf(getItem(
        position).getCorrespondingItemHuman()));
    //        textView3.setText(String.valueOf(getItem(position).status));
    //
    rowViewName.setTextSize(25);
    rowViewDescription.setTextSize(20);
    rowViewReward.setTextSize(18);
    //        textView3.setTextSize(25);

    if (myItems.get(position).isActive()) {
      rowViewName.setBackgroundColor(colorChosen);
      rowViewDescription.setBackgroundColor(colorChosen);
      rowViewReward.setBackgroundColor(colorChosen);
    } else {
      rowViewName.setBackgroundColor(Color.WHITE);
      rowViewDescription.setBackgroundColor(Color.WHITE);
      rowViewReward.setBackgroundColor(Color.WHITE);
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

  public void setColorChosen(int colorChosen) {
    this.colorChosen = colorChosen;
  }

  void checkAchievements(int score, int level, Activity act) {
    for (AchievementItem achievementItem : myItems) {
      achievementItem.checkAchievementFullfiled(score, level, act);
    }
  }
}
