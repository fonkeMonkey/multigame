package sk.palistudios.multigame.game.minigames;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.persistence.PaintSerializable;
import sk.palistudios.multigame.game.time.GameTimeManager;
import sk.palistudios.multigame.game.time.ISecondsObserver;
import sk.palistudios.multigame.game.view.GameCanvasViewTouch;
import sk.palistudios.multigame.mainMenu.DebugSettings;
import sk.palistudios.multigame.tools.RandomGenerator;
import sk.palistudios.multigame.tools.SkinManager;

/**
 * @author Pali
 */
public class MiniGameTGatherer extends BaseMiniGame implements
    GameCanvasViewTouch.userInteractedTouchListener, ISecondsObserver {

  private static final long INITIAL_TIME_TO_LIVE_MILLIS = 10 * 1000;
  private static final long TIME_PERIOD_HALF = INITIAL_TIME_TO_LIVE_MILLIS / 2;
  private static final long TIME_PERIOD_QUARTER = INITIAL_TIME_TO_LIVE_MILLIS / 4;

  //DIFFICULTY
  private int framesToGenerateCircle = (int) (160 / DebugSettings.GLOBAL_DIFFICULTY_COEFFICIENT);
  public static final int CIRCLE_DURATION = 10;
  private int touchingDistance;
  private int maximumDifficulty;
  private int framesToGo = 20;

  //GRAPHICS
  private transient RandomGenerator mRg;
  private boolean gameLost = false;
  private PaintSerializable mPaintCircleColor;
  private PaintSerializable mPaintCircleCenterColor = null;
  private final ArrayList<CircleToTouch> mCircles = new ArrayList<CircleToTouch>();
  private int mCircleSize;
  private int mCircleCenterSize;

  private long mLastUpdate;

  public MiniGameTGatherer(String fileName, Integer position, GameActivity game) {
    super(fileName, position, game);
    type = Type.Touch;
  }

  public void initMinigame(Bitmap mBitmap, boolean wasGameSaved) {
    if (mGame.isTutorial()) {
      framesToGenerateCircle /= DebugSettings.GLOBAL_DIFFICULTY_TUTORIAL_COEFFICIENT;
    }

    mHeight = mBitmap.getHeight();
    mWidth = mBitmap.getWidth();
    mRg = RandomGenerator.getInstance();

    mPaintCircleColor = new PaintSerializable(mPrimaryColor, Paint.Style.FILL);
    mPaintCircleCenterColor = new PaintSerializable(mSecondaryColor, Paint.Style.FILL);

    mCircleSize = mWidth / 20;
    mCircleCenterSize = (int) (mCircleSize / 2.0);
    touchingDistance = (int) (mCircleSize * 1.5);
    //difficulty
    maximumDifficulty = 1;
    isMinigameInitialized = true;

    mLastUpdate = -1;
  }


  public void updateMinigame() {
    if (gameLost) {
      if(mGame != null){
        mGame.onGameLost(mPosition);
      }
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
    final long now = System.currentTimeMillis();
    if(mLastUpdate == -1) {
      mLastUpdate = now;
    }
    final long timeDifference = now - mLastUpdate;
    mLastUpdate = now;
    if(mBackgroundColor != 0) {
      mCanvas.drawColor(mBackgroundColor);
    }

    int tmp;
    for (CircleToTouch obj : mCircles) {
      obj.timeToLive -= timeDifference;

      // ked ostava posledna stvrtina casu, gulicka zacne blikat
      // 10 cyklov drawMinigame sa nevykresli a 10 cyklov ano
      if (obj.timeToLive <= TIME_PERIOD_QUARTER) {
        tmp = obj.mCycle / 10;
        if (tmp > 0) {
          obj.mCycle = tmp;
        }
        if (obj.mCycle % 2 == 1) {
          drawCircleQuarters(mCanvas, obj);
          mCanvas.drawCircle(obj.x, obj.y, mCircleCenterSize, mPaintCircleCenterColor.mPaint);
        }
        obj.mCycle++;
      } else {
        drawCircleQuarters(mCanvas, obj);
        mCanvas.drawCircle(obj.x, obj.y, mCircleCenterSize, mPaintCircleCenterColor.mPaint);
      }
    }
  }

  private void drawCircleQuarters(Canvas canvas, CircleToTouch obj) {
    int alpha;

    // od 10 do 5 sekund
    if(obj.timeToLive >= TIME_PERIOD_HALF) {
      alpha = (int) (((obj.timeToLive - TIME_PERIOD_HALF) / (TIME_PERIOD_HALF * 1.0)) * 255);
      if (alpha < 5) {
        alpha = 5;
      }
    } else {
      alpha = 5; // 2%
    }
    mPaintCircleColor.mPaint.setAlpha(alpha);
    RectF arc = new RectF(obj.x - mCircleSize, obj.y - mCircleSize, obj.x + mCircleSize,
        obj.y + mCircleSize);
    canvas.drawArc(arc, 270, 90, true, mPaintCircleColor.mPaint);

    // od 7,5 do 2,5 sekund
    if(obj.timeToLive > (INITIAL_TIME_TO_LIVE_MILLIS - TIME_PERIOD_QUARTER)) {
      alpha = 255;
    } else if(obj.timeToLive < TIME_PERIOD_QUARTER) {
      alpha = 5; // 2%
    } else {
      alpha = (int) (((obj.timeToLive - TIME_PERIOD_QUARTER) / (TIME_PERIOD_HALF * 1.0)) * 255);
      if(alpha < 5) {
        alpha = 5;
      }
    }
    mPaintCircleColor.mPaint.setAlpha(alpha);
    arc = new RectF(obj.x - mCircleSize, obj.y - mCircleSize,
        obj.x + mCircleSize, obj.y + mCircleSize);
    canvas.drawArc(arc, 0, 90, true, mPaintCircleColor.mPaint);

    // od 5 do 0 sekund
    if(obj.timeToLive >= TIME_PERIOD_HALF) {
      alpha = 255;
    } else {
      alpha = (int) ((obj.timeToLive / (TIME_PERIOD_HALF * 1.0)) * 255);
      if(alpha < 5) {
        alpha = 5;
      }
    }
    mPaintCircleColor.mPaint.setAlpha(alpha);
    arc = new RectF(obj.x - mCircleSize, obj.y - mCircleSize,
        obj.x + mCircleSize, obj.y + mCircleSize);
    canvas.drawArc(arc, 90, 90, true, mPaintCircleColor.mPaint);

    // od 2,5 do 0 sekund
    if(obj.timeToLive >= TIME_PERIOD_QUARTER) {
      alpha = 255;
    } else {
      alpha = (int) ((obj.timeToLive / (TIME_PERIOD_QUARTER * 1.0)) * 255);
      if(alpha < 5) {
        alpha = 5;
      }
    }
    mPaintCircleColor.mPaint.setAlpha(alpha);
    arc = new RectF(obj.x - mCircleSize, obj.y - mCircleSize,
        obj.x + mCircleSize, obj.y + mCircleSize);
    canvas.drawArc(arc, 180, 90, true, mPaintCircleColor.mPaint);
  }

  @Override
  public void onSecondPassed() {
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

  @Override
  public void reskinLocally(SkinManager.Skin currentSkin) {
    final Resources resources = mGame.getResources();
    switch (currentSkin) {
      case QUAD:
        mBackgroundColor = resources.getColor(R.color.game_bg_quad_tgatherer);
        mPrimaryColor = resources.getColor(R.color.quad_primary);
        mSecondaryColor = resources.getColor(R.color.quad_secondary);
        break;
      case THRESHOLD:
        mBackgroundColor = Color.TRANSPARENT;
        mPrimaryColor = resources.getColor(R.color.threshold_primary);
        mSecondaryColor = resources.getColor(R.color.threshold_tgatherer_secondary);
        break;
      case DIFFUSE:
        mBackgroundColor = Color.TRANSPARENT;
        mPrimaryColor = resources.getColor(R.color.diffuse_primary);
        mSecondaryColor = resources.getColor(R.color.diffuse_secondary);
        break;
      case CORRUPTED:
        mBackgroundColor = Color.TRANSPARENT;
        mPrimaryColor = resources.getColor(R.color.corrupted_primary);
        mSecondaryColor = resources.getColor(R.color.corrupted_secondary);
        break;
      default:
        mBackgroundColor = resources.getColor(R.color.game_bg_quad_tgatherer);
        mPrimaryColor = resources.getColor(R.color.quad_primary);
        mSecondaryColor = resources.getColor(R.color.quad_secondary);
        break;
    }
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

  private class CircleToTouch implements Serializable {
    private final int x;
    private final int y;
    private int duration;
    private long nextUpdate = -1;
    private long timeToLive;
    private int mCycle;

    public CircleToTouch(int x, int y) {
      this.x = x;
      this.y = y;
      this.duration = CIRCLE_DURATION;
      this.timeToLive = INITIAL_TIME_TO_LIVE_MILLIS;
    }

    public void decreaseDuration() {
      //TODO yy ugly hack, lebo sa mi v tutoriali to volalo 4krat za sekundu pokiaaľ som vyhral 3
      // predošlé in a row
      if (!mGame.isTutorial() || System.currentTimeMillis() > nextUpdate) {
        duration--;
        nextUpdate = System.currentTimeMillis() + 900;
      }
    }
  }
}
