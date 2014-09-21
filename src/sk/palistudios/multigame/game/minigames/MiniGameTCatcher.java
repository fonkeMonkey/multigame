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
import sk.palistudios.multigame.mainMenu.DebugSettings;
import sk.palistudios.multigame.tools.RandomGenerator;

/**
 * @author Pali
 */
public class MiniGameTCatcher extends AMiniGame implements IMiniGameTouch {

  int numberOfColumns = 7;
  int[] mCatchingBalls = new int[numberOfColumns];
  ArrayList<FallingBall> mFallingBalls = new ArrayList<FallingBall>();
  PaintSerializable mPaintFallingBalls = null;
  PaintSerializable mPaintCatchingBallInactive = null;
  PaintSerializable mPaintCatchingBallActive = null;
  float fallingStep;
  private int activeBall = 4;
  private int mBallSize;
  private int catchingBallsHeight;
  private int columnWidth;
  private int fallingHeight;
  private int maxDifficulty;
  private int difficultyStep;
  private int framesToGenerateNewBall = 160;
  private int framesToGo = 30;

  public MiniGameTCatcher(String fileName, Integer position, GameActivity game) {
    super(fileName, position, game);
    type = Typ.Touch;
  }

  public void updateMinigame() {
    generateFallingBalls();
    moveObjects();

  }

  public void initMinigame(Bitmap mBitmap, boolean wasGameSaved) {

    mHeight = mBitmap.getHeight();
    mWidth = mBitmap.getWidth();

    columnWidth = (mWidth) / numberOfColumns;
    mBallSize = mWidth / 40;
    catchingBallsHeight = mHeight - (mHeight / 15) - mBallSize;
    fallingStep = (float) (mHeight - fallingHeight) / 180;

    mPaintFallingBalls = new PaintSerializable(colorMain, Paint.Style.FILL);
    mPaintCatchingBallActive = new PaintSerializable(colorAlt, Paint.Style.FILL);
    mPaintCatchingBallInactive = new PaintSerializable(colorAlt2, Paint.Style.STROKE);

    fallingHeight = mHeight / 20;

    //difficulty
    maxDifficulty = 1;
    //        difficultyStep = 8;

    countCatchingBallsPosition();
    isMinigameInitialized = true;
  }

  private void generateFallingBalls() {
    if (framesToGo == 0) {
      int position = RandomGenerator.getInstance().generateInt(0, numberOfColumns - 1);

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
        ball = null;
        return;
      }
    }

  }

  public void onUserInteracted(float x, float y) {
    activeBall = findColumnClicked(x);
  }

  public void drawMinigame(Canvas mCanvas) {

    for (int i = 0; i < numberOfColumns; i++) {
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

    for (int i = 0; i < numberOfColumns; i++) {
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
    difficultyStep = (framesToGenerateNewBall / 100) * DebugSettings.globalDifficultyCoeficient;

    if (difficultyStep < 1) {
      difficultyStep = 1;
    }

    if (framesToGenerateNewBall > maxDifficulty) {
      framesToGenerateNewBall -= difficultyStep;
    }
  }

  @Override
  public String getDescription(Context context) {
    return context.getString(R.string.minigames_TCatcher);
    //        return null;
  }

  public String getName() {
    return "Catcher";

  }

  @Override
  public void setForTutorial() {
    framesToGenerateNewBall = (int) (framesToGenerateNewBall * 1.4);
  }

  @Override
  public void setForClassicGame() {
    framesToGenerateNewBall = (int) (framesToGenerateNewBall * 1.2);
  }

  private class FallingBall implements Serializable {

    int xAxis;
    float yAxis;
    int mColumn;

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
        mGame.onGameLost(mPosition);
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