package sk.palistudios.multigame.game.minigames;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
public class MiniGameVBouncer extends AMiniGame implements IMiniGameVertical {

  PointSerializable mPointBall = null;
  PaintSerializable mPaintBall = null;
  PaintSerializable mPaintBar = null;
  float movementSensitivity;
  float velocityX;
  float velocityY;
  float difficultyStepX;
  float difficultyStepY;
  float maxDifficulty;
  private int ballSize;
  private int barHeight;
  private int barTop;
  private int barLeft;
  private int barBottom;
  private int barRight;
  private int barWidth;
  private float actualMovement = 0;

  public MiniGameVBouncer(String fileName, Integer position, GameActivity game) {
    super(fileName, position, game);
    type = Type.Vertical;
  }

  public void updateMinigame() {
    moveBall();
    moveBar();

  }

  public void initMinigame(Bitmap mBitmap, boolean wasGameSaved) {

    mHeight = mBitmap.getHeight();
    mWidth = mBitmap.getWidth();

    ballSize = mHeight / 20;
    barHeight = mHeight / 3;
    movementSensitivity = (float) mHeight / 100;

    if (!wasGameSaved) {
      barTop = mHeight / 2 - barHeight / 2;
      barBottom = mHeight / 2 + barHeight / 2;

      mPointBall = new PointSerializable(30, 30);
      velocityX = (float) mWidth / 200;
      velocityY = RandomGenerator.getInstance().generateFloat(1, 1.2f);

      //difficulty
      maxDifficulty = mWidth / 10;
      //            difficultyStepX = (maxDifficulty - velocityX) / 20;
      //            difficultyStepY = (difficultyStepX / velocityX) * velocityY;

    }

    barLeft = 0;
    barWidth = mWidth / 40;
    barRight = barLeft + barWidth;

    mPaintBall = new PaintSerializable(colorMain, Paint.Style.FILL);

    mPaintBar = new PaintSerializable(colorAlt, Paint.Style.FILL);

    isMinigameInitialized = true;
  }

  public void drawMinigame(Canvas mCanvas) {
    mCanvas.drawCircle(mPointBall.mPoint.x, mPointBall.mPoint.y, ballSize, mPaintBall.mPaint);
    mCanvas.drawRect(barLeft, barTop, barRight, barBottom, mPaintBar.mPaint);
  }

  public void onUserInteracted(float movement) {

    if (mWidth == 0 || mHeight == 0) {
      return;
    }

    movement *= movementSensitivity;

    actualMovement = movement;

  }
  //in order to escape deadlock, when ball is bouncing back and forth on the edge
  //    private int framesFromLastVerticalHit = -1;
  //    private int framesFromLastHorizontalHit = -1;

  private void moveBall() {

    mPointBall.mPoint.x += Math.round(velocityX);
    mPointBall.mPoint.y += Math.round(velocityY);

    if (mPointBall.mPoint.x - ballSize < 0) {
      mGame.onGameLost(mPosition);
      return;
    }

    //hitting the bar
    if (mPointBall.mPoint.x - ballSize <= barRight) {
      if (mPointBall.mPoint.y - ballSize < barBottom && mPointBall.mPoint.y + ballSize > barTop) {
        //                if (framesFromLastVerticalHit != 0) {
        velocityX = Math.abs(velocityX);
        //                    framesFromLastVerticalHit = 1;
        //                }
      }
    }

    //up
    if (mPointBall.mPoint.y - ballSize <= 0) {
      //            if (framesFromLastHorizontalHit != 0) {
      velocityY = Math.abs(velocityY);
      //                framesFromLastHorizontalHit = 1;
      //            }
    }

    //down
    if (mPointBall.mPoint.y + ballSize >= mHeight) {
      //            if (framesFromLastHorizontalHit != 0) {
      velocityY = -Math.abs(velocityY);
      //                framesFromLastHorizontalHit = 1;
      //            }
    }

    //right
    if (mPointBall.mPoint.x + ballSize >= mWidth) {
      //            if (framesFromLastVerticalHit != 0) {
      velocityX = -Math.abs(velocityX);
      //                framesFromLastVerticalHit = 1;
      //            }
    }
    //        framesFromLastHorizontalHit--;
    //        framesFromLastVerticalHit--;
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

  @Override
  public void onDifficultyIncreased() {
    difficultyStepX = (Math.abs(velocityX) / 100) * DebugSettings.globalDifficultyCoeficient;
    difficultyStepY = (Math.abs(velocityY) / 100) * DebugSettings.globalDifficultyCoeficient;

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
  public String getDescription(Context context) {
    return context.getString(R.string.minigames_VBouncer);
  }

  public String getName() {
    return "Bouncer";
  }

  @Override
  public void setForTutorial() {
    //do nothing
  }

  @Override
  public void setForClassicGame() {
  }
}
