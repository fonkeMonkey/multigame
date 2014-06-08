package sk.palistudios.multigame.game.view;

// @author Pali
import sk.palistudios.multigame.game.minigames.AMiniGame;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.View;
import sk.palistudios.multigame.game.persistence.GameSharedPref;

public abstract class AFragmentView extends SurfaceView {

//    Paint _paint;
    Handler mHandler = null;
    AMiniGame mMiniGame = null;
    private boolean isMinigameInitialized = false;
    private boolean wasGameSaved = false;

    public AFragmentView(Context context, int color, AMiniGame minigame) {
        super(context);
        setBackgroundColor(color);

        mMiniGame = minigame;

    }
    int _height;
    int _width;
    Bitmap mBitmap;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        _height = getHeight();
        _width = getWidth();

        setMeasuredDimension(_width, _height);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        _height = View.MeasureSpec.getSize(heightMeasureSpec);
        _width = View.MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(_width, _height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isMinigameInitialized) {

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
//        GameMinigamesManager.setMinigamesInitialized(true, Game.getActiveGame());
        isMinigameInitialized = true;
    }

    public void setBackgroundGray() {
        setBackgroundColor(Color.GRAY);
    }

    public abstract void setBackgroundColored();

    public void setGameSaved(boolean status) {
        wasGameSaved = status;
    }
}
