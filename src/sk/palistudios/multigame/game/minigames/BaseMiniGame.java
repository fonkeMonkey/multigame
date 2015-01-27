package sk.palistudios.multigame.game.minigames;

// @author Pali

import java.io.Serializable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.persistence.GameSaverLoader;
import sk.palistudios.multigame.game.time.ISecondsObserver;
import sk.palistudios.multigame.tools.SkinManager;

abstract public class BaseMiniGame implements Serializable, ISecondsObserver {
  public Type type;
  public Integer mPosition;
  public transient GameActivity mGame;
  protected int mHeight;
  protected int mWidth;
  protected int mBackgroundColor = 0;
  protected int mPrimaryColor = 0;
  protected int mSecondaryColor = 0;
  protected int mAlternateColor = 0;
  String mFileName;
  boolean isMinigameInitialized;
  private boolean active = false;

  public enum Type {
    Horizontal,
    Vertical,
    Touch
  }

  public enum Minigame {
    BALANCE, BOUNCER, BIRD, CATCHER, GATHERER, INVADER,
  }

  public BaseMiniGame(String fileName, int position, GameActivity game) {
    mFileName = fileName;
    mPosition = position;
    mGame = game;

    reskinLocally(SkinManager.getInstance().getCurrentSkin());
  }

  public abstract void initMinigame(Bitmap mBitmap, boolean wasGameSaved);
  public abstract void updateMinigame();
  public abstract void drawMinigame(Canvas canvas);

  public abstract String getName();
  public abstract String getDescription(Context context);

  public abstract void onDifficultyIncreased();

  public void onMinigameSaved(){}
  public void onMinigameLoaded(){}
  public void saveMinigame() {
    GameSaverLoader.SaveMinigametoFile(mFileName, this, mGame);
    onMinigameSaved();
  }

  public void onSecondPassed(){}

  public void onMinigameActivated() {
    active = true;
  }
  public void onMinigameDeactivated() {
    active = false;
  }

  public boolean isActive() {
    return active;
  }

  public boolean isMinigameInitialized() {
    return isMinigameInitialized;
  }

  public abstract void reskinLocally(SkinManager.Skin currentSkin);
}