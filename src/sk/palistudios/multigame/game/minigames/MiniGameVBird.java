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
public class MiniGameVBird extends BaseMiniGame implements
    GameActivity.userInteractedVerticalListener {
  //Difficulty
  private int framesToCreateObstacle = (int) (200 / DebugSettings.GLOBAL_DIFFICULTY_COEFFICIENT);
  private float movementStep;
  private int maxDifficulty;
  private float actualMovement = 0;
  private int framesToGo;

  //Graphics
  private transient RandomGenerator mRG;
  private PaintSerializable mPaintBird = null;
  private PaintSerializable mPaintObstacle = null;
  private float movementSensitivity;
  private final ArrayList<Obstacle> mObstacles = new ArrayList<Obstacle>();
  private float movementThreshold;
  private int birdLeft;
  private int birdRight;
  private int mBirdTop;
  private int mBirdBottom;
  private int mBirdSize;
  private int mObstacleWidth;
  private int mObstacleHeight;

  public MiniGameVBird(String fileName, Integer position, GameActivity game) {
    super(fileName, position, game);
    type = Type.Vertical;
  }

  public void updateMinigame() {
    generateObstacles();
    moveField();
    moveBird();
  }

  public void initMinigame(Bitmap mBitmap, boolean wasGameSaved) {
    mHeight = mBitmap.getHeight();
    mWidth = mBitmap.getWidth();
    mRG = RandomGenerator.getInstance();

    movementThreshold = 0.25f;
    movementSensitivity = (float) mHeight / 150;
    movementStep = ((float) mWidth / 400) * DebugSettings.GLOBAL_DIFFICULTY_COEFFICIENT;

    if (mGame.isTutorial()){
      framesToCreateObstacle /= DebugSettings.GLOBAL_DIFFICULTY_TUTORIAL_COEFFICIENT;
      movementStep *= DebugSettings.GLOBAL_DIFFICULTY_TUTORIAL_COEFFICIENT;
    }

    mPaintObstacle = new PaintSerializable(colorAlt, Paint.Style.FILL);

    mPaintBird = new PaintSerializable(colorMain, Paint.Style.FILL);

    mBirdSize = mHeight / 9;
    mObstacleWidth = mWidth / 20;
    mObstacleHeight = (int) (mHeight / (2.5));

    if (!wasGameSaved) {
      birdLeft = mWidth / 20;
      birdRight = mWidth / 20 + mBirdSize;
      mBirdTop = mWidth / 20;
      mBirdBottom = mWidth / 20 + mBirdSize;
      framesToGo = 100;

      //difficulty
      maxDifficulty = (int) (mBirdSize * 1.5);
    }


    isMinigameInitialized = true;
  }

  public void onUserInteractedVertical(float verticalMovement) {
    if (mWidth == 0 || mHeight == 0) {
      return;
    }

    if (verticalMovement > -movementThreshold && verticalMovement < movementThreshold) {
      return;
    }

    verticalMovement *= movementSensitivity;
    actualMovement = verticalMovement;
  }

  public void drawMinigame(Canvas mCanvas) {
    for (Obstacle obst : mObstacles) {
      mCanvas.drawRect(obst.left, obst.top, obst.right, obst.bottom, mPaintObstacle.mPaint);
    }
    mPaintBird.mPaint.setAlpha(255);
    mCanvas.drawRect(birdLeft, mBirdTop, birdRight, mBirdBottom, mPaintBird.mPaint);
  }

  private void generateObstacles() {
    if (framesToGo == 0) {
      float bottom = mRG.generateInt(mObstacleHeight, mHeight);
      Obstacle obj = new Obstacle(mWidth - 5 - mObstacleWidth, bottom - mObstacleHeight, mWidth - 5,
          bottom);
      mObstacles.add(obj);
      framesToGo = framesToCreateObstacle;
    } else {
      framesToGo--;
    }
  }

  private void moveField() {
    for (Obstacle obst : mObstacles) {
      if (!obst.isOutOfBounds()) {
        obst.move();
      } else {
        mObstacles.remove(obst);
        return;
      }
    }
  }

  private void moveBird() {
    //      checking the edges
    if (mBirdTop + actualMovement > mHeight - mBirdSize) {
      mBirdTop = mHeight - mBirdSize;
      mBirdBottom = mHeight - mBirdSize + mBirdSize;
      return;
    }

    if (mBirdTop + actualMovement < 0) {
      mBirdTop = 0;
      mBirdBottom = mBirdSize;
      return;
    }

    mBirdTop += actualMovement;
    mBirdBottom += actualMovement;
  }

  @Override
  public void onDifficultyIncreased() {
    int difficultyStep =
        (framesToCreateObstacle / 100) * DebugSettings.GLOBAL_DIFFICULTY_INCREASE_COEFFICIENT;

    if (difficultyStep < 1) {
      difficultyStep = 1;
    }

    if (framesToCreateObstacle > maxDifficulty) {
      framesToCreateObstacle -= difficultyStep;
    }
  }

  @Override
  public String getDescription(Context context) {
    return context.getString(R.string.minigames_VBird);
  }

  public String getName() {
    return "Bird";
  }

  private boolean isCollision(float top, float bottom, int birdTop, int birdBottom) {
    if (birdTop < bottom && birdTop > top) {
      return true;
    }
    return birdBottom < bottom && birdBottom > top;
  }

  private class Obstacle implements Serializable {
    float left;
    final float top;
    float right;
    final float bottom;

    public Obstacle(float left, float top, float right, float bottom) {
      this.left = left;
      this.top = top;
      this.right = right;
      this.bottom = bottom;
    }

    private boolean isOutOfBounds() {
      return right - 1 < 0;
    }

    private void move() {
      //if collision
      if ((birdRight >= left - 1 && birdRight <= right - 1) ||
          (birdLeft >= left - 1 && birdLeft <= right - 1)) {
        if (isCollision(top, bottom, mBirdTop, mBirdBottom)) {
          if(mGame != null){
            mGame.onGameLost(mPosition);
          }
        }
      }
      left -= movementStep;
      right -= movementStep;
    }
  }
}
