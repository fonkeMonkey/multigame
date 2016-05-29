package sk.palistudios.multigame.game.minigames;

// @author Pali

import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.persistence.GameSaverLoader;
import sk.palistudios.multigame.game.persistence.MGSettings;

/**
 * Encapsulates minigames and handles access to them.
 */
public class MinigamesManager {
  private BaseMiniGame[] mMinigames = new BaseMiniGame[4];
  private boolean[] mMinigamesActivityFlags = new boolean[4];

  public BaseMiniGame[] getMinigames() {
    return mMinigames;
  }

  public void setMinigames(BaseMiniGame[] minigames) {
    mMinigames = minigames;
  }

  public void activateAllMiniGames() {
    for (int i = 0; i < 4; i++) {
      activateMinigame(i);
    }
  }

  public void deactivateAllMiniGames() {
    for (int i = 0; i < 4; i++) {
      deactivateMinigame(i);
    }
  }

  public void activateMinigame(int number) {
    mMinigamesActivityFlags[number] = true;
    mMinigames[number].onMinigameActivated();
  }

  public void deactivateMinigame(int number) {
    mMinigamesActivityFlags[number] = false;
    mMinigames[number].onMinigameDeactivated();
  }

  public void detachGameRefFromMinigames() {
    for (BaseMiniGame mg : mMinigames) {
      mg.mGame = null;
    }
  }

  public boolean isMiniGameActive(int number) {
    return mMinigamesActivityFlags[number];
  }

  public boolean[] getMinigamesActivityFlags() {
    return mMinigamesActivityFlags;
  }

  public void setmMinigamesActivityFlags(boolean[] flags) {
    System.arraycopy(flags, 0, mMinigamesActivityFlags, 0, 4);
  }

  public void initMinigames(GameActivity game) {
    boolean isGameSaved = MGSettings.isGameSaved();
    boolean isTutorialActive = MGSettings.isTutorialModeActivated();

    //if it was saved it will be loaded by GameSaverLoader.load()
    if (!isGameSaved || isTutorialActive) {
      String[] activeMinigameNames = MGSettings.getChosenMinigamesNames();
      for (int i = 0; i < activeMinigameNames.length; i++) {
        mMinigames[i] = getMinigameInstance(game, activeMinigameNames[i], i);
      }
    } else {
      GameSaverLoader.loadGame(game);
    }
  }

  private BaseMiniGame getMinigameInstance(GameActivity game, String activeMinigameName, int position) {
    if (activeMinigameName.equals("VBird")) {
      return new MiniGameVBird("MG_V", position, game);
    } else if (activeMinigameName.equals("VBouncer")) {
      return new MiniGameVBouncer("MG_V", position, game);

    } else if (activeMinigameName.equals("HBalance")) {
      return new MiniGameHBalance("MG_H", position, game);

    } else if (activeMinigameName.equals("TCatcher")) {

      return new MiniGameTCatcher(resolveTouchType(position), position, game);

    } else if (activeMinigameName.equals("TGatherer")) {
      return new MiniGameTGatherer(resolveTouchType(position), position, game);

    } else if (activeMinigameName.equals("TInvader")) {
      return new MiniGameTInvader(resolveTouchType(position), position, game);

    }
    throw new RuntimeException("Bad game name!");
  }

  private String resolveTouchType(int position) {
    if (position == 2) {
      return GameSaverLoader.GAME_TOUCH1;
    }
    if (position == 3) {
      return GameSaverLoader.GAME_TOUCH2;
    }
    throw new RuntimeException("Touch game not on a touch position!");
  }

  public boolean isAllMinigamesInitialized() {
    for (BaseMiniGame mg : mMinigames) {
      if (!mg.isMinigameInitialized()) {
        return false;
      }
    }
    return true;
  }
}
