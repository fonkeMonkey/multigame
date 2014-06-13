package sk.palistudios.multigame.hall_of_fame;

// @author Pali

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.skins.SkinItem;
import sk.palistudios.multigame.customization_center.skins.SkinsCenterListActivity;
import sk.palistudios.multigame.tools.GraphicUnitsConvertor;

import java.util.ArrayList;
import java.util.Arrays;

public class HofArrayAdapter extends ArrayAdapter<HofItem> {

    private ArrayList<HofItem> myItems = new ArrayList<HofItem>();
    private Context mContext;

    public HofArrayAdapter(Context context, HofItem[] objects) {
        super(context, R.layout.hof_listitem, objects);
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

        View view = convertView;

        if (view == null) {
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.hof_listitem, null);
        }
        TextView textView1 = (TextView) view.findViewById(R.id.hof_ranking);
        TextView textView2 = (TextView) view.findViewById(R.id.hof_player);
        TextView textView3 = (TextView) view.findViewById(R.id.hof_score);

        textView1.setPadding(GraphicUnitsConvertor.convertDptoPx(mContext, 5), 0, 0, 0);
        textView3.setPadding(0, 0, GraphicUnitsConvertor.convertDptoPx(mContext, 5), 0);

        textView1.setText(String.valueOf(getItem(position).rank) + ".");
        textView2.setText(String.valueOf(getItem(position).name));
        textView3.setText(String.valueOf(getItem(position).score));

        boolean shouldBeHightlighted = shouldBeHiglighted(getItem(position).name);

        if (shouldBeHightlighted) {
            textView1.setTypeface(null, Typeface.BOLD);
            textView2.setTypeface(null, Typeface.BOLD);
            textView3.setTypeface(null, Typeface.BOLD);
            SkinItem currentSkin = SkinsCenterListActivity.getCurrentSkin(mContext);
            view.setBackgroundColor(currentSkin.getColorChosen());
        }

        textView1.setTextSize(25);
        textView2.setTextSize(25);
        textView3.setTextSize(25);

        return view;
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
        // TODO Auto-generated method stub

        return false;
    }
}
