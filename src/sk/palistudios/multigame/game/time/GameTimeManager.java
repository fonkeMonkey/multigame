package sk.palistudios.multigame.game.time;

// @author Pali

import java.util.ArrayList;

import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.minigames.AMiniGame;
import sk.palistudios.multigame.game.minigames.MiniGameTCatcher;

public class GameTimeManager {

  private static ArrayList<ITimeObserver> registeredSecondsObservers = new ArrayList<ITimeObserver>();
  private static ArrayList<AMiniGame> registeredLevelObservers = new ArrayList<AMiniGame>();

  public static void onSecondPassed() {
    for (ITimeObserver o : registeredSecondsObservers) {
      o.onTimeChanged();
    }
  }

  public static void onLevelIncreased(GameActivity game) {
    for (AMiniGame mg : registeredLevelObservers) {
      if (mg.isActive()) {
        mg.onDifficultyIncreased();
      }
    }

    if (!game.isTutorial()) {
      game.flashScreen();
    }
  }

  public static void registerSecondsObserver(ITimeObserver to) {
  /* TODO Škaredý ojeb I know  aby sa mi viac razy tam neregoval*/
    for (ITimeObserver obs : registeredSecondsObservers) {
      if (obs instanceof MiniGameTCatcher) {
        return;
      }
    }

    if (!registeredSecondsObservers.contains(to)) {
      registeredSecondsObservers.add(to);
    }
  }

  public static void unregisterSecondsObserver(ITimeObserver to) {
    registeredSecondsObservers.remove(to);
  }

  public static void registerLevelChangedObserver(AMiniGame mg) {
    if (!registeredLevelObservers.contains(mg)) {
      registeredLevelObservers.add(mg);
    }
  }
}
