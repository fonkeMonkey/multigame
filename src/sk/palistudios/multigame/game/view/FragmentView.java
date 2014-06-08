package sk.palistudios.multigame.game.view;

import sk.palistudios.multigame.game.minigames.AMiniGame;
import android.content.Context;

/**
 *
 * @author Pali
 */
public class FragmentView extends AFragmentView {

    int backgroundColor;

    public FragmentView(Context context, int color, AMiniGame minigame) {
        super(context, color, minigame);
        backgroundColor = color;
    }

    @Override
    public void setBackgroundColored() {
        setBackgroundColor(backgroundColor);
    }
}
