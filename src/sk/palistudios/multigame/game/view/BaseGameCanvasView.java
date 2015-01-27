package sk.palistudios.multigame.game.view;

// @author Pali

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.minigames.BaseMiniGame;

abstract public class BaseGameCanvasView extends View {
  protected BaseMiniGame mMiniGame;
  private Bitmap mBitmap;
//  private int mBackgroundColor;

  private int mHeight;
  private int mWidth;

  private boolean mIsMinigameInitialized = false;
  private boolean wasGameSaved = false;

  public BaseGameCanvasView(Context context) {
    this(context, null, 0);
  }

  public BaseGameCanvasView(Context context, AttributeSet attrs){
    this(context, attrs, 0);
  }

  public BaseGameCanvasView(Context context, AttributeSet attrs, int defStyle){
    super(context, attrs, defStyle);
  }

  public void attachMinigame(BaseMiniGame minigame, int position){
    mMiniGame = minigame;
//    int mPosition = position;
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
      init(canvas);
    }
    mMiniGame.drawMinigame(canvas);
  }

  public void init(Canvas canvas) {
    Rect rect = canvas.getClipBounds();
    int width = rect.width();
    int height = rect.height();
    mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    mMiniGame.initMinigame(mBitmap, wasGameSaved);
//    GameSharedPref.setMinigamesInitialized(true);
  }

  public abstract void detachMinigame();

  public void setBackgroundGray() {
//    setBackgroundColor(Color.GRAY);
  }

  public void setBackgroundColored(){
//    setBackgroundColor(mBackgroundColor);
  }

  public void setGameSaved(boolean status) {
    wasGameSaved = status;
  }
}
