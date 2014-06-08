package sk.palistudios.multigame.game.minigames;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.ArrayList;
import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.persistence.PaintSerializable;
import sk.palistudios.multigame.tools.RandomGenerator;
import java.io.Serializable;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.mainMenu.GlobalSettings;

/**
 *
 * @author Pali
 */
public class MiniGameVBird extends AMiniGame implements IMiniGameVertical {

    private ArrayList<Obstacle> mObstacles = new ArrayList<Obstacle>();
    private final static RandomGenerator mRG = RandomGenerator.getInstance();
    private float movementThreshold;
//    private int mBlur;

    public MiniGameVBird(String fileName, Integer position, GameActivity game) {
        super(fileName, position, game);
        type = Typ.Vertical;
    }

    public void updateMinigame() {
        generateObstacles();
        moveField();
        moveBird();

    }
    private int birdLeft;
    private int birdRight;
    private int mBirdTop;
    private int mBirdBottom;
    PaintSerializable mPaintBird = null;
    PaintSerializable mPaintObstacle = null;
    private int mBirdSize;
    private int mObstacleWidth;
    private int mObstacleHeight;
    private int framesWithoutObstacle;
    float movementSensitivity;
    private float movementStep;
    private int difficultyStep;
    private int maxDifficulty;

    public void initMinigame(Bitmap mBitmap, boolean wasGameSaved) {

        mHeight = mBitmap.getHeight();
        mWidth = mBitmap.getWidth();

        mPaintObstacle = new PaintSerializable(colorAlt, Paint.Style.FILL);

        mPaintBird = new PaintSerializable(colorMain, Paint.Style.FILL);

        mBirdSize = mHeight / 9;
//        mBlur = mBirdSize / 20;
        mObstacleWidth = mWidth / 20;
        mObstacleHeight = (int) (mHeight / (2.5));

//        for (int i = 0; i < mBlurMemory.length; i++) {
//            mBlurMemory[i] = -1;
//        }

        if (!wasGameSaved) {
            birdLeft = mWidth / 20;
            birdRight = mWidth / 20 + mBirdSize;
            mBirdTop = mWidth / 20;
            mBirdBottom = mWidth / 20 + mBirdSize;
            framesWithoutObstacle = 160;
            framesToGo = 100;

            //difficulty
            maxDifficulty = (int) (mBirdSize * 1.5);/// movementStep);
//            difficultyStep = (framesWithoutObstacle - maxDifficulty) / 20;
        }

        movementThreshold = 0.25f;
        movementSensitivity = (float) mHeight / 150;
        movementStep = (float) mWidth / 400;

        isMinigameInitialized = true;


    }
    private float actualMovement = 0;

    public void onUserInteracted(float movement) {
        if (mWidth == 0 || mHeight == 0) {
            return;
        }

        if (movement > -movementThreshold && movement < movementThreshold) {
            return;
        }

        movement *= movementSensitivity;
        actualMovement = movement;
    }

    public void drawMinigame(Canvas mCanvas) {

        for (Obstacle obst : mObstacles) {
            mCanvas.drawRect(obst.left, obst.top, obst.right, obst.bottom, mPaintObstacle.mPaint);
        }

//        for (int i = mBlurMemory.length - 1; i > 0; i--) {
////            mPaintBird.mPaint.setAlpha((int)(255 /( 2 * Math.pow(2,i))));
//            mPaintBird.mPaint.setAlpha(255 /(i + 2));
////            mPaintBird.mPaint.setAlpha((int)((255 / (blurMemorySize + 1)) * (blurMemorySize - i)));
////            mCanvas.drawRect(birdLeft - mBlur * i, mBlurMemory[i], birdRight, mBlurMemory[i] + mBirdSize, mPaintBird.mPaint);
//            mCanvas.drawRect(birdLeft - mBlur * i, mBirdTop, birdRight, mBirdBottom, mPaintBird.mPaint);
//        }
        mPaintBird.mPaint.setAlpha(255);
        mCanvas.drawRect(birdLeft, mBirdTop, birdRight, mBirdBottom, mPaintBird.mPaint);

//        /* blur */
//        mPaintBird.mPaint.setAlpha(255 / 4);
//        mCanvas.drawRect(birdLeft - mBlur * 2, mBirdTopBefore2, birdRight, mBirdTopBefore2 + mBirdSize, mPaintBird.mPaint);
//        mPaintBird.mPaint.setAlpha(255 / 2);
//        mCanvas.drawRect(birdLeft - mBlur, mBirdTopBefore1, birdRight, mBirdTopBefore1 + mBirdSize, mPaintBird.mPaint);


    }
    private int framesToGo;

