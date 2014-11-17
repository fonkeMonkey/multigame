package sk.palistudios.multigame.game.time;

// @author Pali

import java.util.ArrayList;

import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.minigames.BaseMiniGame;
import sk.palistudios.multigame.game.minigames.MiniGameTGatherer;

public class GameTimeManager {

  private static ArrayList<ISecondsObserver> registeredSecondsObservers = new ArrayList<ISecondsObserver>();
  private static ArrayList<BaseMiniGame> registeredLevelObservers = new ArrayList<BaseMiniGame>();

  public static void onSecondPassed() {
    for (ISecondsObserver o : registeredSecondsObservers) {
      o.onSecondPassed();
    }
  }

  public static void onLevelIncreased(GameActivity game) {
    for (BaseMiniGame mg : registeredLevelObservers) {
      if (mg.isActive()) {
        mg.onDifficultyIncreased();
      }
    }

    if (!game.isTutorial()) {
      game.flashScreen();
    }
  }

  public static void registerSecondsObserver(ISecondsObserver to) {
  /* TODO Škaredý ojeb I know  aby sa mi viac razy tam neregoval*/
    for (ISecondsObserver obs : registeredSecondsObservers) {
      if (to instanceof MiniGameTGatherer && obs instanceof MiniGameTGatherer) {
        return;
      }
    }

    if (!registeredSecondsObservers.contains(to)) {
      registeredSecondsObservers.add(to);
    }
  }

  public static void unregisterSecondsObserver(ISecondsObserver to) {
    registeredSecondsObservers.remove(to);
  }

  public static void clearTimeObservers(){
    registeredSecondsObservers.clear();
  }

  public static void registerLevelChangedObserver(BaseMiniGame mg) {
    if (!registeredLevelObservers.contains(mg)) {
      registeredLevelObservers.add(mg);
    }
  }
}
