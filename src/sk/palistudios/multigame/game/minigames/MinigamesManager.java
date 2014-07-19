package sk.palistudios.multigame.game.minigames;

// @author Pali

import android.util.Log;
import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.time.GameTimeMaster;
import sk.palistudios.multigame.game.persistence.GameSaverLoader;
import sk.palistudios.multigame.game.persistence.GameSharedPref;

import java.lang.reflect.Constructor;

public class MinigamesManager {

    public static boolean[] currentlyActiveMinigames = new boolean[4];
    private static AMiniGame[] chosenMinigamesObjects = new AMiniGame[4];

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
        currentlyActiveMinigames[number] = true;
        game.getmFragmentViews()[number].setBackgroundColored();

        AMiniGame minigame = MinigamesManager.getMinigamesObjects()[number];

        minigame.onMinigameActivated();

    }

    public static void deactivateMinigame(GameActivity game, int number) {
        currentlyActiveMinigames[number] = false;
        game.getmFragmentViews()[number].setBackgroundGray();

        AMiniGame minigame = MinigamesManager.getMinigamesObjects()[number];

        minigame.onMinigameDeactivated();
        GameTimeMaster.unregisterLevelChangedObserver(minigame);

    }

    public static void loadMinigamesObjects(GameActivity game) {
        boolean isGameSaved = GameSharedPref.isGameSaved();
        boolean isTutorialActive = GameSharedPref.isTutorialModeActivated();

        //if it was saved it will be loaded by GameSaverLoader.load()
        if (!isGameSaved || isTutorialActive) {
            try {
                for (int i = 0; i < 4; i++) {
                    StringBuilder className = new StringBuilder("sk.palistudios.multigame.game.minigames.MiniGame");
                    className.append(GameSharedPref.getChosenMinigamesNames()[i]);
                    Class<?> clazz = Class.forName(className.toString());
                    Constructor<?> ctor = clazz.getConstructor(String.class, Integer.class, GameActivity.class);
                    Object object = null;
                    switch (i) {
                        case 0:
                            object = ctor.newInstance(new Object[]{"MG_V", 0, game});
                            break;
                        case 1:
                            object = ctor.newInstance(new Object[]{"MG_H", 1, game});
                            break;
                        case 2:
                            object = ctor.newInstance(new Object[]{"MG_T1", 2, game});
                            break;
                        case 3:
                            object = ctor.newInstance(new Object[]{"MG_T2", 3, game});
                            break;
                    }
                    chosenMinigamesObjects[i] = (AMiniGame) object;
                }
            } catch (Exception e) {
                Log.e("Game", e.getLocalizedMessage());
            }
        } else {
            GameSaverLoader.loadGame(game);
            GameSharedPref.setGameSaved(false);
//            game.getEditor().putBoolean("gameSaved", false);
        }
    }

    public static boolean isMiniGameActive(int number) {
        return currentlyActiveMinigames[number];
    }

    public static boolean[] getCurrentlyActiveMinigames() {
        return currentlyActiveMinigames;
    }

    public static void setCurrentlyActiveMinigames(boolean[] flags) {
        System.arraycopy(flags, 0, currentlyActiveMinigames, 0, 4);
    }

    public static AMiniGame[] getMinigamesObjects() {
        return chosenMinigamesObjects;
    }

    public static MinigameInfoObject[] getMinigamesInfoObjects() {
        String[] minigamesNames = GameSharedPref.getAllMinigamesNames();
        String[] minigamesTypes = GameSharedPref.getAllMinigamesTypes();
        int numberOfMinigames = minigamesNames.length;

        MinigameInfoObject[] minigameInfoObjects = new MinigameInfoObject[numberOfMinigames];

        for (int i = 0; i < numberOfMinigames; i++) {
            minigameInfoObjects[i] = new MinigameInfoObject(minigamesNames[i], minigamesTypes[i], GameSharedPref.isMinigameChosen(minigamesNames[i]));
        }

        return minigameInfoObjects;

    }

    public static void setAllMinigamesDifficultyForTutorial() {
        for (AMiniGame mg : chosenMinigamesObjects) {
            mg.setForTutorial();
        }
    }

    public static void setAllMinigamesDifficultyForClassicGame() {
        for (AMiniGame mg : chosenMinigamesObjects) {
            mg.setForClassicGame();
        }
    }

    public static boolean areAllMinigamesInitialized() {
        for (AMiniGame mg : chosenMinigamesObjects) {
            if (!mg.isMinigameInitilized()) {
                return false;
            }
        }
        return true;
    }
}
