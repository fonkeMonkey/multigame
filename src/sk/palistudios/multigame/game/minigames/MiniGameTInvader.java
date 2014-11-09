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
import sk.palistudios.multigame.mainMenu.DebugSettings;
import sk.palistudios.multigame.tools.RandomGenerator;

/**
 * @author Pali
 */
public class MiniGameTInvader extends AMiniGame implements IMiniGameTouch {

  final static RandomGenerator mRandomGenerator = RandomGenerator.getInstance();
  PointSerializable mPointMiddleOfScreen = null;
  PointSerializable mPointSmallerCircle = null;
  PaintSerializable mPaintMiddleCircle = null;
  PaintSerializable mPaintSmallCircle = null;
  PaintSerializable mPaintLaser = null;
  PaintSerializable mPaintEnemy = null;
  private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
  private int maximumDifficulty;
  private int difficultyStep;
  private int minimumStepsToInvade;
  private int stepsToInvadestep;
  //difficulty
  private int framesToCreateEnemy = 160;
  private int framesToGo = 60;
  private int mCenterCircleSize;
  private int mSmallCircleSize;
  private int rectLeft, rectRight, rectTop, rectDown;
  private int stepsToInvade;

  public MiniGameTInvader(String fileName, Integer position, GameActivity game) {
    super(fileName, position, game);
    type = Typ.Touch;
  }

  public void updateMinigame() {
    //mCanvas = canvas;

    if (framesToGo == 0) {
      createEnemy();
      framesToGo = framesToCreateEnemy;
    }

    framesToGo--;
    moveObjects();
    //        drawMinigame(canvas);
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
    stepsToInvade = 100;

    maximumDifficulty = 1;
    //        difficultyStep = 8;

    minimumStepsToInvade = 50;
    stepsToInvadestep = 5;

    isMinigameInitialized = true;

  }

  public void onUserInteracted(float x, float y) {

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
      mCanvas.drawCircle(enemy.x, enemy.y, mSmallCircleSize, enemy.mPaintSer.mPaint);
    }

  }

  private void createEnemy() {

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

  private void moveObjects() {
    for (Enemy enemy : enemies) {
      if (enemy.stepsToGo != 0) {
        if (takesDamage(enemy)) {
          if (--enemy.damageToTake == 0) {
            enemies.remove(enemy);
            break;
          }
          changeColor(enemy);
        }
        enemy.move();
      } else {
        mGame.onGameLost(mPosition);
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
    if (rectLeft <= enemy.x && rectRight >= enemy.x && rectTop <= enemy.y && rectDown >= enemy.y) {
      return true;
    }
    return false;
  }

  PaintSerializable mPaintDkGray = new PaintSerializable(Color.DKGRAY);
  PaintSerializable mPaintGray = new PaintSerializable(Color.GRAY);
  private void changeColor(Enemy enemy) {
    switch (enemy.damageToTake) {
      case 2:
        enemy.mPaintSer = mPaintDkGray;
        break;
      case 1:
        enemy.mPaintSer = mPaintGray;
        break;
    }
  }

  @Override
  public void onDifficultyIncreased() {
    difficultyStep = (framesToCreateEnemy / 100) * DebugSettings.globalDifficultyCoeficient;

    if (difficultyStep < 1) {
      difficultyStep = 1;
    }

    //        if (stepsToInvade > minimumStepsToInvade) {
    //            stepsToInvade -= stepsToInvadestep;
    //        }

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
  public void setForTutorial() {
    //do nothing
  }

  @Override
  public void setForClassicGame() {
  }

  private class Enemy implements Serializable {

    float x;
    float y;
    private float stepX;
    private float stepY;
    private int damageToTake = 3;
    private PaintSerializable mPaintSer = null;
    private int stepsToGo;

    public Enemy(int initialX, int initialY) {
      x = initialX;
      y = initialY;
      stepsToGo = stepsToInvade;
      stepX = countStep(initialX, mPointMiddleOfScreen.mPoint.x);
      stepY = countStep(initialY, mPointMiddleOfScreen.mPoint.y);
      mPaintSer = mPaintEnemy;
    }

    public void move() {
      stepsToGo--;

      x += stepX;
      y += stepY;

    }

    private float countStep(int initial, int destination) {
      return -((initial - destination) / (float) stepsToGo);
    }
  }
}
