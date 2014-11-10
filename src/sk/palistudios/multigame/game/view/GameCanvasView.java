package sk.palistudios.multigame.game.view;

import android.content.Context;
import android.util.AttributeSet;

import sk.palistudios.multigame.game.minigames.AMiniGame;

/**
 * @author Pali
 */
public class GameCanvasView extends BaseGameCanvasView {
  public GameCanvasView(Context context) {
    this(context, null, 0);
  }

  public GameCanvasView(Context context, AttributeSet attrs){
    this(context, attrs, 0);
  }

  public GameCanvasView(Context context, AttributeSet attrs, int defStyle){
    super(context, attrs, defStyle);
  }

  @Override
  public void detachMinigame() {
    mMiniGame = null;
  }
}
