package sk.palistudios.multigame.game.view;

// @author Pali

import android.hardware.Sensor;
import android.support.v4.app.Fragment;
import sk.palistudios.multigame.game.minigames.AMiniGame;

/**
 * @author Pali
 */
public abstract class AFragment extends Fragment {

    final static String ARG_POSITION = "position";
    int mCurrentPosition = -1;
    AMiniGame mMiniGame = null;
    AFragmentView mView = null;

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public AMiniGame getMiniGame() {
        return mMiniGame;
    }

    public AFragmentView getmView() {
        return mView;
    }
}
