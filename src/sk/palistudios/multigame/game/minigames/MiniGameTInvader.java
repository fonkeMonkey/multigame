package sk.palistudios.multigame.game.minigames;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.persistence.PaintSerializable;
import sk.palistudios.multigame.game.persistence.PointSerializable;
import sk.palistudios.multigame.game.view.GameCanvasViewTouch;
import sk.palistudios.multigame.mainMenu.DebugSettings;
import sk.palistudios.multigame.tools.RandomGenerator;
import sk.palistudios.multigame.tools.SkinManager;

/**
 * @author Pali
 */
public class MiniGameTInvader extends BaseMiniGame implements
    GameCanvasViewTouch.userInteractedTouchListener {
  //Difficulty
  private int framesToCreateEnemy = (int) (120 / DebugSettings.GLOBAL_DIFFICULTY_COEFFICIENT);
  private int stepsToInvade  = (int) (100 / DebugSettings.GLOBAL_DIFFICULTY_COEFFICIENT);;
  private int maximumDifficulty = 1;
  private int framesToGo = 60;

  //Graphics
  private transient RandomGenerator mRandomGenerator;
  private PaintSerializable mPaintMiddleCircle = null;
  private PaintSerializable mPaintLaser = null;
  private PaintSerializable mPaintLaserFill = null;
  private PaintSerializable mPaintEnemy = null;
  private PaintSerializable mPaintResizeArrows = null;
  private final PaintSerializable mPaintDkGray = new PaintSerializable(Color.DKGRAY);
  private final PaintSerializable mPaintGray = new PaintSerializable(Color.GRAY);
  private PointSerializable mPointMiddleOfScreen = null;
  private PointSerializable mPointSmallerCircle = null;
  private final ArrayList<Enemy> enemies = new ArrayList<Enemy>();
  private int mCenterCircleSize;
  private int mEnemyCircleSize, mEnemyCircleSize1, mEnemyCircleSize2, mEnemyCircleSize3;
  private int rectLeft, rectRight, rectTop, rectDown;

  public MiniGameTInvader(String fileName, Integer position, GameActivity game) {
    super(fileName, position, game);
    type = Type.Touch;
  }

  @Override
  public void initMinigame() {
    if (mGame.isTutorial()){
      framesToCreateEnemy /= DebugSettings.GLOBAL_DIFFICULTY_TUTORIAL_COEFFICIENT;
      stepsToInvade /= DebugSettings.GLOBAL_DIFFICULTY_TUTORIAL_COEFFICIENT;
    }
    mRandomGenerator = RandomGenerator.getInstance();

    mPointMiddleOfScreen = new PointSerializable(mWidth / 2, mHeight / 2);
    if (!mWasgameSaved) {
      mPointSmallerCircle = new PointSerializable(mPointMiddleOfScreen.mPoint.x / 2,
          mPointMiddleOfScreen.mPoint.y / 2);
    }

    mPaintMiddleCircle = new PaintSerializable(mPrimaryColor, Paint.Style.FILL);
    mPaintLaser = new PaintSerializable(mPrimaryColor, Paint.Style.STROKE);
    mPaintLaser.mPaint.setPathEffect(new DashPathEffect(new float[] {30,10}, 0));
    mPaintLaserFill = new PaintSerializable(mPrimaryColor, Paint.Style.FILL);
    mPaintLaserFill.mPaint.setAlpha(64); // 25%
    mPaintEnemy = new PaintSerializable(mSecondaryColor, Paint.Style.FILL);
    final int resizeArrowsColor = (mAlternateColor != 0) ? mAlternateColor : mPrimaryColor;
    mPaintResizeArrows = new PaintSerializable(resizeArrowsColor, Paint.Style.STROKE);

    setRectangleCoordinates(mPointMiddleOfScreen, mPointSmallerCircle);

    mCenterCircleSize = mWidth / 25;
    mEnemyCircleSize = mWidth / 35;
    mEnemyCircleSize1 = (int) (mEnemyCircleSize / 1.5);
    mEnemyCircleSize2 = (int) (mEnemyCircleSize / 3.0);
    mEnemyCircleSize3 = (int) (mEnemyCircleSize / 4.5);

    mPaintLaser.setStrokeWidth((int) (mCenterCircleSize / 5.0));
    mPaintResizeArrows.setStrokeWidth((int) (mCenterCircleSize / 5.0));

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
    if(mBackgroundColor != 0) {
      mCanvas.drawColor(mBackgroundColor);
    }
    mCanvas.drawCircle(mPointMiddleOfScreen.mPoint.x, mPointMiddleOfScreen.mPoint.y,
        mCenterCircleSize, mPaintMiddleCircle.mPaint);

    drawResizeArrows(mCanvas);

    mCanvas.drawRect(rectLeft, rectTop, rectRight, rectDown, mPaintLaserFill.mPaint);
    mCanvas.drawRect(rectLeft, rectTop, rectRight, rectDown, mPaintLaser.mPaint);
    mCanvas.drawPath(new Path(), mPaintLaser.mPaint);

    for (Enemy enemy : enemies) {
      enemy.mPaint.mPaint.setAlpha(255);
      mCanvas.drawCircle(enemy.x, enemy.y, mEnemyCircleSize, enemy.mPaint.mPaint);
      enemy.mPaint.mPaint.setAlpha(128);
      mCanvas.drawCircle(enemy.x - (17 * enemy.stepX), enemy.y - (17 * enemy.stepY),
          mEnemyCircleSize1, enemy.mPaint.mPaint);
      enemy.mPaint.mPaint.setAlpha(90);
      mCanvas.drawCircle(enemy.x - (28 * enemy.stepX), enemy.y - (28 * enemy.stepY),
          mEnemyCircleSize2, enemy.mPaint.mPaint);
      enemy.mPaint.mPaint.setAlpha(51);
      mCanvas.drawCircle(enemy.x - (37 * enemy.stepX), enemy.y - (37 * enemy.stepY),
          mEnemyCircleSize3, enemy.mPaint.mPaint);
    }
  }

  private void drawResizeArrows(Canvas canvas) {
    float centerX = mPointSmallerCircle.mPoint.x;
    float centerY = mPointSmallerCircle.mPoint.y;
    float arrowBasementLength = mHeight / 12.0f;
    float arrowLength = arrowBasementLength / 3.0f;
    float top = centerY - arrowBasementLength;
    float bottom = centerY + arrowBasementLength;
    float left = centerX - arrowBasementLength;
    float right = centerX + arrowBasementLength;
    canvas.drawLine(centerX, top, centerX, bottom, mPaintResizeArrows.mPaint);
    canvas.drawLine(left, centerY, right, centerY, mPaintResizeArrows.mPaint);

    // left arrow
    canvas.drawLine(left, centerY, left + arrowLength, centerY - arrowLength, mPaintResizeArrows.mPaint);
    canvas.drawLine(left, centerY, left + arrowLength, centerY + arrowLength, mPaintResizeArrows.mPaint);

    // top arrow
    canvas.drawLine(centerX, top, centerX - arrowLength, top + arrowLength, mPaintResizeArrows.mPaint);
    canvas.drawLine(centerX, top, centerX + arrowLength, top + arrowLength, mPaintResizeArrows.mPaint);

    // right arrow
    canvas.drawLine(right, centerY, right - arrowLength, centerY - arrowLength, mPaintResizeArrows.mPaint);
    canvas.drawLine(right, centerY, right - arrowLength, centerY + arrowLength, mPaintResizeArrows.mPaint);

    // bottom arrow
    canvas.drawLine(centerX, bottom, centerX - arrowLength, bottom - arrowLength,
        mPaintResizeArrows.mPaint);
    canvas.drawLine(centerX, bottom, centerX + arrowLength, bottom - arrowLength,
        mPaintResizeArrows.mPaint);

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
  public void reskinLocally(SkinManager.Skin currentSkin) {
    final Resources resources = mGame.getResources();
    switch (currentSkin) {
      case QUAD:
        mBackgroundColor = resources.getColor(R.color.game_bg_quad_tinvader);
        mPrimaryColor = resources.getColor(R.color.quad_primary);
        mSecondaryColor = resources.getColor(R.color.quad_secondary);
        break;
      case THRESHOLD:
        mBackgroundColor = Color.TRANSPARENT;
        mPrimaryColor = resources.getColor(R.color.threshold_primary);
        mSecondaryColor = resources.getColor(R.color.threshold_tinvader_secondary);
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
        mAlternateColor = resources.getColor(R.color.corrupted_alt);
        break;
      default:
        mBackgroundColor = resources.getColor(R.color.game_bg_quad_tinvader);
        mPrimaryColor = resources.getColor(R.color.quad_primary);
        mSecondaryColor = resources.getColor(R.color.quad_secondary);
        break;
    }
  }

  @Override
  public String getDescription(Context context) {
    return context.getString(R.string.minigames_TInvader);
  }

  public String getName() {
    return "Invader";
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
