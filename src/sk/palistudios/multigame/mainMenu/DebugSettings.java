package sk.palistudios.multigame.mainMenu;

/**
 *
 * @author virdzek
 */
public class DebugSettings {

    public static boolean adsActivated = false;
    /* DEBUG */
    public static boolean debugFirstRun = true;
    public static boolean unlockAllItems = false;
    public static boolean tutorialCompleted = true;
    public static boolean alwaysWinner = false;
    /* GAME */
    public static final int SECONDS_PER_LEVEL = 10;//15
    public static final int SCORE_COEFICIENT = 100;//1
    public static final int SECONDS_PER_LEVEL_TUTORIAL = 3;//10
    /* Čím väčšia číslo, tým ťahšie. */
    public static final int globalDifficultyCoeficient = 7;
    //    final private int GAME_REFRESH_INTERVAL = 1000 / GAME_UPDATES_PER_SECOND;
}