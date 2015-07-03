package sk.palistudios.multigame.game.minigames;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.persistence.PaintSerializable;
import sk.palistudios.multigame.mainMenu.DebugSettings;
import sk.palistudios.multigame.tools.RandomGenerator;
import sk.palistudios.multigame.tools.SkinManager;

/**
 * @author Pali
 */
public class MiniGameVBird extends BaseMiniGame implements
    GameActivity.userInteractedVerticalListener {

  private static final int MAX_PATH_POINTS = 20;

  //Difficulty
  private int framesToCreateObstacle = (int) (200 / DebugSettings.GLOBAL_DIFFICULTY_COEFFICIENT);
  private float movementStep;
  private int maxDifficulty;
  private float actualMovement = 0;
  private int framesToGo;

  //Graphics
  private transient RandomGenerator mRG;
  private PaintSerializable mPaintBird = null;
  private PaintSerializable mPaintBirdCenter = null;
  private PaintSerializable mPaintObstacle = null;
  private float movementSensitivity;
  private final ArrayList<Obstacle> mObstacles = new ArrayList<Obstacle>();
  private float movementThreshold;
  private int birdLeft;
  private int birdRight;
  private int mBirdTop;
  private int mBirdBottom;
  private int mBirdVertCenter;
  private int mBirdSize;
  private int mObstacleWidth;
  private int mObstacleHeight;
  private int mTailPart1Size;
  private int mTailPart2Size;
  private int mTailPart3Size;
  private int mTailPartOffset;
  private int mTailLength;

  private List<Integer> mBirdPathHistory = new ArrayList<Integer>();

  public MiniGameVBird(String fileName, Integer position, GameActivity game) {
    super(fileName, position, game);
    type = Type.Vertical;
  }

  public void updateMinigame() {
    generateObstacles();
    moveField();
    moveBird();
    addBirdCenterToHistory();
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

    final int obstacleColor = (mAlternateColor != 0) ? mAlternateColor : mPrimaryColor;
    mPaintObstacle = new PaintSerializable(obstacleColor, Paint.Style.FILL);
    mPaintBird = new PaintSerializable(mPrimaryColor, Paint.Style.FILL);
    mPaintBirdCenter = new PaintSerializable(mSecondaryColor, Paint.Style.FILL);

    mBirdSize = mHeight / 8;
    mObstacleWidth = mWidth / 25;
    mObstacleHeight = (int) (mHeight / (2.5));

    mTailPartOffset = mBirdSize / 3;
    mTailPart1Size = (int) (mBirdSize / 2.0);
    mTailPart2Size = (int) ((mTailPart1Size / 3.0) * 2);
    mTailPart3Size = (int) ((mTailPart2Size / 3.0) * 2);
    mTailLength = mTailPart1Size + mTailPartOffset + mTailPart2Size + mTailPartOffset +
        mTailPart3Size + mTailPartOffset;

    if (!wasGameSaved) {
      birdLeft = (mWidth / 20) + mTailLength;
      birdRight = birdLeft + mBirdSize;
      mBirdTop = mWidth / 20;
      mBirdBottom = mBirdTop + mBirdSize;
      mBirdVertCenter = (mBirdTop + mBirdBottom) / 2;
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
    if(mBackgroundColor != 0) {
      mCanvas.drawColor(mBackgroundColor);
    }
    for (Obstacle obst : mObstacles) {
      mCanvas.drawRect(obst.left, obst.top, obst.right, obst.bottom, mPaintObstacle.mPaint);
    }
    mPaintBird.mPaint.setAlpha(255);
    mCanvas.drawRect(birdLeft, mBirdTop, birdRight, mBirdBottom, mPaintBird.mPaint);

    // draw rectangle on center of bird
    final int birdCenterSize = (int) (mBirdSize / 3.0);
    final int birdCenterLeft = (int) (birdLeft + (mBirdSize / 2.0) - (birdCenterSize / 2.0));
    final int birdCenterTop = (int) (mBirdTop + (mBirdSize / 2.0) - (birdCenterSize / 2.0));
    mCanvas.drawRect(birdCenterLeft, birdCenterTop, birdCenterLeft + birdCenterSize,
        birdCenterTop + birdCenterSize, mPaintBirdCenter.mPaint);

    drawTail(mCanvas);
  }

  private void drawTail(Canvas canvas) {
    final int pathHistorySize = mBirdPathHistory.size();

    final int part1Right = birdLeft - mTailPartOffset;
    final int part1Left = part1Right - mTailPart1Size;

    final int part2Right = part1Left - mTailPartOffset;
    final int part2Left = part2Right - mTailPart2Size;

    final int part3Right = part2Left - mTailPartOffset;
    final int part3Left = part3Right - mTailPart3Size;

    int tmpBirdVertCenter;

    int part1Top = (int) (mBirdVertCenter - (mTailPart1Size / 2.0));
    if(pathHistorySize > 5) {
      tmpBirdVertCenter = mBirdPathHistory.get(5);
      part1Top = (int) (tmpBirdVertCenter - (mTailPart1Size / 2.0));
    }

    int part2Top = (int) (mBirdVertCenter - (mTailPart2Size / 2.0));
    if(pathHistorySize > 10) {
      tmpBirdVertCenter = mBirdPathHistory.get(10);
      part2Top = (int) (tmpBirdVertCenter - (mTailPart2Size / 2.0));
    }

    int part3Top = (int) (mBirdVertCenter - (mTailPart3Size / 2.0));
    if(pathHistorySize > 15) {
      tmpBirdVertCenter = mBirdPathHistory.get(15);
      part3Top = (int) (tmpBirdVertCenter - (mTailPart3Size / 2.0));
    }

    Paint paint = mPaintBird.mPaint;
    paint.setAlpha(128); // 50%
    final Rect part1 = new Rect(part1Left, part1Top, part1Right, part1Top + mTailPart1Size);
    canvas.drawRect(part1, paint);

    paint.setAlpha(90); // 35%
    final Rect part2 = new Rect(part2Left, part2Top, part2Right, part2Top + mTailPart2Size);
    canvas.drawRect(part2, paint);

    paint.setAlpha(51); // 20%
    final Rect part3 = new Rect(part3Left, part3Top, part3Right, part3Top + mTailPart3Size);
    canvas.drawRect(part3, paint);
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

  private void addBirdCenterToHistory() {
    mBirdVertCenter = (mBirdTop + mBirdBottom) / 2;

    mBirdPathHistory.add(0, mBirdVertCenter);
    final int pathSize = mBirdPathHistory.size();
    if(pathSize > MAX_PATH_POINTS) {
      mBirdPathHistory.remove(pathSize - 1);
    }
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
  public void reskinLocally(SkinManager.Skin currentSkin) {
    final Resources resources = mGame.getResources();
    switch (currentSkin) {
      case QUAD:
        mBackgroundColor = resources.getColor(R.color.game_bg_quad_vbird);
        mPrimaryColor = resources.getColor(R.color.quad_primary);
        mSecondaryColor = resources.getColor(R.color.quad_secondary);
        break;
      case THRESHOLD:
        mBackgroundColor = Color.TRANSPARENT;
        mPrimaryColor = resources.getColor(R.color.threshold_primary);
        mSecondaryColor = resources.getColor(R.color.threshold_vbird_secondary);
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
        mBackgroundColor = resources.getColor(R.color.game_bg_quad_vbird);
        mPrimaryColor = resources.getColor(R.color.quad_primary);
        mSecondaryColor = resources.getColor(R.color.quad_secondary);
        break;
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
