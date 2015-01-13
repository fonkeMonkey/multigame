package sk.palistudios.multigame.customization_center.music;

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

public class MusicArrayAdapter extends ArrayAdapter<MusicItem> {
  private ArrayList<MusicItem> myItems = new ArrayList<MusicItem>();
  private Context context;

  public MusicArrayAdapter(Context context, ArrayList<MusicItem> objects) {
    super(context, R.layout.musicloops_listitem, objects);
    this.context = context;
    myItems = objects;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    View view = convertView;

    if (view == null) {
      LayoutInflater vi = (LayoutInflater) context.getSystemService(
          Context.LAYOUT_INFLATER_SERVICE);
      view = vi.inflate(R.layout.list_item_music, null);
    }

    TextView TVName = (TextView) view.findViewById(R.id.name);
    TextView TVDescription = (TextView) view.findViewById(R.id.description);

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
    } else {
      TVName.setTextColor(SkinManager.getInstance().getCurrentListViewColorInactive(
          view.getResources()));
      TVDescription.setText("");
    }
    return view;
  }

  @Override
  public MusicItem getItem(int position) {
    return myItems.get(position);
  }

  @Override
  public void add(MusicItem item) {
    myItems.add(item);
  }

  @Override
  public boolean isEnabled(int position) {
    return !myItems.get(position).isLocked();
  }

  public void activateItem(int positionClicked) {
    for (MusicItem item : myItems) {
      item.inactivate();

    }

    myItems.get(positionClicked).activate();
  }

}
