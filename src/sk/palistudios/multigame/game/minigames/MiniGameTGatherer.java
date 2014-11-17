package sk.palistudios.multigame.game.minigames;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.persistence.PaintSerializable;
import sk.palistudios.multigame.game.time.GameTimeManager;
import sk.palistudios.multigame.game.time.ITimeObserver;
import sk.palistudios.multigame.game.view.GameCanvasViewTouch;
import sk.palistudios.multigame.mainMenu.DebugSettings;
import sk.palistudios.multigame.tools.RandomGenerator;

/**
 * @author Pali
 */
public class MiniGameTGatherer extends BaseMiniGame implements
    GameCanvasViewTouch.userInteractedTouchListener,
    ITimeObserver {
  //DIFFICULTY
  public static final int CIRCLE_DURATION = 9;
  private int touchingDistance;
  private int maximumDifficulty;
  private int framesToGenerateCircle = (int) (160 / DebugSettings.GLOBAL_DIFFICULTY_COEFFICIENT);
  private int framesToGo = 20;

  //GRAPHICS
  private final RandomGenerator mRg = RandomGenerator.getInstance();
  private boolean gameLost = false;
  private PaintSerializable mPaintCircleColor = null;
  private PaintSerializable mPaintNumberColor = null;
  private final ArrayList<CircleToTouch> mCircles = new ArrayList<CircleToTouch>();
  private int mCircleSize;
  private int textAlign;

  public MiniGameTGatherer(String fileName, Integer position, GameActivity game) {
    super(fileName, position, game);
    type = Type.Touch;
  }

  public void initMinigame(Bitmap mBitmap, boolean wasGameSaved) {
    mHeight = mBitmap.getHeight();
    mWidth = mBitmap.getWidth();

    mPaintCircleColor = new PaintSerializable(colorAlt, Paint.Style.STROKE);
    mPaintNumberColor = new PaintSerializable(colorMain, Paint.Style.FILL);

    mCircleSize = mWidth / 20;
    textAlign = mCircleSize / 3;
    mPaintNumberColor.setTextSize(mCircleSize);
    touchingDistance = (int) (mCircleSize * 1.5);
    //difficulty
    maximumDifficulty = 1;
    isMinigameInitialized = true;
  }


  public void updateMinigame() {
    if (gameLost) {
      mGame.onGameLost(mPosition);
    }
    generateNewObjects();
  }


  public void onUserInteractedTouch(float x, float y) {
    int touchX = Math.round(x);
    int touchY = Math.round(y);

    for (CircleToTouch obj : mCircles) {
      if ((touchX < obj.x && touchX > obj.x - touchingDistance) ||
          (touchX > obj.x && touchX < obj.x + touchingDistance)) {
        if ((touchY < obj.y && touchY > obj.y - touchingDistance) ||
            (touchY > obj.y && touchY < obj.y + touchingDistance)) {
          mCircles.remove(obj);
          break;
        }
      }
    }
  }

  private void generateNewObjects() {
    if (framesToGo == 0) {
      CircleToTouch circle = new CircleToTouch(mRg.generateInt((mCircleSize),
          mWidth - (mCircleSize)), mRg.generateInt((mCircleSize), mHeight - (mCircleSize)));
      if (!collidesWithOtherCircles(circle)) {
        mCircles.add(circle);
        framesToGo = framesToGenerateCircle;
      } else {
        framesToGo = 1;
      }
    }
    framesToGo--;
  }

  public void drawMinigame(Canvas mCanvas) {
    for (CircleToTouch obj : mCircles) {
      mCanvas.drawCircle(obj.x, obj.y, mCircleSize, mPaintCircleColor.mPaint);
      mCanvas.drawText(String.valueOf(obj.duration), obj.x - textAlign, obj.y + textAlign,
          mPaintNumberColor.mPaint);
    }
  }



  @Override
  public void onTimeChanged() {
    for (CircleToTouch circle : mCircles) {
      circle.decreaseDuration();
      if (circle.duration == 0) {
        gameLost = true;
      }

    }
  }

  @Override
  public void onDifficultyIncreased() {
    int difficultyStep =
        (framesToGenerateCircle / 100) * DebugSettings.GLOBAL_DIFFICULTY_INCREASE_COEFFICIENT;

    if (difficultyStep < 1) {
      difficultyStep = 1;
    }

    if (framesToGenerateCircle >= maximumDifficulty) {
      framesToGenerateCircle -= difficultyStep;
    }
  }

  @Override
  public String getDescription(Context context) {
    return context.getString(R.string.minigames_TGatherer);
  }

  public String getName() {
    return "Gatherer";
  }

  @Override
  public void onMinigameActivated() {
    super.onMinigameActivated();
    GameTimeManager.registerSecondsObserver(this);
  }

  @Override
  public void onMinigameDeactivated() {
    super.onMinigameDeactivated();
    GameTimeManager.unregisterSecondsObserver(this);
  }

  private boolean collidesWithOtherCircles(CircleToTouch circleNew) {
    for (CircleToTouch circleOld : mCircles) {
      if (circleNew.x > circleOld.x - 2 * mCircleSize &&
          circleNew.x < circleOld.x + 2 * mCircleSize) {
        if (circleNew.y > circleOld.y - 2 * mCircleSize &&
            circleNew.y < circleOld.y + 2 * mCircleSize) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public void setDifficultyForTutorial() {
    framesToGenerateCircle *= 1.3;
  }

  @Override
  public void setDifficultyForClassicGame() {
    framesToGenerateCircle *= 1.2;
  }

  private class CircleToTouch implements Serializable {
    private final int x;
    private final int y;
    private int duration;

    public CircleToTouch(int x, int y) {
      this.x = x;
      this.y = y;
      this.duration = CIRCLE_DURATION;
    }

    public void decreaseDuration() {
      duration--;
    }
  }
}
