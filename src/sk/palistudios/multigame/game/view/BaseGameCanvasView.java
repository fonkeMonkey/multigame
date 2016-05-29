package sk.palistudios.multigame.game.view;

// @author Pali

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.minigames.BaseMiniGame;

abstract public class BaseGameCanvasView extends View {
  protected BaseMiniGame mMiniGame;

  private int mHeight;
  private int mWidth;

  private boolean mIsMinigameInitialized = false;
  private boolean mWasGameSaved = false;

  private boolean mGameLost = false;

  public BaseGameCanvasView(Context context) {
    this(context, null, 0);
  }

  public BaseGameCanvasView(Context context, AttributeSet attrs){
    this(context, attrs, 0);
  }

  public BaseGameCanvasView(Context context, AttributeSet attrs, int defStyle){
    super(context, attrs, defStyle);

    setLayerType(LAYER_TYPE_SOFTWARE, null);
  }

  public void attachMinigame(BaseMiniGame minigame, int position){
    mMiniGame = minigame;
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);

    mHeight = getHeight();
    mWidth = getWidth();

    setMeasuredDimension(mWidth, mHeight);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (!mIsMinigameInitialized) {
      mIsMinigameInitialized = true;
      initMinigame(canvas);
    }
    mMiniGame.drawMinigame(canvas);
    if (mGameLost) {
      canvas.drawColor(getResources().getColor(R.color.lost_game_overlay));
    }
  }

  public void initMinigame(Canvas canvas) {
    Rect rect = canvas.getClipBounds();
    int width = rect.width();
    int height = rect.height();
    mMiniGame.initMinigame(width, height, mWasGameSaved);
  }

  public abstract void detachMinigame();

  public void onGameLost() {
    mGameLost = true;
    invalidate();
  }

  public void setGameSaved(boolean status) {
    mWasGameSaved = status;
  }
}
