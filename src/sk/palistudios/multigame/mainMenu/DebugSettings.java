package sk.palistudios.multigame.mainMenu;

import android.content.Context;

/**
 * @author virdzek
 */
public class DebugSettings {

  /* GAME */
  public static final int SECONDS_PER_LEVEL = 15;//15
  public static final int SCORE_COEFFICIENT = 1;//1
  public static final int SECONDS_PER_LEVEL_TUTORIAL = 10;//10
  /* Čím väčšia čísla, tým ťahšie. */
  public static final float GLOBAL_DIFFICULTY_COEFFICIENT = 0.8f;
  /* Percento toho horného, tj asi vždy by to malo byť pod 1, kedže tutorial je easier. */
  public static final float GLOBAL_DIFFICULTY_TUTORIAL_COEFFICIENT = 0.8f;
  public static final int GLOBAL_DIFFICULTY_INCREASE_COEFFICIENT = 7;

  /* DEBUG */
  public static boolean debugInit = false;
  public static boolean debugFirstRun = false;
  public static boolean unlockAllItems = false;
  public static boolean tutorialCompleted = false;
  public static boolean alwaysWinner = false;

  public static void debugInit(Context context) {
  }
}
