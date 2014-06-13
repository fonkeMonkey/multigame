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
        TextView textView1 = (TextView) view.findViewById(R.id.mgc_type);
        TextView textView2 = (TextView) view.findViewById(R.id.mgc_name);

        textView1.setText(String.valueOf(getItem(position).type));
        textView2.setText(String.valueOf(getItem(position).getHumanName()));

        textView1.setPadding(GraphicUnitsConvertor.convertDptoPx(context, 3), 0, 0, 0);
        textView1.setGravity(Gravity.CENTER);

        textView1.setTextSize(25);
        textView2.setTextSize(25);

        if (myItems.get(position).isChosen()) {
            textView2.setBackgroundColor(colorChosen);
        } else {
            textView2.setBackgroundColor(Color.WHITE);
        }

        if (myItems.get(position).isLocked()) {
            textView2.setBackgroundColor(Color.LTGRAY);
        }

        return view;
    }

    //                AdView adView = new AdView(activity, AdSize.BANNER, ADMOB_PUBLISHER_ID);
//
//// Disable focus for sub-views of the AdView to avoid problems with
//
//// trackpad navigation of the list.
//
//                for (int i = 0; i < adView.getChildCount(); i++) {
//
//                    adView.getChildAt(i).setFocusable(false);
//
//                }
//
//                adView.setFocusable(false);
//
//// Default layout params have to be converted to ListView compatible
//
//// params otherwise there will be a ClassCastException.
//
//                float density = activity.getResources().getDisplayMetrics().density;
//
//                int height = Math.round(AdSize.BANNER.getHeight() * density);
//
//                AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT,
//                        height);
//
//                adView.setLayoutParams(params);
//
//                adView.loadAd(new AdRequest());
//
//                return adView;
//
//            }
//        AdView adView = new AdView((Activity)context, AdSize.BANNER,
//                                   "ca-app-pub-5314490326517173/8380820043");
//
//        // Convert the default layout parameters so that they play nice with
//        // ListView.
//
//        float density = context.getResources().getDisplayMetrics().density;
//        int height = Math.round(AdSize.BANNER.getHeight() * density);
//        AbsListView.LayoutParams params = new AbsListView.LayoutParams(
//            AbsListView.LayoutParams.FILL_PARENT,
//            height);
//        adView.setLayoutParams(params);
//        AdRequest adRequest = new AdRequest();
//        adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
//        
////        AdManager.setTestDevices( new String[] { AdManager.TEST_EMULATOR, "E83D20734F72FB3108F104ABC0FFC738", //Phone ID 
////} );
////}
//        
//        adView.loadAd(adRequest);
//        return adView;
//        }
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
