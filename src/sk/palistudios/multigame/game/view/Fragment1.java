package sk.palistudios.multigame.game.view;

import sk.palistudios.multigame.game.minigames.AMiniGame;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.GameMinigamesManager;

/**
 *
 * @author Pali
 */
public class Fragment1 extends AFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {


        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }

        Activity activity = getActivity();
        AMiniGame minigame = GameMinigamesManager.getMinigamesObjects()[0];
        mView = new FragmentView(activity, getResources().getColor(R.color.game_background_1)
//                SkinsCenterListActivity.getCurrentSkin(activity).getBarBgColor()
                , minigame);
        return mView;



    }
}
