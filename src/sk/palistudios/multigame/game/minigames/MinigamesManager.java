package sk.palistudios.multigame.game.minigames;

// @author Pali

import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.persistence.GameSaverLoader;
import sk.palistudios.multigame.game.persistence.GameSharedPref;

public class MinigamesManager {

  //TODO tuto by sa mali centralnejsie robit tie veci a nie volat zvonku activateMinigame po jednom
  //

  private static BaseMiniGame[] mMinigames = new BaseMiniGame[4];
  private static boolean[] mMinigamesActivityFlags = new boolean[4];

  public static void loadMinigames(GameActivity game) {
    boolean isGameSaved = GameSharedPref.isGameSaved();
    boolean isTutorialActive = GameSharedPref.isTutorialModeActivated();

    //if it was saved it will be loaded by GameSaverLoader.load()
    if (!isGameSaved || isTutorialActive) {
      String[] activeMinigameNames = GameSharedPref.getChosenMinigamesNames();
      for (int i = 0; i < activeMinigameNames.length; i++) {
        mMinigames[i] = loadMinigame(game, activeMinigameNames[i], i);
      }
    } else {
      GameSaverLoader.loadGame(game);
    }
  }

  private static BaseMiniGame loadMinigame(GameActivity game, String activeMinigameName,
      int position) {
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

  private static String resolveTouchType(int position) {
    if(position == 2) return "MG_T1";
    if(position == 3) return "MG_T2";
    throw new RuntimeException("Touch game not on a touch position!");
  }

  protected static void activateAllMiniGames(GameActivity game) {
    for (int i = 0; i < 4; i++) {
      activateMinigame(game, i);
    }
  }

  public static void deactivateAllMiniGames(GameActivity game) {
    for (int i = 0; i < 4; i++) {
      deactivateMinigame(game, i);
    }
  }

  public static void activateMinigame(GameActivity game, int number) {
    mMinigamesActivityFlags[number] = true;
    mMinigames[number].onMinigameActivated();
  }

  public static void deactivateMinigame(GameActivity game, int number) {
    mMinigamesActivityFlags[number] = false;
    mMinigames[number].onMinigameDeactivated();
  }

  public static void detachGameRefFromMinigames(){
    for(BaseMiniGame mg : mMinigames){
      mg.mGame = null;
    }
  }

  public static boolean isMiniGameActive(int number) {
    return mMinigamesActivityFlags[number];
  }

  public static boolean[] getmMinigamesActivityFlags() {
    return mMinigamesActivityFlags;
  }

  public static void setmMinigamesActivityFlags(boolean[] flags) {
    System.arraycopy(flags, 0, mMinigamesActivityFlags, 0, 4);
  }

  public static BaseMiniGame[] getMinigames() {
    return mMinigames;
  }

  public static boolean isAllMinigamesInitialized() {
    for (BaseMiniGame mg : mMinigames) {
      if (!mg.isMinigameInitialized()) {
        return false;
      }
    }
    return true;
  }
}
