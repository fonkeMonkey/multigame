package sk.palistudios.multigame.customization_center.skins;

// @author Pali

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.IAdapter;

public class SkinsArrayAdapter extends ArrayAdapter<SkinItem> implements IAdapter {

  private final int colorDisabled;
  private ArrayList<SkinItem> myItems = new ArrayList<SkinItem>();
  private Context context;
  private int colorChosen;

  public SkinsArrayAdapter(Context context, ArrayList<SkinItem> objects, SkinItem skin) {
    super(context, R.layout.mgc_listitem, objects);
    this.context = context;
    myItems = objects;
    colorChosen = skin.getColorChosen();
    colorDisabled = context.getResources().getColor(R.color.listview_inactive_item);

  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    View view = convertView;

    if (view == null) {
      LayoutInflater vi = (LayoutInflater) context.getSystemService(
          Context.LAYOUT_INFLATER_SERVICE);
      view = vi.inflate(R.layout.skin_listitem, null);
    }
    TextView textView1 = (TextView) view.findViewById(R.id.skin_name);

    textView1.setText(String.valueOf(getItem(position).getHumanName()));

    textView1.setTextSize(25);

    if (myItems.get(position).isChosen()) {
      textView1.setBackgroundColor(colorChosen);
    } else {
      textView1.setBackgroundColor(Color.WHITE);
    }
    if (myItems.get(position).isLocked()) {
      textView1.setBackgroundColor(colorDisabled);
    }

    return view;
  }

  @Override
  public SkinItem getItem(int position) {
    return myItems.get(position);
  }

  @Override
  public void add(SkinItem item) {
    myItems.add(item);
  }

  public void activateItem(int positionClicked) {

    //because the list starts from 1, unlike the arraylsit
    positionClicked -= 1;

    //header was clicked
    if (positionClicked == -1) {
      return;
    }

    for (SkinItem item : myItems) {
      item.inactivate();

    }

    myItems.get(positionClicked).activate();

  }

  public void setColorChosen(int colorChosen) {
    this.colorChosen = colorChosen;
  }
}
