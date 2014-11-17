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
public class MiniGameHBalance extends BaseMiniGame implements
    GameActivity.userInteractedHorizontalListener {
  //DIFFICULTY
  private int framestoRandomLeverMovement =
      (int) (60 / DebugSettings.GLOBAL_DIFFICULTY_COEFFICIENT);
  private float maxSpeed;
  private float maxLean;
  private float maxDifficulty;

  //GRAPHICS
  private int mBallSize;
  private PaintSerializable mPaintBallColor = null;
  private PaintSerializable mPaintBarColor = null;
  private int splitHeight;
  private float movementSensitivity;
  private int leanRatio;
  private PointSerializable pointBarLeftEdge;
  private PointSerializable pointBarRightEdge;
  private float lean;
  private PointSerializable pBallCenter;
  private float momentalBallSpeed;
  private int overEdgeToLose;
  private float ballXAxis;
  private PointSerializable pVector;
  private PointSerializable normalVector;

  protected MiniGameHBalance(String fileName, Integer position, GameActivity game) {
    super(fileName, position, game);
    type = Type.Horizontal;
  }

  public void initMinigame(Bitmap mBitmap, boolean wasGameSaved) {
    mHeight = mBitmap.getHeight();
    mWidth = mBitmap.getWidth();

    splitHeight = (mHeight / 2);
    mBallSize = mWidth / 30;
    maxSpeed = (float) (mWidth) / 500;
    maxLean = mWidth / 20;
    leanRatio = 150;
    movementSensitivity = maxLean / 5;
    setDifficultyForClassicGame();

    if (!wasGameSaved) {
      int barLength = mWidth / 2;
      int barLeftEdgeX = (mWidth - barLength) / 2;
      int barRightEdgeX = (mWidth - barLength) / 2 + barLength;
      pointBarLeftEdge = new PointSerializable(barLeftEdgeX, splitHeight);
      pointBarRightEdge = new PointSerializable(barRightEdgeX, splitHeight);
      pBallCenter = new PointSerializable(mWidth / 2, splitHeight - mBallSize);
      ballXAxis = mWidth / 2;

      //difficulty
      int DIF_SHORTEST_BAR = 4;
      maxDifficulty = DIF_SHORTEST_BAR;
      //because in onDifficultyIncreased you decrease from both sides
    }
    mPaintBallColor = new PaintSerializable(colorMain, Paint.Style.FILL);
    mPaintBarColor = new PaintSerializable(colorAlt, Paint.Style.STROKE);

    mPaintBarColor.setStrokeWidth(mWidth / 50);

    overEdgeToLose = mBallSize / 3 * 2;

    isMinigameInitialized = true;

    pVector = new PointSerializable(pointBarRightEdge.mPoint.x - pointBarLeftEdge.mPoint.x,
        pointBarRightEdge.mPoint.y - pointBarLeftEdge.mPoint.y);
    normalVector = new PointSerializable(pVector.mPoint.y, -pVector.mPoint.x);
  }

  public void updateMinigame() {

    //for random movement of the bar
    if (framestoRandomLeverMovement == 0) {
      onUserInteractedHorizontal(RandomGenerator.getInstance().generateFloat(-0.5f, 0.5f));
      int DIF_FRAMES_TO_RANDOM = 160;
      framestoRandomLeverMovement = DIF_FRAMES_TO_RANDOM;
    }
    framestoRandomLeverMovement--;

    //BAR
    pointBarLeftEdge.mPoint.y = Math.round(splitHeight - lean);
    pointBarRightEdge.mPoint.y = Math.round(splitHeight + lean);

    float speedDelta = lean / leanRatio;

    //BALL
    if (momentalBallSpeed + speedDelta >= -maxSpeed && momentalBallSpeed + speedDelta <= maxSpeed) {
      momentalBallSpeed += speedDelta;
    } else {
      if (momentalBallSpeed + speedDelta >= maxSpeed) {
        momentalBallSpeed = maxSpeed;
      } else {
        momentalBallSpeed = -maxSpeed;
      }
    }

    ballXAxis += momentalBallSpeed;
    pBallCenter.mPoint.x = Math.round(ballXAxis);
    pBallCenter.mPoint.y = findOnLine(pointBarLeftEdge, pointBarRightEdge, pBallCenter.mPoint.x) -
        mBallSize;
    if (pBallCenter.mPoint.x + overEdgeToLose < pointBarLeftEdge.mPoint.x ||
        pBallCenter.mPoint.x - overEdgeToLose > pointBarRightEdge.mPoint.x) {
      mGame.onGameLost(mPosition);
    }
  }


  int findOnLine(PointSerializable pBarLeftEdge, PointSerializable pBarRightEdge, int xAxisBall) {
    pVector.mPoint.x = pBarRightEdge.mPoint.x - pBarLeftEdge.mPoint.x;
    pVector.mPoint.y = pBarRightEdge.mPoint.y - pBarLeftEdge.mPoint.y;
    normalVector.mPoint.x = pVector.mPoint.y;
    normalVector.mPoint.y = -pVector.mPoint.x;

    int c = -(normalVector.mPoint.x * pBarLeftEdge.mPoint.x +
        normalVector.mPoint.y * pBarLeftEdge.mPoint.y);
    return ((-c - (normalVector.mPoint.x * xAxisBall)) / normalVector.mPoint.y);
  }

  public void drawMinigame(Canvas canvas) {
    canvas.drawLine(pointBarLeftEdge.mPoint.x, pointBarLeftEdge.mPoint.y,
        pointBarRightEdge.mPoint.x, pointBarRightEdge.mPoint.y, mPaintBarColor.mPaint);
    canvas.drawCircle(pBallCenter.mPoint.x, pBallCenter.mPoint.y, mBallSize,
        mPaintBallColor.mPaint);
  }

  public void onUserInteractedHorizontal(float horizontalMovement) {
    if (mWidth == 0 || mHeight == 0) {
      return;
    }

    //for the ball not to go uphills and be more sensitivite to user inputs
    horizontalMovement *= movementSensitivity;

    if (horizontalMovement > -maxLean && horizontalMovement < maxLean) {
      lean = horizontalMovement;
    }
  }

  @Override
  public void onDifficultyIncreased() {
    int barWidth = pointBarRightEdge.mPoint.x - pointBarLeftEdge.mPoint.x;

    float difficultyStep = ((pointBarRightEdge.mPoint.x - pointBarLeftEdge.mPoint.x) / 100) *
        (DebugSettings.GLOBAL_DIFFICULTY_INCREASE_COEFFICIENT / 2);

    if (difficultyStep < 1) {
      difficultyStep = 1;
    }

    if (barWidth >= maxDifficulty) {
      pointBarLeftEdge.mPoint.x += difficultyStep;
      pointBarRightEdge.mPoint.x -= difficultyStep;

      pointBarLeftEdge.mPoint.y = findOnLine(pointBarLeftEdge, pointBarRightEdge,
          pointBarLeftEdge.mPoint.x);
      pointBarRightEdge.mPoint.y = findOnLine(pointBarLeftEdge, pointBarRightEdge,
          pointBarRightEdge.mPoint.x);
    }

    maxSpeed *= 1.05f;
  }

  @Override
  public String getDescription(Context context) {
    return context.getString(R.string.minigames_HBalance);
  }

  public String getName() {
    return "Balance";
  }

  @Override
  public void setDifficultyForTutorial() {
    framestoRandomLeverMovement = (int) (framestoRandomLeverMovement * 1.4);
    //        maxLean /= 2;
    //        maxSpeed /= 1.6;
  }

  @Override
  public void setDifficultyForClassicGame() {
    framestoRandomLeverMovement = (int) (framestoRandomLeverMovement * 1.3);
    //        maxLean /= 2;
    //        maxSpeed /= 1.6;
    //        maxSpeed /= 0;

  }
}
