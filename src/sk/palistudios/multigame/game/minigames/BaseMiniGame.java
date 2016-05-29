package sk.palistudios.multigame.game.minigames;

// @author Pali

import java.io.Serializable;

import android.content.Context;
import android.graphics.Canvas;

import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.persistence.GameSaverLoader;
import sk.palistudios.multigame.game.time.ISecondsObserver;
import sk.palistudios.multigame.tools.SkinManager;

abstract public class BaseMiniGame implements Serializable, ISecondsObserver {
  public Type type;
  public transient GameActivity mGame;

  int mPosition;
  int mHeight;
  int mWidth;
  boolean mWasgameSaved;

  int mBackgroundColor = 0;
  int mPrimaryColor = 0;
  int mSecondaryColor = 0;
  int mAlternateColor = 0;
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

  public abstract void updateMinigame();
  public abstract void drawMinigame(Canvas canvas);

  public abstract String getName();
  public abstract String getDescription(Context context);

  public abstract void onDifficultyIncreased();

  public abstract void onMinigameLoaded();

  abstract void initMinigame();

  public void saveMinigame() {
    GameSaverLoader.SaveMinigametoFile(mFileName, this, mGame.getApplicationContext());
  }
  public void initMinigame(int width, int height, boolean wasGameSaved){
    mWidth = width;
    mHeight = height;
    mWasgameSaved = wasGameSaved;
    initMinigame();
  }

  public void onSecondPassed(){
    //For now, do nothing
  }

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