package sk.palistudios.multigame.hall_of_fame;

// @author Pali

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.tools.SkinManager;

public class HofArrayAdapter extends ArrayAdapter<HofItem> {

  private ArrayList<HofItem> myItems = new ArrayList<HofItem>();
  private Context mContext;

  public HofArrayAdapter(Context context, HofItem[] objects) {
    super(context, R.layout.hof_list_item, objects);
    mContext = context;
    myItems = new ArrayList<HofItem>(Arrays.asList(objects));
  }

  @Override
  public HofItem getItem(int position) {
    return myItems.get(position);
  }

  @Override
  public void add(HofItem item) {
    myItems.add(item);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      LayoutInflater vi = (LayoutInflater) mContext.getSystemService(
          Context.LAYOUT_INFLATER_SERVICE);
      convertView = vi.inflate(R.layout.hof_list_item, null);
    }
    TextView positionTV = (TextView) convertView.findViewById(R.id.position);
    TextView nameTV = (TextView) convertView.findViewById(R.id.name);
    TextView scoreTV = (TextView) convertView.findViewById(R.id.score);
    View underlineView = (View) convertView.findViewById(R.id.underline);

    positionTV.setText(String.valueOf(position + 1) + ".");
    nameTV.setText(getItem(position).getName());
    scoreTV.setText(String.valueOf(getItem(position).getScore()));

    boolean isPlayer = shouldBeHiglighted(getItem(position).getName());

    int skinColor;
    if (isPlayer) {
      positionTV.setTypeface(null, Typeface.BOLD);
      nameTV.setTypeface(null, Typeface.BOLD);
      scoreTV.setTypeface(null, Typeface.BOLD);
      skinColor = SkinManager.getInstance().getCurrentTextColorListItemActive(
          convertView.getResources());
    } else {
      positionTV.setTypeface(null, Typeface.BOLD_ITALIC);
      nameTV.setTypeface(null, Typeface.BOLD_ITALIC);
      scoreTV.setTypeface(null, Typeface.BOLD_ITALIC);
      skinColor = SkinManager.getInstance().getCurrentTextColorListItemInactive(
          convertView.getResources());
    }
    positionTV.setTextColor(skinColor);
    nameTV.setTextColor(skinColor);
    scoreTV.setTextColor(skinColor);
    underlineView.setBackgroundColor(skinColor);

    return convertView;
  }

  /* Hack, lebo sa mi nechcelo updatovať db. */
  private boolean shouldBeHiglighted(String name) {
    if (name.equals("Chuck N.")) {
      return false;
    }
    if (name.equals("Steven S.")) {
      return false;
    }
    if (name.equals("Bruce L.")) {
      return false;
    }
    if (name.equals("Bruce W.")) {
      return false;
    }
    if (name.equals("Arnold S.")) {
      return false;
    }
    if (name.equals("Sylvester S.")) {
      return false;
    }
    if (name.equals("Jackie Ch.")) {
      return false;
    }
    if (name.equals("Vin D.")) {
      return false;
    }
    if (name.equals("Denzel W.")) {
      return false;
    }
    if (name.equals("Jason S.")) {
      return false;
    }
    return true;
  }

  /* Makes listview unclickable. */
  @Override
  public boolean isEnabled(int position) {
    return false;
  }
}
