package sk.palistudios.multigame.game.minigames;

// @author Pali
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import java.io.Serializable;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.skins.SkinsCenterListActivity;
import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.game.persistence.GameSaverLoader;
import sk.palistudios.multigame.tools.ITimeObserver;

public abstract class AMiniGame implements Serializable, ITimeObserver {

    public Typ type;
    String mFileName;
    protected int mHeight;
    protected int mWidth;
    public Integer mPosition;
    public transient GameActivity mGame;
    private boolean active = false;
    protected int colorMain;
    protected int colorAlt;
    boolean isMinigameInitialized;
    protected final int colorAlt2;

    public abstract void updateMinigame();

    public abstract String getName();

    public enum Typ {

        Horizontal,
        Vertical,
        Touch
    }

    /**
     *
     * @param game
     */
    public AMiniGame(String fileName, int position, GameActivity game) {
        mFileName = fileName;
        mPosition = position;
        mGame = game;
        this.colorMain = mGame.getResources().getColor(R.color.gameMain);
//        this.colorMain = SkinsCenterListActivity.getCurrentSkin(game).getColorMain();
//        this.colorAlt = SkinsCenterListActivity.getCurrentSkin(game).getColorAlt();
        this.colorAlt = mGame.getResources().getColor(R.color.gameAlt);
        this.colorAlt2 = mGame.getResources().getColor(R.color.gameAlt2);
    }

    public abstract void onDifficultyIncreased();

    public abstract void initMinigame(Bitmap mBitmap, boolean wasGameSaved);

    public abstract void drawMinigame(Canvas canvas);

    public void saveMinigame() {
        GameSaverLoader.SaveMinigametoFile(mFileName, this, mGame);
        onMinigameSaved();
    }

    public void onMinigameSaved() {
    }

    public void onMinigameLoaded() {
    }

    public void onTimeChanged() {
    }

    public abstract String getDescription(Context context);

    public void onMinigameActivated() {
        active = true;

    }

    public void onMinigameDeactivated() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public abstract void setForTutorial();

    public abstract void setForClassicGame();

    public boolean isMinigameInitilized() {
        return isMinigameInitialized;
    }
}
