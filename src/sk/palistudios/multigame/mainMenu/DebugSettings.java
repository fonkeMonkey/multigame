package sk.palistudios.multigame.mainMenu;

import android.content.Context;
import sk.palistudios.multigame.game.persistence.GameSharedPref;

/**
 * @author virdzek
 */
public class DebugSettings {

    /* GAME */
    public static final int SECONDS_PER_LEVEL = 15;//15
    public static final int SCORE_COEFICIENT = 1;//1
    public static final int SECONDS_PER_LEVEL_TUTORIAL = 10;//10
    /* Čím väčšia číslo, tým ťahšie. */
    public static final int globalDifficultyCoeficient = 7;
    public static boolean adsActivated = false;
    /* DEBUG */
    public static boolean debugInit = false;
    public static boolean debugFirstRun = false;
    public static boolean unlockAllItems = false;
    public static boolean tutorialCompleted = false;
    public static boolean alwaysWinner = false;
    //    final private int GAME_REFRESH_INTERVAL = 1000 / GAME_UPDATES_PER_SECOND;

    public static void debugInit(Context context){
//        GameSharedPref.setGameMode("Tutorial");
    }
}
