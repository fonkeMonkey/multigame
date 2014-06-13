package sk.palistudios.multigame.game.persistence;

// @author Pali

import android.content.Context;
import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.GameMinigamesManager;
import sk.palistudios.multigame.game.minigames.AMiniGame;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameSaverLoader {

    public static void saveGame(GameActivity game) {

        final int scoreToSave = game.getScore();
        final int levelToSave = game.getLevel();
        final int framesToSave = game.getFrames();
        final boolean[] activeMinigames = GameMinigamesManager.getCurrentlyActiveMinigames();
        GameSharedPref.saveGameDetails(scoreToSave, levelToSave, framesToSave, activeMinigames);
        for (AMiniGame minigame : GameMinigamesManager.getMinigamesObjects()) {
            minigame.saveMinigame();
        }
    }

    public static void loadGame(GameActivity game) {

        int[] gameDetails = GameSharedPref.loadGameDetails();
        game.setGameDetails(gameDetails[1], gameDetails[0], gameDetails[2]);
        boolean[] activityFlags = GameSharedPref.loadMinigamesActivity();
        GameMinigamesManager.setCurrentlyActiveMinigames(activityFlags);


        GameMinigamesManager.getMinigamesObjects()[0] = loadMinigameFromFile("MG_V", game);
        GameMinigamesManager.getMinigamesObjects()[1] = loadMinigameFromFile("MG_H", game);
        GameMinigamesManager.getMinigamesObjects()[2] = loadMinigameFromFile("MG_T1", game);
        GameMinigamesManager.getMinigamesObjects()[3] = loadMinigameFromFile("MG_T2", game);

        /* Keď sa dojebe loadovanie spusti novú hru */
        try {
            for (AMiniGame minigame : GameMinigamesManager.getMinigamesObjects()) {
                minigame.mGame = game;
                minigame.onMinigameLoaded();
            }
        } catch (Exception e) {
            GameSharedPref.setGameSaved(false);
            game.recreate();
        }

//        game.getEditor().commit();
        GameSharedPref.setMinigamesInitialized(true);
//        GameMinigamesManager.setMinigamesInitialized(true, game);
    }

    public static AMiniGame loadMinigameFromFile(String mFileName, GameActivity game) {
        Context mContext = game;
        FileInputStream fis = null;
        AMiniGame minigame = null;
        try {
            fis = mContext.openFileInput(mFileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            minigame = (AMiniGame) is.readObject();
            is.close();
        } catch (OptionalDataException ex) {
            Logger.getLogger(AMiniGame.class.getName()).log(Level.SEVERE, "LoadGame OptionalDataException", ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AMiniGame.class.getName()).log(Level.SEVERE, "LoadGame ClassNotFoundException", ex);
        } catch (IOException ex) {
            Logger.getLogger(AMiniGame.class.getName()).log(Level.SEVERE, "Loadgame IO exception", ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(AMiniGame.class.getName()).log(Level.SEVERE, "Loadgame IO exception", ex);
            }
        }
        return minigame;
    }

    public static void SaveMinigametoFile(String mFileName, Object object, GameActivity game) {
        Context mContext = game;
        FileOutputStream fos = null;
        try {
            fos = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(object);
            os.close();
        } catch (IOException ex) {
            Logger.getLogger(AMiniGame.class.getName()).log(Level.SEVERE, "File IO Exception: " + mFileName, ex);
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(AMiniGame.class.getName()).log(Level.SEVERE, "File IO Exception: " + mFileName, ex);
            }
        }
    }
}
