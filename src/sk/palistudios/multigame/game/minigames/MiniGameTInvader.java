package sk.palistudios.multigame.game.minigames;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.persistence.PaintSerializable;
import sk.palistudios.multigame.game.persistence.PointSerializable;
import sk.palistudios.multigame.game.view.GameCanvasViewTouch;
import sk.palistudios.multigame.mainMenu.DebugSettings;
import sk.palistudios.multigame.tools.RandomGenerator;

/**
 * @author Pali
 */
public class MiniGameTInvader extends BaseMiniGame implements
    GameCanvasViewTouch.userInteractedTouchListener {
  //Difficulty
  private int maximumDifficulty;
  private int framesToCreateEnemy = (int) (160 / DebugSettings.GLOBAL_DIFFICULTY_COEFFICIENT);
  private int framesToGo = 60;
  private int stepsToInvade;

  //Graphics
  private final RandomGenerator mRandomGenerator = RandomGenerator.getInstance();
  private PaintSerializable mPaintMiddleCircle = null;
  private PaintSerializable mPaintSmallCircle = null;
  private PaintSerializable mPaintLaser = null;
  private PaintSerializable mPaintEnemy = null;
  private final PaintSerializable mPaintDkGray = new PaintSerializable(Color.DKGRAY);
  private final PaintSerializable mPaintGray = new PaintSerializable(Color.GRAY);
  private PointSerializable mPointMiddleOfScreen = null;
  private PointSerializable mPointSmallerCircle = null;
  private final ArrayList<Enemy> enemies = new ArrayList<Enemy>();
  private int mCenterCircleSize;
  private int mSmallCircleSize;
  private int rectLeft, rectRight, rectTop, rectDown;

  public MiniGameTInvader(String fileName, Integer position, GameActivity game) {
    super(fileName, position, game);
    type = Type.Touch;
  }

  public void initMinigame(Bitmap mBitmap, boolean wasGameSaved) {
    mHeight = mBitmap.getHeight();
    mWidth = mBitmap.getWidth();

    mPointMiddleOfScreen = new PointSerializable(mWidth / 2, mHeight / 2);
    if (!wasGameSaved) {
      mPointSmallerCircle = new PointSerializable(mPointMiddleOfScreen.mPoint.x / 2,
          mPointMiddleOfScreen.mPoint.y / 2);
    }

    mPaintMiddleCircle = new PaintSerializable(colorAlt, Paint.Style.STROKE);
    mPaintSmallCircle = new PaintSerializable(colorMain, Paint.Style.FILL);
    mPaintLaser = new PaintSerializable(colorMain, Paint.Style.STROKE);
    mPaintEnemy = new PaintSerializable(colorAlt, Paint.Style.FILL);

    setRectangleCoordinates(mPointMiddleOfScreen, mPointSmallerCircle);

    mCenterCircleSize = mWidth / 25;
    mSmallCircleSize = mWidth / 60;

    //difficulty
    stepsToInvade = (int) (100 / DebugSettings.GLOBAL_DIFFICULTY_COEFFICIENT);

    maximumDifficulty = 1;
    isMinigameInitialized = true;
  }

  public void updateMinigame() {
    if (framesToGo == 0) {
      createEnemy();
      framesToGo = framesToCreateEnemy;
    }

    framesToGo--;
    moveObjects();
  }

  private void createEnemy() {
    //TODO jj mohlo by tu v kľude byť toss iba raz od jedna do štyri a switch
    //Complicated for not have enemy coming from kolma line
    if (mRandomGenerator.tossACoin(50)) {
      if (mRandomGenerator.tossACoin(50)) {
        //LEFT SIDE
        if (mRandomGenerator.tossACoin(50)) {
          //leftup quadrant
          enemies.add(new Enemy(0, mRandomGenerator.generateInt(0, ((mHeight / 2) - 5))));
        } else {
          //leftdown quadrant
          enemies.add(new Enemy(0, mRandomGenerator.generateInt(((mHeight / 2) + 5), mHeight)));
        }
      } else {
        //RIGHT SIDE
        if (mRandomGenerator.tossACoin(50)) {
          //leftup quadrant
          enemies.add(new Enemy(0, mRandomGenerator.generateInt(0, ((mHeight / 2) - 5))));
        } else {
          //leftdown quadrant
          enemies.add(new Enemy(0, mRandomGenerator.generateInt(((mHeight / 2) + 5), mHeight)));
        }
      }
    } else {
      if (mRandomGenerator.tossACoin(50)) {
        //UP
        if (mRandomGenerator.tossACoin(50)) {
          //upleft
          enemies.add(new Enemy(mRandomGenerator.generateInt(0, (mWidth / 2) - 5), 0));
        } else {
          //upright
          enemies.add(new Enemy(mRandomGenerator.generateInt((mWidth / 2) + 5, mWidth), 0));
        }
        //DOWN
      } else {
        if (mRandomGenerator.tossACoin(50)) {
          //downleft
          enemies.add(new Enemy(mRandomGenerator.generateInt(0, (mWidth / 2) - 5), 0));
        } else {
          //downright
          enemies.add(new Enemy(mRandomGenerator.generateInt((mWidth / 2) + 5, mWidth), 0));
        }
      }

    }
  }

  public void onUserInteractedTouch(float x, float y) {
    if (x > 0 && x < mWidth) {
      if (y > 0 && y < mHeight) {
        mPointSmallerCircle.mPoint.x = Math.round(x);
        mPointSmallerCircle.mPoint.y = Math.round(y);
        setRectangleCoordinates(mPointMiddleOfScreen, mPointSmallerCircle);
      }
    }
  }

  public void drawMinigame(Canvas mCanvas) {
    mCanvas.drawCircle(mPointMiddleOfScreen.mPoint.x, mPointMiddleOfScreen.mPoint.y,
        mCenterCircleSize, mPaintMiddleCircle.mPaint);
    mCanvas.drawCircle(mPointSmallerCircle.mPoint.x, mPointSmallerCircle.mPoint.y, mSmallCircleSize,
        mPaintSmallCircle.mPaint);
    mCanvas.drawRect(rectLeft, rectTop, rectRight, rectDown, mPaintLaser.mPaint);

    for (Enemy enemy : enemies) {
      mCanvas.drawCircle(enemy.x, enemy.y, mSmallCircleSize, enemy.mPaint.mPaint);
    }
  }

  private void moveObjects() {
    for (Enemy enemy : enemies) {
      if (enemy.stepsToGo != 0) {
        if (takesDamage(enemy)) {
          if (--enemy.damageToTake == 0) {
            enemies.remove(enemy);
            break;
          }
          enemy.changeColor();
        }
        enemy.move();
      } else {
        if(mGame != null){
          mGame.onGameLost(mPosition);
        }
        break;
      }
    }
  }

  private void setRectangleCoordinates(PointSerializable mPointMiddleOfScreen,
      PointSerializable mPointSmallerCircle) {
    if (mPointSmallerCircle.mPoint.x < mPointMiddleOfScreen.mPoint.x) {
      rectLeft = mPointSmallerCircle.mPoint.x;
      rectRight = mPointMiddleOfScreen.mPoint.x;
    } else {
      rectLeft = mPointMiddleOfScreen.mPoint.x;
      rectRight = mPointSmallerCircle.mPoint.x;
    }

    if (mPointSmallerCircle.mPoint.y < mPointMiddleOfScreen.mPoint.y) {
      rectTop = mPointSmallerCircle.mPoint.y;
      rectDown = mPointMiddleOfScreen.mPoint.y;
    } else {
      rectTop = mPointMiddleOfScreen.mPoint.y;
      rectDown = mPointSmallerCircle.mPoint.y;
    }
  }

  private boolean takesDamage(Enemy enemy) {
    return rectLeft <= enemy.x && rectRight >= enemy.x && rectTop <= enemy.y && rectDown >= enemy.y;
  }

  @Override
  public void onDifficultyIncreased() {
    int difficultyStep =
        (framesToCreateEnemy / 100) * DebugSettings.GLOBAL_DIFFICULTY_INCREASE_COEFFICIENT;

    if (difficultyStep < 1) {
      difficultyStep = 1;
    }
    if (framesToCreateEnemy > maximumDifficulty) {
      framesToCreateEnemy -= difficultyStep;
    }
  }

  @Override
  public String getDescription(Context context) {
    return context.getString(R.string.minigames_TInvader);
  }

  public String getName() {
    return "Invader";
  }

  @Override
  public void setDifficultyForTutorial() {
    //do nothing
  }

  @Override
  public void setDifficultyForClassicGame() {
  }

  private class Enemy implements Serializable {
    float x;
    float y;
    private final float stepX;
    private final float stepY;
    private int damageToTake = 3;
    private PaintSerializable mPaint = null;
    private int stepsToGo;

    public Enemy(int initialX, int initialY) {
      x = initialX;
      y = initialY;
      stepsToGo = stepsToInvade;
      stepX = countStep(initialX, mPointMiddleOfScreen.mPoint.x);
      stepY = countStep(initialY, mPointMiddleOfScreen.mPoint.y);
      mPaint = mPaintEnemy;
    }

    public void move() {
      stepsToGo--;

      x += stepX;
      y += stepY;
    }

    private float countStep(int initial, int destination) {
      return -((initial - destination) / (float) stepsToGo);
    }

    private void changeColor() {
      switch (damageToTake) {
        case 2:
          mPaint = mPaintDkGray;
          break;
        case 1:
          mPaint = mPaintGray;
          break;
      }
    }
  }
}
