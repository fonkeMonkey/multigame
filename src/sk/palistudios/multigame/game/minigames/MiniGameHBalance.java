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
public class MiniGameHBalance extends AMiniGame implements IMiniGameHorizontal {

  int framestoRandomLeverMovement = (int) (60 / DebugSettings.GLOBAL_DIFFICULTY_COEFFICIENT);
  float maxSpeed;
  float maxLean;
  float difficultyStep;
  float maxDifficulty;
  float speedDelta;
  PointSerializable[] mBar;
  int mBallSize;
  PaintSerializable mPaintBallColor = null;
  PaintSerializable mPaintBarColor = null;
  int splitHeight;
  float movementSensitivity;
  int leanRatio;
  int barLength;
  int barLeftEdgeX;
  int barRightEdgeX;
  PointSerializable pointBarLeftEdge;
  PointSerializable pointBarRightEdge;
  float lean;
  PointSerializable pBallCenter;
  float momentalBallSpeed;
  int overEdgeToLose;
  float ballXAxis;
  float ballYAxis;
  int barWidth;
  //Difficulty settings
  private int DIF_FRAMES_TO_RANDOM = 160;
  private int DIF_SHORTEST_BAR = 4;

  public MiniGameHBalance(String fileName, Integer position, GameActivity game) {
    super(fileName, position, game);
    type = Type.Horizontal;
  }

  public void initMinigame(Bitmap mBitmap, boolean wasGameSaved) {

    mHeight = mBitmap.getHeight();
    mWidth = mBitmap.getWidth();

    splitHeight = (int) (mHeight / 2);
    mBallSize = mWidth / 30;
    maxSpeed = (float) (mWidth) / 500;
    //        maxSpeed /= 1.6;
    maxLean = mWidth / 20;
    leanRatio = 150;
    movementSensitivity = maxLean / 5;
    setForClassicGame();

    if (!wasGameSaved) {
      barLength = mWidth / 2;
      barLeftEdgeX = (mWidth - barLength) / 2;
      barRightEdgeX = (mWidth - barLength) / 2 + barLength;
      pointBarLeftEdge = new PointSerializable(barLeftEdgeX, splitHeight);
      pointBarRightEdge = new PointSerializable(barRightEdgeX, splitHeight);
      pBallCenter = new PointSerializable(mWidth / 2, splitHeight - mBallSize);
      ballXAxis = mWidth / 2;

      //difficulty
      maxDifficulty = DIF_SHORTEST_BAR;
      //because in onDifficultyIncreased you decrease from both sides
      //            difficultyStep = (barLength - maxDifficulty) / 40;
    }
    mPaintBallColor = new PaintSerializable(colorMain, Paint.Style.FILL);
    mPaintBarColor = new PaintSerializable(colorAlt, Paint.Style.STROKE);

    mPaintBarColor.setStrokeWidth(mWidth / 50);

    overEdgeToLose = mBallSize / 3 * 2;

    isMinigameInitialized = true;

    pVector = new PointSerializable(pointBarRightEdge.mPoint.x - pointBarLeftEdge.mPoint.x,
        pointBarRightEdge.mPoint.y - pointBarLeftEdge.mPoint.y);
    normalVector = new PointSerializable(pVector.mPoint.y,-pVector.mPoint.x);
  }

  public void updateMinigame() {

    //for random movement of the bar
    if (framestoRandomLeverMovement == 0) {
      onUserInteracted(RandomGenerator.getInstance().generateFloat(-0.5f, 0.5f));
      framestoRandomLeverMovement = DIF_FRAMES_TO_RANDOM;
    }
    framestoRandomLeverMovement--;

    //BAR
    pointBarLeftEdge.mPoint.y = Math.round(splitHeight - lean);
    pointBarRightEdge.mPoint.y = Math.round(splitHeight + lean);

    speedDelta = lean / leanRatio;
    //        Log.i("Max speed: " + String.valueOf(maxSpeed));
    //        Log.i("Speed delta: " + String.valueOf(speedDelta));
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

    //        Log.i("Momental ball speed: " + String.valueOf(momentalBallSpeed));

    ballXAxis += momentalBallSpeed;
    //        Log.i("X axis: " + String.valueOf(ballXAxis));
    pBallCenter.mPoint.x = Math.round(ballXAxis);
    pBallCenter.mPoint.y = findOnLine(pointBarLeftEdge, pointBarRightEdge, pBallCenter.mPoint.x) -
        mBallSize;
    if (pBallCenter.mPoint.x + overEdgeToLose < pointBarLeftEdge.mPoint.x ||
        pBallCenter.mPoint.x - overEdgeToLose > pointBarRightEdge.mPoint.x) {
      if(mGame != null){
        mGame.onGameLost(mPosition);
      }
    }
  }

  PointSerializable pVector;
  PointSerializable normalVector;
  int findOnLine(PointSerializable pBarLeftEdge, PointSerializable pBarRightEdge, int xAxisBall) {
    pVector.mPoint.x =
        pBarRightEdge.mPoint.x - pBarLeftEdge.mPoint.x;
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

  public void onUserInteracted(float movement) {
    if (mWidth == 0 || mHeight == 0) {
      return;
    }

    //for the ball not to go uphills and be more sensitivite to user inputs
    movement *= movementSensitivity;

    if (movement > -maxLean && movement < maxLean) {
      lean = movement;
    }
  }



  @Override
  public void onDifficultyIncreased() {
    barWidth = pointBarRightEdge.mPoint.x - pointBarLeftEdge.mPoint.x;

    difficultyStep = ((pointBarRightEdge.mPoint.x - pointBarLeftEdge.mPoint.x) / 100) *
        (DebugSettings.GLOBAL_DIFFICULTY_INCREASE_COEFFICIENT / 2);//because I trim from both sides

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
    //        return context.getText(R.string.minigames_HBalance);
    return context.getString(R.string.minigames_HBalance);
  }

  public String getName() {
    return "Balance";
  }

  @Override
  public void setForTutorial() {
    framestoRandomLeverMovement = (int) (framestoRandomLeverMovement * 1.4);
    //        maxLean /= 2;
    //        maxSpeed /= 1.6;
  }

  @Override
  public void setForClassicGame() {
    framestoRandomLeverMovement = (int) (framestoRandomLeverMovement * 1.3);
    //        maxLean /= 2;
    //        maxSpeed /= 1.6;
    //        maxSpeed /= 0;

  }
}
