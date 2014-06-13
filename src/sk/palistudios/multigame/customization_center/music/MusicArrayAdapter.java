package sk.palistudios.multigame.customization_center.music;

// @author Pali

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

import java.util.ArrayList;

public class MusicArrayAdapter extends ArrayAdapter<MusicItem> implements IAdapter {

    TextView rowView = null;
    private ArrayList<MusicItem> myItems = new ArrayList<MusicItem>();
    private Context context;
    private int colorChosen;

    public MusicArrayAdapter(Context context, ArrayList<MusicItem> objects, SkinItem skin) {
        super(context, R.layout.musicloops_listitem, objects);
        this.context = context;
        myItems = objects;
        colorChosen = skin.getColorChosen();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.musicloops_listitem, null);
        }
        rowView = (TextView) view.findViewById(R.id.music_loop_name);

        rowView.setText(String.valueOf(getItem(position).getHumanName()));

        rowView.setTextSize(25);

        if (myItems.get(position).isChosen()) {
            rowView.setBackgroundColor(colorChosen);
        } else {
            rowView.setBackgroundColor(Color.WHITE);
        }
        if (myItems.get(position).isLocked()) {
            rowView.setBackgroundColor(Color.LTGRAY);
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

    public void activateItem(int positionClicked) {

        //because the list starts from 1, unlike the arraylsit
        positionClicked -= 1;

        //header was clicked
        if (positionClicked == -1) {
            return;
        }

        for (MusicItem item : myItems) {
            item.inactivate();

        }

        myItems.get(positionClicked).activate();
    }

    public void setColorChosen(int colorChosen) {
        this.colorChosen = colorChosen;
    }
}
