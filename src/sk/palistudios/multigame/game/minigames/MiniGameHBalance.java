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
public class MiniGameHBalance extends BaseMiniGame implements
    GameActivity.userInteractedHorizontalListener {

  private static final int MAX_PATH_POINTS = 30;

  //DIFFICULTY
  private int framesToGo =
      (int) (60 / DebugSettings.GLOBAL_DIFFICULTY_COEFFICIENT);
  private int mFramesToRandomLeverageMovement =
      (int) (180 / DebugSettings.GLOBAL_DIFFICULTY_COEFFICIENT);
  private float maxSpeed;
  private float maxLean;
  private float maxDifficulty;

  //GRAPHICS
  private int mBallSize;
  private PaintSerializable mPaintBallColor = null;
  private PaintSerializable mPaintBallCenterColor = null;
  private PaintSerializable mPaintBarColor = null;
  private int splitWidth, splitHeight;
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

  private int mBarThickness;

  private List<PointSerializable> mBallPathHistory = new ArrayList<PointSerializable>();
  private transient RandomGenerator mRandomGenerator;
  private PaintSerializable mPaintHistory;

  protected MiniGameHBalance(String fileName, Integer position, GameActivity game) {
    super(fileName, position, game);
    type = Type.Horizontal;
    mRandomGenerator = RandomGenerator.getInstance();
  }

  @Override
  public void initMinigame() {
    if (mGame.isTutorial()) {
      mFramesToRandomLeverageMovement /= DebugSettings.GLOBAL_DIFFICULTY_TUTORIAL_COEFFICIENT;
    }

    splitHeight = (mHeight / 2);
    splitWidth = (mWidth / 2);
    mBallSize = mWidth / 30;
    maxSpeed = (float) (mWidth) / 500;
    maxLean = mWidth / 20;
    leanRatio = 150;
    movementSensitivity = maxLean / 5;

    if (!mWasgameSaved) {
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

    mPaintBallColor = new PaintSerializable(mPrimaryColor, Paint.Style.FILL);
    mPaintBallCenterColor = new PaintSerializable(mSecondaryColor, Paint.Style.FILL);
    mPaintHistory = new PaintSerializable(mPrimaryColor, Paint.Style.FILL);
    final int barColor = (mAlternateColor != 0) ? mAlternateColor : mPrimaryColor;
    mPaintBarColor = new PaintSerializable(barColor, Paint.Style.STROKE);

    mBarThickness = mWidth / 50;
    mPaintBarColor.setStrokeWidth(mBarThickness);

    overEdgeToLose = mBallSize / 3 * 2;

    isMinigameInitialized = true;

    pVector = new PointSerializable(pointBarRightEdge.mPoint.x - pointBarLeftEdge.mPoint.x,
        pointBarRightEdge.mPoint.y - pointBarLeftEdge.mPoint.y);
    normalVector = new PointSerializable(pVector.mPoint.y, -pVector.mPoint.x);
  }

  public void updateMinigame() {
    //for random movement of the bar
    if (framesToGo == 0) {
      onUserInteractedHorizontal(mRandomGenerator.generateFloat(-0.5f, 0.5f));
      framesToGo = mFramesToRandomLeverageMovement;
    }
    framesToGo--;

    //BAR
    pointBarLeftEdge.mPoint.y = Math.round(splitHeight - lean);
    pointBarRightEdge.mPoint.y = Math.round(splitHeight + lean);


    //BALL
    float speedDelta = lean / leanRatio;
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

    addBallCenterToHistory();

    if (pBallCenter.mPoint.x + overEdgeToLose < pointBarLeftEdge.mPoint.x ||
        pBallCenter.mPoint.x - overEdgeToLose > pointBarRightEdge.mPoint.x) {
      if(mGame != null){
        mGame.onGameLost(mPosition);
      }
    }
  }

  private void addBallCenterToHistory() {
    mBallPathHistory.add(0, new PointSerializable(pBallCenter.mPoint.x, pBallCenter.mPoint.y));
    final int pathSize = mBallPathHistory.size();
    if(pathSize > MAX_PATH_POINTS) {
      mBallPathHistory.remove(pathSize - 1);
    }
  }

  private int findOnLine(PointSerializable pBarLeftEdge, PointSerializable pBarRightEdge, int
      xAxisBall) {
    pVector.mPoint.x = pBarRightEdge.mPoint.x - pBarLeftEdge.mPoint.x;
    pVector.mPoint.y = pBarRightEdge.mPoint.y - pBarLeftEdge.mPoint.y;
    normalVector.mPoint.x = pVector.mPoint.y;
    normalVector.mPoint.y = -pVector.mPoint.x;

    int c = -(normalVector.mPoint.x * pBarLeftEdge.mPoint.x +
        normalVector.mPoint.y * pBarLeftEdge.mPoint.y);
    return ((-c - (normalVector.mPoint.x * xAxisBall)) / normalVector.mPoint.y);
  }

  public void drawMinigame(Canvas canvas) {
    if(mBackgroundColor != 0) {
      canvas.drawColor(mBackgroundColor);
    }
    canvas.drawLine(pointBarLeftEdge.mPoint.x, pointBarLeftEdge.mPoint.y,
        pointBarRightEdge.mPoint.x, pointBarRightEdge.mPoint.y, mPaintBarColor.mPaint);

    canvas.drawCircle(splitWidth, splitHeight, (int) (mBarThickness / 3.0), mPaintBallCenterColor
        .mPaint);

    drawMotionBlur(canvas);

    canvas.drawCircle(pBallCenter.mPoint.x, pBallCenter.mPoint.y, mBallSize,
        mPaintBallColor.mPaint);
    canvas.drawCircle(pBallCenter.mPoint.x, pBallCenter.mPoint.y, (int) (mBallSize / 2.5),
        mPaintBallCenterColor.mPaint);
  }

  private void drawMotionBlur(Canvas canvas) {
    PointSerializable point;
    mPaintHistory.mPaint.setAlpha(51); //20%
    if (mBallPathHistory.size() > 29) {
      point = mBallPathHistory.get(29);
      canvas.drawCircle(point.mPoint.x, point.mPoint.y, mBallSize, mPaintHistory.mPaint);
    }

    mPaintHistory.mPaint.setAlpha(90); //35%
    if (mBallPathHistory.size() > 14) {
      point = mBallPathHistory.get(14);
      canvas.drawCircle(point.mPoint.x, point.mPoint.y, mBallSize, mPaintHistory.mPaint);
    }

    mPaintHistory.mPaint.setAlpha(128); //50%
    if (mBallPathHistory.size() > 4) {
      point = mBallPathHistory.get(4);
      canvas.drawCircle(point.mPoint.x, point.mPoint.y, mBallSize, mPaintHistory.mPaint);
    }
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
  public void onMinigameLoaded() {
    mRandomGenerator = new RandomGenerator();
  }

  @Override
  public void reskinLocally(SkinManager.Skin currentSkin) {
    final Resources resources = mGame.getResources();
    switch (currentSkin) {
      case QUAD:
        mBackgroundColor = resources.getColor(R.color.game_bg_quad_hbalance);
        mPrimaryColor = resources.getColor(R.color.quad_primary);
        mSecondaryColor = resources.getColor(R.color.quad_secondary);
        break;
      case THRESHOLD:
        mBackgroundColor = Color.TRANSPARENT;
        mPrimaryColor = resources.getColor(R.color.threshold_primary);
        mSecondaryColor = resources.getColor(R.color.threshold_hbalance_secondary);
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
        mBackgroundColor = resources.getColor(R.color.game_bg_quad_hbalance);
        mPrimaryColor = resources.getColor(R.color.quad_primary);
        mSecondaryColor = resources.getColor(R.color.quad_secondary);
        break;
    }
  }

  @Override
  public String getDescription(Context context) {
    return context.getString(R.string.minigames_HBalance_description);
  }

  public String getName() {
    return "Balance";
  }
}
