package sk.palistudios.multigame.game.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import sk.palistudios.multigame.game.GameMinigamesManager;
import sk.palistudios.multigame.game.minigames.AMiniGame;
import sk.palistudios.multigame.game.minigames.IMiniGameTouch;
import sk.palistudios.multigame.game.persistence.GameSharedPref;

/**
 * @author Pali
 */
public class FragmentViewTouch extends AFragmentView {

    private int backgroundColor;
    private boolean wasGameStarted = false;

    public FragmentViewTouch(Context context, int color, final AMiniGame minigame, final int position) {
        super(context, color, minigame);

        backgroundColor = color;

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {

                if (GameMinigamesManager.isMiniGameActive(position) && !minigame.mGame.isGameStopped()) {

                    ((IMiniGameTouch) mMiniGame).onUserInteracted(event.getX(), event.getY());
                    return true;
                }

                if (event.getAction() == MotionEvent.ACTION_DOWN && minigame.mGame.gameStopped && !GameSharedPref.isTutorialModeActivated()) {
                    minigame.mGame.startGame();
//                    minigame.mGame.gameStopped = false;
                }

                return true;
            }
        });

    }

    @Override
    public void setBackgroundColored() {
        setBackgroundColor(backgroundColor);

    }
}
