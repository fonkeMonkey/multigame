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
public class Fragment3 extends AFragment {
//

    int position = 2;
//

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }

        Activity activity = getActivity();
//
        AMiniGame minigame = MinigamesManager.getMinigamesObjects()[2];
//
        mView = new FragmentViewTouch(activity, getResources().getColor(R.color.game_background_3), minigame, position);
//
        return mView;
//
    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//    }
//
//    public FragmentViewTouch getmView() {
//        return mView;
//    }
}
