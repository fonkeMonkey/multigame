package sk.palistudios.multigame.customization_center.skins;

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

public class SkinsArrayAdapter extends ArrayAdapter<SkinItem> {
  private ArrayList<SkinItem> myItems = new ArrayList<SkinItem>();
  private Context context;
  private int mActiveItem;

  public SkinsArrayAdapter(Context context, ArrayList<SkinItem> objects) {
    super(context, R.layout.mgc_listitem, objects);
    this.context = context;
    myItems = objects;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view = convertView;

    if (view == null) {
      LayoutInflater vi = (LayoutInflater) context.getSystemService(
          Context.LAYOUT_INFLATER_SERVICE);
      view = vi.inflate(R.layout.list_item_skins, null);
    }
    TextView TVName = (TextView) view.findViewById(R.id.skin_name);
    TextView TVDescription = (TextView) view.findViewById(R.id.skin_description);

    TVName.setText(String.valueOf(getItem(position).getHumanName()));

    if (myItems.get(position).isLocked()) {
      TVName.setTextColor(SkinManager.getInstance().getCurrentListViewColorLocked(
          view.getResources()));
      TVDescription.setTextColor(SkinManager.getInstance().getCurrentListViewColorLocked(
          view.getResources()));
      TVDescription.setText(getItem(position).getLockedDescription());
    } else if (myItems.get(position).isActive()) {
      TVName.setTextColor(SkinManager.getInstance().getCurrentListViewColorActive(
          view.getResources()));
      TVDescription.setText("");
      mActiveItem = position;
    } else {
      TVName.setTextColor(SkinManager.getInstance().getCurrentListViewColorInactive(
          view.getResources()));
      TVDescription.setText("");
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
    for (SkinItem item : myItems) {
      item.inactivate();
    }

    myItems.get(positionClicked).activate();
  }

  @Override
  public boolean isEnabled(int position) {
    return !myItems.get(position).isLocked();
  }

  public int getActiveItem() {
    for (int i = 0; i < myItems.size(); i++) {
      if (myItems.get(i).isActive()){
        return i;
      }
    }
    return -1;
  }
}
