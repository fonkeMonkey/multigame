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
import sk.palistudios.multigame.game.view.GameCanvasViewTouch;
import sk.palistudios.multigame.mainMenu.DebugSettings;
import sk.palistudios.multigame.tools.RandomGenerator;
import sk.palistudios.multigame.tools.SkinManager;

/**
 * @author Pali
 */
public class MiniGameTCatcher extends BaseMiniGame
    implements GameCanvasViewTouch.userInteractedTouchListener {
  //DIFFICULTY
  private int framesToGenerateNewBall = (int) (160 / DebugSettings.GLOBAL_DIFFICULTY_COEFFICIENT);
  private float fallingStep;
  private int fallingHeight;
  private int maxDifficulty;
  private int framesToGo = 30;

  //GRAPHICS
  private final int NUMBER_OF_COLUMNS = 7;
  private final int[] mCatchingBalls = new int[NUMBER_OF_COLUMNS];
  private final ArrayList<FallingBall> mFallingBalls = new ArrayList<FallingBall>();
  private PaintSerializable mPaintFallingBalls = null;
  private PaintSerializable mPaintCatchingBallInactive = null;
  private PaintSerializable mPaintCatchingBallActive = null;
  private int activeBall = 4;
  private int mBallSize;
  private int catchingBallsHeight;
  private int columnWidth;

  public MiniGameTCatcher(String fileName, Integer position, GameActivity game) {
    super(fileName, position, game);
    type = Type.Touch;
  }

  public void initMinigame(Bitmap mBitmap, boolean wasGameSaved) {
    mHeight = mBitmap.getHeight();
    mWidth = mBitmap.getWidth();

    columnWidth = (mWidth) / NUMBER_OF_COLUMNS;
    mBallSize = mWidth / 40;
    catchingBallsHeight = mHeight - (mHeight / 15) - mBallSize;
    fallingStep =
        ((float) (mHeight - fallingHeight) / 180) * DebugSettings.GLOBAL_DIFFICULTY_COEFFICIENT;

    if (mGame.isTutorial()) {
      framesToGenerateNewBall /= DebugSettings.GLOBAL_DIFFICULTY_TUTORIAL_COEFFICIENT;
      fallingStep *= DebugSettings.GLOBAL_DIFFICULTY_TUTORIAL_COEFFICIENT;
    }

    mPaintFallingBalls = new PaintSerializable(colorMain, Paint.Style.FILL);
    mPaintCatchingBallActive = new PaintSerializable(colorAlt, Paint.Style.FILL);
    mPaintCatchingBallInactive = new PaintSerializable(colorAlt2, Paint.Style.STROKE);

    fallingHeight = mHeight / 20;

    //difficulty
    maxDifficulty = 1;

    countCatchingBallsPosition();
    isMinigameInitialized = true;
  }

  public void updateMinigame() {
    generateFallingBalls();
    moveObjects();
  }

  private void generateFallingBalls() {
    if (framesToGo == 0) {
      int position = RandomGenerator.getInstance().generateInt(0, NUMBER_OF_COLUMNS - 1);
      mFallingBalls.add(new FallingBall(mCatchingBalls[position], fallingHeight, position));
      framesToGo = framesToGenerateNewBall;
    }
    framesToGo--;
  }

  private void moveObjects() {
    for (FallingBall ball : mFallingBalls) {
      ball.fall();
      if (ball.isCatched()) {
        mFallingBalls.remove(ball);
        return;
      }
    }
  }

  public void onUserInteractedTouch(float x, float y) {
    activeBall = findColumnClicked(x);
  }

  public void drawMinigame(Canvas mCanvas) {
    for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
      if (i != activeBall) {
        mCanvas.drawCircle(mCatchingBalls[i], catchingBallsHeight, mBallSize,
            mPaintCatchingBallInactive.mPaint);
      } else {
        mCanvas.drawCircle(mCatchingBalls[i], catchingBallsHeight, mBallSize,
            mPaintCatchingBallActive.mPaint);
      }
    }

    for (FallingBall ball : mFallingBalls) {
      mCanvas.drawCircle(ball.xAxis, Math.round(ball.yAxis), mBallSize, mPaintFallingBalls.mPaint);
    }

  }

  private void countCatchingBallsPosition() {
    int centerOfColumn = columnWidth / 2;

    for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
      mCatchingBalls[i] = centerOfColumn + i * columnWidth;
    }

  }

  int findColumnClicked(float x) {
    if (x > 0 && x < mWidth) {
      return (int) (x / columnWidth);
    } else {
      return activeBall;
    }
  }

  @Override
  public void onDifficultyIncreased() {
    int difficultyStep =
        (framesToGenerateNewBall / 100) * DebugSettings.GLOBAL_DIFFICULTY_INCREASE_COEFFICIENT;

    if (difficultyStep < 1) {
      difficultyStep = 1;
    }

    if (framesToGenerateNewBall > maxDifficulty) {
      framesToGenerateNewBall -= difficultyStep;
    }
  }

  @Override
  public void reskinLocally(SkinManager.Skin currentSkin) {

  }

  @Override
  public String getDescription(Context context) {
    return context.getString(R.string.minigames_TCatcher);
  }

  public String getName() {
    return "Catcher";

  }

  private class FallingBall implements Serializable {
    final int xAxis;
    float yAxis;
    final int mColumn;

    public FallingBall(int xAxis, float yAxis, int column) {
      this.xAxis = xAxis;
      this.yAxis = yAxis;
      this.mColumn = column;
    }

    private void fall() {
      yAxis += fallingStep;
    }

    private boolean isCatched() {

      if (yAxis + mBallSize + 1 > mHeight) {
        if(mGame != null){
          mGame.onGameLost(mPosition);
        }
      } else {
        //collision scenario
        if (mColumn == activeBall) {
          if (yAxis + mBallSize <= catchingBallsHeight + mBallSize &&
              yAxis + mBallSize >= catchingBallsHeight - mBallSize) {
            return true;
          }
          if (yAxis - mBallSize <= catchingBallsHeight + mBallSize &&
              yAxis - mBallSize >= catchingBallsHeight - mBallSize) {
            return true;
          }
        }
      }
      return false;
    }
  }
}