package sk.palistudios.multigame.game.persistence;

// @author Pali

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.content.Context;
import android.content.Intent;

import sk.palistudios.multigame.game.GameActivity;
import sk.palistudios.multigame.game.minigames.AMiniGame;
import sk.palistudios.multigame.game.minigames.MinigamesManager;

public class GameSaverLoader {

  public static void saveGame(GameActivity game) {

    final int scoreToSave = game.getScore();
    final int levelToSave = game.getLevel();
    final int framesToSave = game.getFrames();
    final boolean[] activeMinigames = MinigamesManager.getmMinigamesActivityFlags();
    GameSharedPref.saveGameDetails(scoreToSave, levelToSave, framesToSave, activeMinigames);
    for (AMiniGame minigame : MinigamesManager.getMinigames()) {
      minigame.saveMinigame();
    }
  }

  public static void loadGame(GameActivity game) {

         /* Keď sa dojebe loadovanie spusti novú hru */
    try {
      int[] gameDetails = GameSharedPref.loadGameDetails();
      game.setGameDetails(gameDetails[1], gameDetails[0], gameDetails[2]);
      boolean[] activityFlags = GameSharedPref.loadMinigamesActivity();
      MinigamesManager.setmMinigamesActivityFlags(activityFlags);

      MinigamesManager.getMinigames()[0] = loadMinigameFromFile("MG_V", game);
      MinigamesManager.getMinigames()[1] = loadMinigameFromFile("MG_H", game);
      MinigamesManager.getMinigames()[2] = loadMinigameFromFile("MG_T1", game);
      MinigamesManager.getMinigames()[3] = loadMinigameFromFile("MG_T2", game);

      for (AMiniGame minigame : MinigamesManager.getMinigames()) {
        minigame.mGame = game;
        minigame.onMinigameLoaded();
      }
    } catch (Exception e) {
      GameSharedPref.setGameSaved(false);
      int apiVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
      if (apiVersion >= 11) {
        game.recreate();
      } else {
        Intent intent = game.getIntent();
        game.finish();
        game.startActivity(intent);
      }
    }
    GameSharedPref.setMinigamesInitialized(true);
  }

  public static AMiniGame loadMinigameFromFile(String mFileName, GameActivity game)
      throws NotSerializableException {
    Context mContext = game;
    FileInputStream fis = null;
    AMiniGame minigame = null;
    try {
      fis = mContext.openFileInput(mFileName);
      ObjectInputStream is = new ObjectInputStream(fis);
      minigame = (AMiniGame) is.readObject();
      is.close();
    } catch (OptionalDataException ex) {
      Logger.getLogger(AMiniGame.class.getName()).log(Level.SEVERE,
          "LoadGame OptionalDataException", ex);
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(AMiniGame.class.getName()).log(Level.SEVERE,
          "LoadGame ClassNotFoundException", ex);
    } catch (NotSerializableException ex) {
      Logger.getLogger(AMiniGame.class.getName()).log(Level.SEVERE, "Not serializable", ex);
    } catch (Exception ex) {
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

  public static void SaveMinigametoFile(String mFileName, AMiniGame miniGame,
      GameActivity context) {
    FileOutputStream fos = null;
    try {
      fos = context.openFileOutput(mFileName, Context.MODE_PRIVATE);
      ObjectOutputStream os = new ObjectOutputStream(fos);
      os.writeObject(miniGame);
      os.close();
    } catch (Exception ex) {
      Logger.getLogger(AMiniGame.class.getName()).log(Level.SEVERE,
          "File IO Exception: " + mFileName, ex);
    } finally {
      try {
        fos.close();
      } catch (Exception ex) {
        Logger.getLogger(AMiniGame.class.getName()).log(Level.SEVERE,
            "File IO Exception: " + mFileName, ex);
      }
    }
  }
}
