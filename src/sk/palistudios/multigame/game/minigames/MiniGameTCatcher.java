package sk.palistudios.multigame.game.minigames;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.persistence.PaintSerializable;
import sk.palistudios.multigame.game.persistence.PathSerializable;
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
  private PaintSerializable mPaintFallingBallCenter = null;
  private PaintSerializable mPaintCatchingBallInactive = null;
  private PaintSerializable mPaintCatchingBallActive = null;
  private PaintSerializable mMaskPathPaint = null;
  private int activeBall = 4;
  private int mTmpActiveBall = -1;
  private int mBallSize, mBallSize1, mBallSize2, mBallSize3, mBallSize4;
  private int catchingBallsHeight;
  private int columnWidth;

  private PathSerializable mMaskPath;

  public MiniGameTCatcher(String fileName, Integer position, GameActivity game) {
    super(fileName, position, game);
    type = Type.Touch;
  }

  public void initMinigame(Bitmap mBitmap, boolean wasGameSaved) {
    mHeight = mBitmap.getHeight();
    mWidth = mBitmap.getWidth();

    columnWidth = (mWidth) / NUMBER_OF_COLUMNS;
    mBallSize = mWidth / 35;
    mBallSize1 = (int) (mBallSize / 1.6);
    mBallSize2 = (int) (mBallSize / 2.4);
    mBallSize3 = (int) (mBallSize / 3.2);
    mBallSize4 = (int) (mBallSize / 4.0);
    catchingBallsHeight = mHeight - (mHeight / 15) - mBallSize;
    fallingStep =
        ((float) (mHeight - fallingHeight) / 180) * DebugSettings.GLOBAL_DIFFICULTY_COEFFICIENT;

    if (mGame.isTutorial()) {
      framesToGenerateNewBall /= DebugSettings.GLOBAL_DIFFICULTY_TUTORIAL_COEFFICIENT;
      fallingStep *= DebugSettings.GLOBAL_DIFFICULTY_TUTORIAL_COEFFICIENT;
    }

    mPaintFallingBalls = new PaintSerializable(mPrimaryColor, Paint.Style.FILL);
    mPaintFallingBallCenter = new PaintSerializable(mSecondaryColor, Paint.Style.FILL);
    final int catchingBallsColor = (mAlternateColor != 0) ? mAlternateColor : mPrimaryColor;
    mPaintCatchingBallActive = new PaintSerializable(catchingBallsColor, Paint.Style.STROKE);
    mPaintCatchingBallInactive = new PaintSerializable(catchingBallsColor, Paint.Style.FILL);
    mMaskPathPaint = new PaintSerializable(mBackgroundColor);

    fallingHeight = mHeight / 20;

    //difficulty
    maxDifficulty = 1;

    countCatchingBallsPosition();

    initMaskPath();

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
    int lastBallCenter;
    for (FallingBall ball : mFallingBalls) {
      ball.fall();
      // posledna gulicka chvosta
      lastBallCenter = Math.round(
          ball.yAxis - (2 * mBallSize) - (2 * mBallSize1) - (2 * mBallSize2) -
              (2 * mBallSize3));
      if (ball.wasCatched) {
        mTmpActiveBall = ball.mColumn;
        if(lastBallCenter > mHeight) {
          mFallingBalls.remove(ball);
          mTmpActiveBall = -1;
          return;
        }
      }
    }
  }

  public void onUserInteractedTouch(float x, float y) {
    activeBall = findColumnClicked(x);
  }

  public void drawMinigame(Canvas mCanvas) {
    mCanvas.drawColor(mBackgroundColor);

    if(mMaskPath == null) {
      initMaskPath();
    }

    for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
      float left = mCatchingBalls[i] - mBallSize * 2;
      float right = left + (mBallSize * 4);
      int top = catchingBallsHeight - mBallSize;
      int bottom = catchingBallsHeight + mBallSize;
      RectF rectF = new RectF(left, top, right, bottom);
      if (i == activeBall || i == mTmpActiveBall) {
        mCanvas.drawOval(rectF, mPaintCatchingBallActive.mPaint);
      } else {
        mCanvas.drawOval(rectF, mPaintCatchingBallInactive.mPaint);
      }
    }

    Paint paint = mPaintFallingBalls.mPaint;
    int ballCenter;
    for (FallingBall ball : mFallingBalls) {
      paint.setAlpha(255); // 100%
      ballCenter = Math.round(ball.yAxis);
      mCanvas.drawCircle(ball.xAxis, ballCenter, mBallSize, mPaintFallingBalls.mPaint);
      mCanvas.drawCircle(ball.xAxis, ballCenter, (int) (mBallSize/ 2.5),
          mPaintFallingBallCenter.mPaint);
      paint.setAlpha(128); // 50%
      ballCenter = Math.round(ball.yAxis - (2 * mBallSize));
      mCanvas.drawCircle(ball.xAxis, ballCenter, mBallSize1, paint);
      paint.setAlpha(102); // 40%
      ballCenter = Math.round(ball.yAxis - (2 * mBallSize) - (2 * mBallSize1));
      mCanvas.drawCircle(ball.xAxis, ballCenter, mBallSize2, paint);
      paint.setAlpha(77); // 30%
      ballCenter = Math.round(ball.yAxis - (2 * mBallSize) - (2 * mBallSize1) - (2 * mBallSize2));
      mCanvas.drawCircle(ball.xAxis, ballCenter, mBallSize3, paint);
      paint.setAlpha(51); // 20%
      ballCenter = Math.round(ball.yAxis - (2 * mBallSize) - (2 * mBallSize1) - (2 * mBallSize2) -
          (2 * mBallSize3));
      mCanvas.drawCircle(ball.xAxis, ballCenter, mBallSize4, paint);
    }

    mCanvas.drawPath(mMaskPath, mMaskPathPaint.mPaint);
  }

  private void initMaskPath() {
    if(mBackgroundColor == Color.TRANSPARENT || mBackgroundColor == 0) {
      mMaskPathPaint.mPaint.setColor(Color.TRANSPARENT);
      mMaskPathPaint.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    } else {
      mMaskPathPaint.mPaint.setColor(mBackgroundColor);
    }
    mMaskPath = new PathSerializable();
    mMaskPath.setFillType(Path.FillType.EVEN_ODD);
    mMaskPath.moveTo(mWidth, mHeight);
    mMaskPath.lineTo(0, mHeight);
    mMaskPath.lineTo(0, catchingBallsHeight);

    for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
      float left = (mCatchingBalls[i] - mBallSize * 2) - 1;
      float right = left + (mBallSize * 4) + 1;
      int top = catchingBallsHeight - mBallSize - 1;
      int bottom = catchingBallsHeight + mBallSize + 1;
      RectF rectF = new RectF(left, top, right, bottom);
      mMaskPath.lineTo(left, catchingBallsHeight);
      mMaskPath.arcTo(rectF, 0f, 180f);
    }
    mMaskPath.lineTo(mWidth, catchingBallsHeight);
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
    final Resources resources = mGame.getResources();
    switch (currentSkin) {
      case QUAD:
        mBackgroundColor = resources.getColor(R.color.game_bg_quad_tcatcher);
        mPrimaryColor = resources.getColor(R.color.quad_primary);
        mSecondaryColor = resources.getColor(R.color.quad_secondary);
        break;
      case THRESHOLD:
        mBackgroundColor = Color.TRANSPARENT;
        mPrimaryColor = resources.getColor(R.color.threshold_primary);
        mSecondaryColor = resources.getColor(R.color.threshold_tcatcher_secondary);
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
        mBackgroundColor = resources.getColor(R.color.game_bg_quad_tcatcher);
        mPrimaryColor = resources.getColor(R.color.quad_primary);
        mSecondaryColor = resources.getColor(R.color.quad_secondary);
        break;
    }
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
    boolean wasCatched;

    public FallingBall(int xAxis, float yAxis, int column) {
      this.xAxis = xAxis;
      this.yAxis = yAxis;
      this.mColumn = column;
    }

    private void fall() {
      yAxis += fallingStep;
      isCatched();
    }

    private boolean isCatched() {

      if ((mColumn != activeBall) && (yAxis + mBallSize + 1 > catchingBallsHeight) && !wasCatched) {
        if(mGame != null){
          mGame.onGameLost(mPosition);
        }
      } else {
        //collision scenario
        if (mColumn == activeBall) {
          if (yAxis + mBallSize <= catchingBallsHeight + mBallSize &&
              yAxis + mBallSize >= catchingBallsHeight - mBallSize) {
            wasCatched = true;
            return true;
          }
          if (yAxis - mBallSize <= catchingBallsHeight + mBallSize &&
              yAxis - mBallSize >= catchingBallsHeight - mBallSize) {
            wasCatched = true;
            return true;
          }
        }
      }
      return false;
    }
  }
}