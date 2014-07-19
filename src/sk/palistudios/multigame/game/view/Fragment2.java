package sk.palistudios.multigame.game.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.minigames.MinigamesManager;
import sk.palistudios.multigame.game.minigames.AMiniGame;

/**
 * @author Pali
 */
public class Fragment2 extends AFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }

        Activity activity = getActivity();

        AMiniGame minigame = MinigamesManager.getMinigamesObjects()[1];

        mView = new FragmentView(activity, getResources().getColor(R.color.game_background_2), minigame);

        return mView;


    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//    }
//    
//        public FragmentView getmView() {
//        return mView;
//    }
}
