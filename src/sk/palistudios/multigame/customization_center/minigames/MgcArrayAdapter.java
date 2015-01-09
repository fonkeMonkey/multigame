package sk.palistudios.multigame.customization_center.minigames;

// @author Pali

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.IAdapter;

public class MgcArrayAdapter extends ArrayAdapter<MgcItem> implements IAdapter {
  private final int colorDisabled;
  TextView mHorizontal;
  TextView mVertical;
  TextView mTouch;
  private ArrayList<MgcItem> myItems = new ArrayList<MgcItem>();
  private Context context;
  private int lastTActivated;
  private int colorChosen;
  private FrameLayout mFrame;
  MgcArrayAdapter(Context context, ArrayList<MgcItem> items) {
    super(context, R.layout.mgc_listitem, items);
    this.context = context;
    myItems = items;
    lastTActivated = findFirstActiveTouchGame();
    colorDisabled = context.getResources().getColor(R.color.listview_inactive_item);
  }


  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view = convertView;
    if (view == null) {
      LayoutInflater vi = (LayoutInflater) context.getSystemService(
          Context.LAYOUT_INFLATER_SERVICE);
      view = vi.inflate(R.layout.mgc_listitem, null);
    }

    mFrame = (FrameLayout) view.findViewById(R.id.mgc_frame);
    mHorizontal = (TextView) view.findViewById(R.id.mgc_type_h);
    mVertical = (TextView) view.findViewById(R.id.mgc_type_v);
    mTouch = (TextView) view.findViewById(R.id.mgc_type_t);

    View background = view.findViewById(R.id.mgc_row);

    TextView textView2 = (TextView) view.findViewById(R.id.mgc_name);

    showCorrectLeftIcon(view, getItem(position).type);
    textView2.setText(String.valueOf(getItem(position).getHumanName()));

    if (myItems.get(position).isActive()) {
      background.setBackgroundColor(colorChosen);
    } else {
      background.setBackgroundColor(Color.WHITE);
    }

    if (myItems.get(position).isLocked()) {
      background.setBackgroundColor(colorDisabled);
    }

    return view;
  }

  private void showCorrectLeftIcon(View view, char type) {
    if (type == MinigamesFragment.SYMBOL_MINIGAME_VERTICAL) {
      mHorizontal.setVisibility(View.INVISIBLE);
      mTouch.setVisibility(View.GONE);
      mVertical.setVisibility(View.VISIBLE);
    }
    if (type == MinigamesFragment.SYMBOL_MINIGAME_TOUCH) {
      mHorizontal.setVisibility(View.INVISIBLE);
      mTouch.setVisibility(View.VISIBLE);
      mVertical.setVisibility(View.GONE);
    }
    if (type == MinigamesFragment.SYMBOL_MINIGAME_HORIZONTAL) {
      mHorizontal.setVisibility(View.VISIBLE);
      mTouch.setVisibility(View.GONE);
      mVertical.setVisibility(View.GONE);
    }
  }

  @Override
  public MgcItem getItem(int position) {
    return myItems.get(position);
  }

  @Override
  public void add(MgcItem item) {
    myItems.add(item);
  }

  public String activateItem(char type, int positionClicked) {

    //because the list starts from 1, unlike the arraylsit
    positionClicked -= 1;
    int currentPosition;
    int tmpLastTActivated = -1;

    //if it is already activated
    if (myItems.get(positionClicked).isActive() == true) {
      return "";
    }

    for (MgcItem item : myItems) {
      //H or V
      if (type != MinigamesFragment.SYMBOL_MINIGAME_TOUCH) {
        if (item.type == type) {
          item.inactivate();
        }
        myItems.get(positionClicked).activate();
      } else {//T
        if (item.type == type) {
          item.inactivate();
          currentPosition = myItems.indexOf(item);

          if (lastTActivated == currentPosition) {
            myItems.get(currentPosition).activate();
          }

          if (positionClicked == currentPosition) {
            myItems.get(currentPosition).activate();

            tmpLastTActivated = currentPosition;
          }

        }

      }

    }
    if (type == MinigamesFragment.SYMBOL_MINIGAME_TOUCH) {
      lastTActivated = tmpLastTActivated;

      for (MgcItem item : myItems) {
        if (item.type == MinigamesFragment.SYMBOL_MINIGAME_TOUCH) {
          if (item.isActive() == false) {
            return item.getComputerName();
          }
        }
      }
    }

    return "";
  }

  private int findFirstActiveTouchGame() {
    for (MgcItem item : myItems) {

      if (item.type == MinigamesFragment.SYMBOL_MINIGAME_TOUCH) {
        if (item.isActive() == true) {
          return myItems.indexOf(item);
        }
      }
    }
    return -1;
  }

  public void setColorChosen(int colorChosen) {
    this.colorChosen = colorChosen;
  }
}