    private void generateObstacles() {

        if (framesToGo == 0) {
            float bottom = mRG.generateInt(mObstacleHeight, mHeight);
            Obstacle obj = new Obstacle(mWidth - 5 - mObstacleWidth, bottom - mObstacleHeight, mWidth - 5, bottom);
            mObstacles.add(obj);
            framesToGo = framesWithoutObstacle;
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
                obst = null;
                return;

            }
        }

    }
//    private int blurMemorySize = 10;
//    private int[] mBlurMemory = new int[blurMemorySize];

    private void moveBird() {

        /* Blur */
//        for (int i = mBlurMemory.length - 1; i > 0; i--) {
//            mBlurMemory[i] = mBlurMemory[i - 1];
//        }
//        mBlurMemory[0] = mBirdTop;


//        mBirdTopBefore2 = mBirdTopBefore1;
//        mBirdTopBefore1 = mBirdTop;

        //      checking the edges
        if (mBirdTop + actualMovement > mHeight - mBirdSize) {
            mBirdTop = mHeight - mBirdSize;
            mBirdBottom = mHeight - mBirdSize + mBirdSize;
            return;
        }

        if (mBirdTop + actualMovement < 0) {
            mBirdTop = 0;
            mBirdBottom = 0 + mBirdSize;
            return;
        }

        mBirdTop += actualMovement;
        mBirdBottom += actualMovement;
    }

    @Override
    public void onDifficultyIncreased() {
        difficultyStep = (framesWithoutObstacle / 100) * GlobalSettings.globalDifficultyCoeficient;

        if (difficultyStep < 1) {
            difficultyStep = 1;
        }

        if (framesWithoutObstacle > maxDifficulty) {
            framesWithoutObstacle -= difficultyStep;
        }
    }

    @Override
    public String getDescription(Context context) {
        return context.getString(R.string.minigames_VBird);
    }

    public String getName() {
        return "Bird";
    }

    private class Obstacle implements Serializable {

        float left;
        float top;
        float right;
        float bottom;

        public Obstacle(float left, float top, float right, float bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        private boolean isOutOfBounds() {
            if (right - 1 >= 0) {
                return false;
            }
            return true;
        }

        private void move() {

            //if collision
            if ((birdRight >= left - 1 && birdRight <= right - 1) || (birdLeft >= left - 1 && birdLeft <= right - 1)) {
                if (isCollision(top, bottom, mBirdTop, mBirdBottom)) {
                    mGame.onGameLost(mPosition);
                }
            }
            left -= movementStep;
            right -= movementStep;

        }
    }

    private boolean isCollision(float top, float bottom, int birdTop, int birdBottom) {

        if (birdTop < bottom && birdTop > top) {
            return true;
        }
        if (birdBottom < bottom && birdBottom > top) {
            return true;
        }
        return false;
    }

    @Override
    public void setForTutorial() {
        framesWithoutObstacle = (int) (framesWithoutObstacle * 1.2);
    }

    @Override
    public void setForClassicGame() {
        framesWithoutObstacle = (int) (framesWithoutObstacle * 1.1);
    }
}
