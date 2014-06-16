package sk.palistudios.multigame.customization_center.mgc;

// @author Pali

import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.IAdapter;
import sk.palistudios.multigame.customization_center.skins.SkinItem;

import java.util.ArrayList;

public class MgcArrayAdapter extends ArrayAdapter<MgcItem> implements IAdapter {

    private final int colorDisabled;
    private ArrayList<MgcItem> myItems = new ArrayList<MgcItem>();
    private Context context;
    private int lastTActivated;
    private int colorChosen;
    private FrameLayout mFrame;
//    SkinItem skin;
//    public MgcArrayAdapter(Context context, ArrayList<MgcItem> objects) {
//        super(context, R.layout.mgc_listitem, objects);
//        this.context = context;
//        myItems = objects;
//        lastTActivated = findFirstActiveTouchGame();
//
//    }

    MgcArrayAdapter(Context context, ArrayList<MgcItem> items, SkinItem currentSkin) {
        super(context, R.layout.mgc_listitem, items);
        this.context = context;
        myItems = items;
        lastTActivated = findFirstActiveTouchGame();
        colorChosen = currentSkin.getColorChosen();
        colorDisabled = context.getResources().getColor(R.color.listview_inactive_item);
    }

    TextView mHorizontal;
    TextView mVertical;
    TextView mTouch;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//
        //TODO reklama
//            if (convertView instanceof AdView) {
////
//                return convertView;
////
//            } else {

        View view = convertView;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.mgc_listitem, null);
        }

        mFrame = (FrameLayout) view.findViewById(R.id.mgc_frame);
        mHorizontal = (TextView) view.findViewById(R.id.mgc_type_h);
        mVertical = (TextView) view.findViewById(R.id.mgc_type_v);
        mTouch = (TextView) view.findViewById(R.id.mgc_type_t);

        View background = view.findViewById(R.id.mgc_row);

//        TextView textView1 = (TextView) view.findViewById(R.id.mgc_type);
        TextView textView2 = (TextView) view.findViewById(R.id.mgc_name);

//        textView1.setText(String.valueOf(getItem(position).type));
        showCorrectLeftIcon(view, getItem(position).type);
        textView2.setText(String.valueOf(getItem(position).getHumanName()));

        if (myItems.get(position).isChosen()) {
//            textView1.setBackgroundColor(colorChosen);
            background.setBackgroundColor(colorChosen);
        } else {
//            textView1.setBackgroundColor(Color.WHITE);
            background.setBackgroundColor(Color.WHITE);
        }

        if (myItems.get(position).isLocked()) {
//            textView1.setBackgroundColor(Color.LTGRAY);
            background.setBackgroundColor(colorDisabled);
        }

        return view;
    }

    private void showCorrectLeftIcon(View view, char type) {



//        mTouch.requestLayout();

        if (type == MinigamesCenterListActivity.SYMBOL_MINIGAME_VERTICAL) {
            mHorizontal.setVisibility(View.INVISIBLE);
            mTouch.setVisibility(View.GONE);
            mVertical.setVisibility(View.VISIBLE);
        }
        if (type == MinigamesCenterListActivity.SYMBOL_MINIGAME_TOUCH) {
            mHorizontal.setVisibility(View.INVISIBLE);
            mTouch.setVisibility(View.VISIBLE);
            mVertical.setVisibility(View.GONE);
        }
        if (type == MinigamesCenterListActivity.SYMBOL_MINIGAME_HORIZONTAL) {
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
        if (myItems.get(positionClicked).isChosen() == true) {
            return "";
        }

        for (MgcItem item : myItems) {

            if (type != MinigamesCenterListActivity.SYMBOL_MINIGAME_TOUCH) {//H or V
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
        if (type == MinigamesCenterListActivity.SYMBOL_MINIGAME_TOUCH) {
            lastTActivated = tmpLastTActivated;

            for (MgcItem item : myItems) {
                if (item.type == MinigamesCenterListActivity.SYMBOL_MINIGAME_TOUCH) {
                    if (item.isChosen() == false) {
                        return item.getComputerName();
                    }
                }
            }
        }

        return "";


    }

    private int findFirstActiveTouchGame() {
        for (MgcItem item : myItems) {

            if (item.type == MinigamesCenterListActivity.SYMBOL_MINIGAME_TOUCH) {
                if (item.isChosen() == true) {
                    return myItems.indexOf(item);
                }
            }
        }
        return -1;
    }

    public void setColorChosen(int colorChosen) {
        this.colorChosen = colorChosen;
    }
//    private void reDraw() {
//
//        
//
//    }
}
