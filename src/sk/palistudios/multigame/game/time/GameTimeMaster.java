package sk.palistudios.multigame.game.time;

// @author Pali

import java.util.ArrayList;

import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.minigames.AMiniGame;
import sk.palistudios.multigame.game.minigames.MiniGameTCatcher;

public class GameTimeMaster {

  private static ArrayList<ITimeObserver> registeredObservers = new ArrayList<ITimeObserver>();
  private static ArrayList<ILevelChangedObserver> registeredLevelChangeObservers =
      new ArrayList<ILevelChangedObserver>();
  private static ArrayList<AMiniGame> registeredMinigames = new ArrayList<AMiniGame>();

  public static void onSecondPassed() {
    for (ITimeObserver o : registeredObservers) {
      o.onTimeChanged();
    }
  }

  public static void onLevelIncreased(GameActivity game) {
    for (AMiniGame mg : registeredMinigames) {
      if (mg.isActive()) {
        mg.onDifficultyIncreased();
      }
    }

    for (ILevelChangedObserver obs : registeredLevelChangeObservers) {
      obs.onLevelChanged();
    }

    if (!game.isTutorial()) {
      GameActivity.flashScreen(game);
    }

  }

  public static void registerTimeObserver(ITimeObserver to) {
        /* TODO Škaredý ojeb I know  aby sa mi viac razy tam neregoval*/
    for (ITimeObserver obs : registeredObservers) {
      if (obs instanceof MiniGameTCatcher) {
        return;
      }
    }

    if (!registeredObservers.contains(to)) {
      registeredObservers.add(to);
    }
  }

  public static void unregisterTimeObserver(ITimeObserver to) {
    registeredObservers.remove(to);
  }

  public static void registerLevelChangedObserver(AMiniGame mg) {
    if (!registeredMinigames.contains(mg)) {
      registeredMinigames.add(mg);
    }
  }

  public static void registerLevelChangedObserver(ILevelChangedObserver obs) {
    if (!registeredLevelChangeObservers.contains(obs)) {
      registeredLevelChangeObservers.add(obs);
    }
  }

  public static void unregisterLevelChangedObserver(AMiniGame mg) {
    registeredMinigames.remove(mg);
  }

  public static void unregisterLevelChangedObserver(ILevelChangedObserver obs) {
    registeredLevelChangeObservers.remove(obs);
  }
}
