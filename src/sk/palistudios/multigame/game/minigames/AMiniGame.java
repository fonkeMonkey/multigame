package sk.palistudios.multigame.game.minigames;

// @author Pali

import java.io.Serializable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.persistence.GameSaverLoader;
import sk.palistudios.multigame.game.time.ITimeObserver;

public abstract class AMiniGame implements Serializable, ITimeObserver {

  protected final int colorAlt2;
  public Type type;
  public Integer mPosition;
  public transient GameActivity mGame;
  protected int mHeight;
  protected int mWidth;
  protected int colorMain;
  protected int colorAlt;
  String mFileName;
  boolean isMinigameInitialized;
  private boolean active = false;


  public enum Type {
    Horizontal,
    Vertical,
    Touch
  }


  /**
   * @param game
   */
  public AMiniGame(String fileName, int position, GameActivity game) {
    mFileName = fileName;
    mPosition = position;
    mGame = game;
    this.colorMain = mGame.getResources().getColor(R.color.gameMain);
    this.colorAlt = mGame.getResources().getColor(R.color.gameAlt);
    this.colorAlt2 = mGame.getResources().getColor(R.color.gameAlt2);
  }

  public abstract void updateMinigame();

  public abstract String getName();

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

  public boolean isMinigameInitialized() {
    return isMinigameInitialized;
  }

}