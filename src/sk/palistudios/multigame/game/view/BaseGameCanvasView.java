package sk.palistudios.multigame.game.view;

// @author Pali

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.minigames.AMiniGame;
import sk.palistudios.multigame.game.minigames.MinigamesManager;
import sk.palistudios.multigame.game.persistence.GameSharedPref;

abstract public class BaseGameCanvasView extends View {
  protected AMiniGame mMiniGame;
  private Bitmap mBitmap;
  private int mPosition = -1;
  private int mBackgroundColor;

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

  public void attachMinigame(AMiniGame minigame, int position){
    mMiniGame = minigame;
    mPosition = position;

    switch (position){
      case 0 : mBackgroundColor = getResources().getColor(R.color.game_background_1); break;
      case 1 : mBackgroundColor = getResources().getColor(R.color.game_background_2); break;
      case 2 : mBackgroundColor = getResources().getColor(R.color.game_background_3); break;
      case 3 : mBackgroundColor = getResources().getColor(R.color.game_background_4); break;
    }
    setBackgroundColor(mBackgroundColor);
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);

    mHeight = getHeight();
    mWidth = getWidth();

    setMeasuredDimension(mWidth, mHeight);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    mHeight = View.MeasureSpec.getSize(heightMeasureSpec);
    mWidth = View.MeasureSpec.getSize(widthMeasureSpec);
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
    GameSharedPref.setMinigamesInitialized(true);
  }

  public abstract void detachMinigame();

  public void setBackgroundGray() {
    setBackgroundColor(Color.GRAY);
  }

  public void setBackgroundColored(){
    setBackgroundColor(mBackgroundColor);
  }

  public void setGameSaved(boolean status) {
    wasGameSaved = status;
  }
}
