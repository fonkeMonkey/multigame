package sk.palistudios.multigame.customization_center.mgc;

// @author Pali

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.IAdapter;
import sk.palistudios.multigame.customization_center.skins.SkinItem;
import sk.palistudios.multigame.tools.GraphicUnitsConvertor;

import java.util.ArrayList;

public class MgcArrayAdapter extends ArrayAdapter<MgcItem> implements IAdapter {

    private ArrayList<MgcItem> myItems = new ArrayList<MgcItem>();
    private Context context;
    private int lastTActivated;
    private int colorChosen;
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
//        skin = currentSkin;
        colorChosen = currentSkin.getColorChosen();
    }

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
            background.setBackgroundColor(Color.LTGRAY);
        }

        return view;
    }

    private void showCorrectLeftIcon(View view, char type) {
        TextView horizontal = (TextView) view.findViewById(R.id.mgc_type_h);
        TextView vertical = (TextView) view.findViewById(R.id.mgc_type_v);
        TextView touch = (TextView) view.findViewById(R.id.mgc_type_t);

        if (type == '⇅') {
            horizontal.setVisibility(View.GONE);
            touch.setVisibility(View.INVISIBLE);
            vertical.setVisibility(View.VISIBLE);
        }
        if (type == '✋') {
            horizontal.setVisibility(View.GONE);
            touch.setVisibility(View.VISIBLE);
            vertical.setVisibility(View.GONE);
        }
        if (type == '⇆') {
            horizontal.setVisibility(View.VISIBLE);
            touch.setVisibility(View.INVISIBLE);
            vertical.setVisibility(View.GONE);
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
