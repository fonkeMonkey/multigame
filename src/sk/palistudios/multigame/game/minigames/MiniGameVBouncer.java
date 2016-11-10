package sk.palistudios.multigame.game.minigames;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.persistence.PaintSerializable;
import sk.palistudios.multigame.game.persistence.PointSerializable;
import sk.palistudios.multigame.mainMenu.DebugSettings;
import sk.palistudios.multigame.tools.RandomGenerator;
import sk.palistudios.multigame.tools.SkinManager;

/**
 * @author Pali
 */
public class MiniGameVBouncer extends BaseMiniGame
    implements GameActivity.userInteractedVerticalListener {

  private static final int MAX_PATH_POINTS = 50;

  //Difficulty
  private float velocityX;
  private float velocityY;
  private float difficultyStepX;
  private float difficultyStepY;
  private float maxDifficulty;

  //Graphics
  private PointSerializable mPointBall = null;
  private PaintSerializable mPaintBall = null;
  private PaintSerializable mPaintBallCenter = null;
  private PaintSerializable mPaintBar = null;
  private float movementSensitivity;
  private int mBallSize, mBallSize1, mBallSize2, mBallSize3, mBallSize4;
  private int barHeight;
  private int barTop;
  private int barLeft;
  private int barBottom;
  private int barRight;
  private float actualMovement = 0;

  private List<PointSerializable> mBallPathHistory = new ArrayList<PointSerializable>();

  public MiniGameVBouncer(String fileName, Integer position, GameActivity game) {
    super(fileName, position, game);
    type = Type.Vertical;
  }

  @Override
  public void initMinigame() {
    mBallSize = mHeight / 20;
    mBallSize1 = (int) (mBallSize / 1.6);
    mBallSize2 = (int) (mBallSize / 2.4);
    mBallSize3 = (int) (mBallSize / 3.2);
    mBallSize4 = (int) (mBallSize / 4.0);

    barHeight = mHeight / 3;
    movementSensitivity = (float) mHeight / 100;

    if (!mWasgameSaved) {
      barTop = mHeight / 2 - barHeight / 2;
      barBottom = mHeight / 2 + barHeight / 2;

      mPointBall = new PointSerializable(30, 30);
      velocityX = ((float) mWidth / 200) * DebugSettings.GLOBAL_DIFFICULTY_COEFFICIENT;
      if (mGame.isTutorial()) {
        velocityX *= DebugSettings.GLOBAL_DIFFICULTY_TUTORIAL_COEFFICIENT;
      }
      velocityY = RandomGenerator.getInstance().generateFloat(1, 1.2f);

      //difficulty
      maxDifficulty = mWidth / 10;
    }

    barLeft = 0;
    int barWidth = mWidth / 40;
    barRight = barLeft + barWidth;

    mPaintBall = new PaintSerializable(mPrimaryColor, Paint.Style.FILL);
    mPaintBallCenter = new PaintSerializable(mSecondaryColor, Paint.Style.FILL);
    final int barColor = (mAlternateColor != 0) ? mAlternateColor : mPrimaryColor;
    mPaintBar = new PaintSerializable(barColor, Paint.Style.FILL);
    isMinigameInitialized = true;
  }

  public void updateMinigame() {
    moveBall();
    moveBar();
  }

  private void moveBall() {
    mPointBall.mPoint.x += Math.round(velocityX);
    mPointBall.mPoint.y += Math.round(velocityY);
    addBallCenterToHistory();

    if (mPointBall.mPoint.x - mBallSize < 0) {
      if(mGame != null){
        mGame.onGameLost(mPosition);
      }
      return;
    }

    //hitting the bar
    if (mPointBall.mPoint.x - mBallSize <= barRight) {
      if (mPointBall.mPoint.y - mBallSize < barBottom && mPointBall.mPoint.y + mBallSize > barTop) {
        velocityX = Math.abs(velocityX);
      }
    }

    //up
    if (mPointBall.mPoint.y - mBallSize <= 0) {
      velocityY = Math.abs(velocityY);
    }

    //down
    if (mPointBall.mPoint.y + mBallSize >= mHeight) {
      velocityY = -Math.abs(velocityY);
    }

    //right
    if (mPointBall.mPoint.x + mBallSize >= mWidth) {
      velocityX = -Math.abs(velocityX);
    }
  }

  private void addBallCenterToHistory() {
    mBallPathHistory.add(0, new PointSerializable(mPointBall.mPoint.x, mPointBall.mPoint.y));
    final int pathSize = mBallPathHistory.size();
    if(pathSize > MAX_PATH_POINTS) {
      mBallPathHistory.remove(pathSize - 1);
    }
  }

  private void moveBar() {
    if (barBottom + actualMovement <= mHeight && barTop + actualMovement >= 0) {
      barBottom += actualMovement;
      barTop += actualMovement;
    } else {
      if (barBottom + actualMovement >= mHeight) {
        barBottom = mHeight;
        barTop = mHeight - barHeight;
      }
      if (barTop + actualMovement <= 0) {
        barBottom = barHeight;
        barTop = 0;
      }
    }
  }

  public void drawMinigame(Canvas mCanvas) {
    if(mBackgroundColor != 0) {
      mCanvas.drawColor(mBackgroundColor);
    }
    mCanvas.drawCircle(mPointBall.mPoint.x, mPointBall.mPoint.y, mBallSize, mPaintBall.mPaint);
    mCanvas.drawCircle(mPointBall.mPoint.x, mPointBall.mPoint.y, (int) (mBallSize / 2.5),
        mPaintBallCenter.mPaint);
    drawTail(mCanvas);
    mCanvas.drawRect(barLeft, barTop, barRight, barBottom, mPaintBar.mPaint);
  }

  private void drawTail(Canvas canvas) {
    final PaintSerializable paint = new PaintSerializable(mPrimaryColor, Paint.Style.FILL);

    final int historySize = mBallPathHistory.size();
    PointSerializable point;
    if(historySize > 17) {
      paint.mPaint.setAlpha(128);
      point = mBallPathHistory.get(17);
      canvas.drawCircle(point.mPoint.x, point.mPoint.y, mBallSize1, paint.mPaint);
    }

    if(historySize > 28) {
      paint.mPaint.setAlpha(102);
      point = mBallPathHistory.get(28);
      canvas.drawCircle(point.mPoint.x, point.mPoint.y, mBallSize2, paint.mPaint);
    }

    if(historySize > 36) {
      paint.mPaint.setAlpha(77);
      point = mBallPathHistory.get(36);
      canvas.drawCircle(point.mPoint.x, point.mPoint.y, mBallSize3, paint.mPaint);
    }

    if(historySize > 42) {
      paint.mPaint.setAlpha(51);
      point = mBallPathHistory.get(42);
      canvas.drawCircle(point.mPoint.x, point.mPoint.y, mBallSize4, paint.mPaint);
    }
  }

  public void onUserInteractedVertical(float verticalMovement) {
    if (mWidth == 0 || mHeight == 0) {
      return;
    }
    verticalMovement *= movementSensitivity;
    actualMovement = verticalMovement;
  }

  @Override
  public void onDifficultyIncreased() {
    difficultyStepX = (Math.abs(velocityX) / 100) *
        DebugSettings.GLOBAL_DIFFICULTY_INCREASE_COEFFICIENT;
    difficultyStepY = (Math.abs(velocityY) / 100) *
        DebugSettings.GLOBAL_DIFFICULTY_INCREASE_COEFFICIENT;

    if (Math.abs(velocityX) < maxDifficulty - difficultyStepX) {
      if (velocityX > 0) {
        velocityX += difficultyStepX;
      } else {
        velocityX -= difficultyStepX;
      }
      if (velocityY > 0) {
        velocityY += difficultyStepY;
      } else {
        velocityY -= difficultyStepY;
      }
    }
  }

  @Override
  public void onMinigameLoaded() {
    
  }

  @Override
  public void reskinLocally(SkinManager.Skin currentSkin) {
    final Resources resources = mGame.getResources();
    switch (currentSkin) {
      case QUAD:
        mBackgroundColor = resources.getColor(R.color.game_bg_quad_vbouncer);
        mPrimaryColor = resources.getColor(R.color.quad_primary);
        mSecondaryColor = resources.getColor(R.color.quad_secondary);
        break;
      case THRESHOLD:
        mBackgroundColor = Color.TRANSPARENT;
        mPrimaryColor = resources.getColor(R.color.threshold_primary);
        mSecondaryColor = resources.getColor(R.color.threshold_vbouncer_secondary);
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
        mBackgroundColor = resources.getColor(R.color.game_bg_quad_vbouncer);
        mPrimaryColor = resources.getColor(R.color.quad_primary);
        mSecondaryColor = resources.getColor(R.color.quad_secondary);
        break;
    }
  }

  @Override
  public String getDescription(Context context) {
    return context.getString(R.string.minigames_VBouncer_description);
  }

  public String getName() {
    return "Bouncer";
  }
}
