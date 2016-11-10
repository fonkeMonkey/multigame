package sk.palistudios.multigame.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import sk.palistudios.multigame.MgTracker;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.minigames.MinigamesFragment;
import sk.palistudios.multigame.game.persistence.MGSettings;
import sk.palistudios.multigame.hall_of_fame.HallOfFameActivity;
import sk.palistudios.multigame.hall_of_fame.HallofFameDatabaseHelper;
import sk.palistudios.multigame.hall_of_fame.HofItem;
import sk.palistudios.multigame.mainMenu.DebugSettings;
import sk.palistudios.multigame.mainMenu.MainMenuActivity;
import sk.palistudios.multigame.tools.internet.FacebookSharer;
import sk.palistudios.multigame.tools.internet.InternetChecker;

//TODO títo chlapci jedného dňa by mohli dediť od MultiGameDialog a mať každý samostatnú classu
public class GameDialogs {

  public final static int ASK_USER_TO_CONNECT = 1;
  public static boolean sLastGameLost = true;
  public static int sTutorialRestartWindowsShownPerSession = 0;

  public static void showWelcomeTutorialWindow(final GameActivity game) {

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            showNextTutorialWindow(game, true);
            break;
        }
      }
    };
    game.getMinigamesManager().deactivateAllMiniGames();

    AlertDialog.Builder builder = new AlertDialog.Builder(game);
    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
      @Override
      public void onCancel(DialogInterface dialog) {
        game.stopTutorial();
        game.finish();
      }
    });
    builder.setTitle(game.getString(R.string.tutorial_welcome_title));
    builder.setMessage(game.getString(R.string.tutorial_welcome)).setPositiveButton(
        Html.fromHtml("<b>" + game.getString(R.string.tutorial_positive) +
            "</b>"), dialogClickListener).show().setCanceledOnTouchOutside(false);
  }

  public static void showNextTutorialWindow(final GameActivity gameActivity, boolean showPopup) {
    gameActivity.getMinigamesManager().deactivateAllMiniGames();

    //We increment it here for some unknown reason. lazy to refactor.
    if (!GameDialogs.sLastGameLost) {
      if (GameActivity.sTutorialLastLevel == 3) {
        GameActivity.sTutorialLastLevel = 11;
      } else {
        GameActivity.sTutorialLastLevel++;
      }
    }
    MgTracker.trackTutorialWindowShown(GameActivity.sTutorialLastLevel);
    sLastGameLost = false;

    if (showPopup) {
      DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
              gameActivity.startGameTutorial();
              MgTracker.trackTutorialLevelStarted(GameActivity.sTutorialLastLevel);
              break;
          }
        }
      };

      AlertDialog.Builder builder = new AlertDialog.Builder(gameActivity);
      builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
          gameActivity.stopTutorial();
          gameActivity.finish();
        }
      });

      String symbol = "";
      String title = "";
      String message = "";
      String okButton = "";
      switch (GameActivity.sTutorialLastLevel) {
        case 0:
          symbol = "<font color=#D98179><b>(" + MinigamesFragment.SYMBOL_MINIGAME_VERTICAL +
              ")</b></font>";
          title = gameActivity.getString(R.string.minigames_VBird_title);
          message =
              gameActivity.getMinigamesManager().getMinigames()[GameActivity.sTutorialLastLevel]
                  .getDescription(gameActivity) + symbol;
          okButton = gameActivity.getString(R.string.tutorial_ok);
          break;
        case 1:
          symbol = "<font color=#D98179><b>(" + MinigamesFragment.SYMBOL_MINIGAME_HORIZONTAL +
              ")</b></font>";
          title = gameActivity.getString(R.string.minigames_HBalance_title);
          message =
              gameActivity.getMinigamesManager().getMinigames()[GameActivity.sTutorialLastLevel]
                  .getDescription(gameActivity) + symbol;
          okButton = gameActivity.getString(R.string.tutorial_ok);
          break;
        case 2:
          symbol = "<font color=#D98179><b>(" + MinigamesFragment.SYMBOL_MINIGAME_TOUCH +
              ")</b></font>";
          title = gameActivity.getString(R.string.minigames_TCatcher_title);
          message =
              gameActivity.getMinigamesManager().getMinigames()[GameActivity.sTutorialLastLevel]
                  .getDescription(gameActivity) + symbol;
          okButton = gameActivity.getString(R.string.tutorial_ok);
          break;
        case 3:
          symbol = "<font color=#D98179><b>(" + MinigamesFragment.SYMBOL_MINIGAME_TOUCH +
              ")</b></font>";
          title = gameActivity.getString(R.string.minigames_TGatherer_title);
          message =
              gameActivity.getMinigamesManager().getMinigames()[GameActivity.sTutorialLastLevel]
                  .getDescription(gameActivity) + symbol;
          okButton = gameActivity.getString(R.string.tutorial_ok);
          break;
        case 11:
          title = gameActivity.getString(R.string.first_multitask_title);
          message = gameActivity.getString(R.string.first_multitask_description);
          okButton = gameActivity.getString(R.string.first_multitask_ok);
          break;
        default:
          //          title = symbol + gameActivity.getString(R.string.minigame) +
          //              (GameActivity.sTutorialLastLevel + 1) + ": " +
          //              gameActivity.getMinigamesManager().getMinigames()[GameActivity
          // .sTutorialLastLevel]
          //                  .getName();
          break;
      }

      if (GameActivity.sTutorialLastLevel <= 11) {
        builder.setTitle(Html.fromHtml(title));
        builder.setMessage(Html.fromHtml(message)).setPositiveButton(
            Html.fromHtml("<b>" + okButton + "</b>"), dialogClickListener).show()
            .setCanceledOnTouchOutside(false);
      } else {
        gameActivity.startGameTutorial();
      }
    } else {
      gameActivity.startGameTutorial();
    }
  }

  public static void showTutorialWinnerWindow(final GameActivity game) {
    MgTracker.trackTutorialWindowShown(MgTracker.LABEL_TUTORIAL_WINNER);
    MgTracker.trackTutorialRestartWindowsShownPerSession(sTutorialRestartWindowsShownPerSession,
        true);
    sTutorialRestartWindowsShownPerSession = 0;
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            MGSettings.onTutorialCompleted();
            GameActivity.sTutorialLastLevel = 0;
            sLastGameLost = true;
            game.restartGame();
            break;
        }
      }
    };
    game.getMinigamesManager().deactivateAllMiniGames();
    game.stopTutorial();
    AlertDialog.Builder builder = new AlertDialog.Builder(game);
    builder.setCancelable(false).setTitle(game.getString(R.string.tutorial_finished_title))
        .setMessage(game.getString(R.string.tutorial_finished)).setPositiveButton(
        Html.fromHtml(game.getString(R.string.tutorial_finished_ok)), dialogClickListener).show();
  }

  public static void showTutorialLoserDialogWindow(final GameActivity game) {
    MgTracker.trackTutorialRestartWindowsShown();
    sTutorialRestartWindowsShownPerSession++;
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            MgTracker.trackTutorialRestarted(GameActivity.sTutorialLastLevel);
            sLastGameLost = true;
            game.stopTutorial();
            GameActivity.sTutorialRestart = true;
            game.restartGame();
            break;
        }
      }
    };
    game.stopMusic();

    AlertDialog.Builder builder = new AlertDialog.Builder(game);
    builder.setCancelable(false).setTitle(game.getString(R.string.tutorial_failed_title))
        .setMessage(game.getString(R.string.tutorial_failed)).setPositiveButton(
        Html.fromHtml(game.getString(R.string.tutorial_try_again)), dialogClickListener).show();

  }

  public static void showLoserDialogWindow(final GameActivity game) {
    if (DebugSettings.alwaysWinner) {
      showWinnerDialogWindow(game);
      return;
    }
    final int score = game.getScore();
    if (MGSettings.getHighestScore() < game.getScore()) {
      MGSettings.setHighestScore(game.getScore());
      MGSettings.setHighestScoreSubmitted(false);
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            MgTracker.trackGameLoserRetryButtonPushed();
            game.restartGame();
            break;

          case DialogInterface.BUTTON_NEUTRAL:
            MgTracker.trackGameLoserShareButtonPushed();
            if (InternetChecker.isNetworkAvailable(game)) {
              FacebookSharer.shareScoreToFacebook(score, false);
              game.finish();
            } else {
              askUserToConnect(game, false, score);
            }
            break;

          case DialogInterface.BUTTON_NEGATIVE:
            MgTracker.trackGameLoserOkButtonPushed();
            getBackToMainMenu(game);
            break;
        }
      }
    };
    AlertDialog.Builder builder = new AlertDialog.Builder(game);
    builder.setCancelable(false).setTitle(game.getString(R.string.game_loser_title)).setMessage(
        game.getString(R.string.game_loser)).setNegativeButton("OK", dialogClickListener)
        .setPositiveButton(Html.fromHtml("<b>" + game.getString(R.string.game_retry) + "</b>"),
            dialogClickListener).setNeutralButton(game.getString(R.string.game_share),
        dialogClickListener).show();
  }

  public static void showWinnerDialogWindow(final GameActivity gameActivity) {
    Tracker t = MgTracker.getTracker(gameActivity);
    t.setScreenName("Winner");
    t.send(new HitBuilders.AppViewBuilder().build());

    final EditText userNameEditText = new EditText(gameActivity);
    userNameEditText.setSingleLine();
    userNameEditText.setText(gameActivity.getString(R.string.button_hall_of_fame_multigame_fan));
    if (!(("").equals(MGSettings.getLastHofName()))) {
      userNameEditText.setText(MGSettings.getLastHofName());
    }
    userNameEditText.setSelection(userNameEditText.getText().length());

    final Activity act = gameActivity;
    final int score = gameActivity.getScore();
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        Intent intent;
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            /* Showing so that animation does not look that bad when moving to other activity */
            showStatusBar(gameActivity);

            MgTracker.trackGameWinnerOkButtonPushed();
            String playerName = userNameEditText.getText().toString();
            MGSettings.setLastHofName(playerName);

            HallofFameDatabaseHelper db = HallofFameDatabaseHelper.getInstance(gameActivity);
            int playerPosition = db.writeIntoHallOfFame(
                new HofItem(playerName, gameActivity.getScore()));
            if (MGSettings.getHighestScore() < gameActivity.getScore()) {
              MGSettings.setHighestScore(gameActivity.getScore());
              MGSettings.setHighestScoreSubmitted(false);
            }
            intent = new Intent(act, HallOfFameActivity.class);
            intent.putExtra(HallOfFameActivity.SCROLL_TO_POSITION, playerPosition);
            gameActivity.startActivity(intent);
            break;
          case DialogInterface.BUTTON_NEGATIVE:
            MgTracker.trackGameWinnerRetryButtonPushed();
            gameActivity.restartGame();
            break;
          case DialogInterface.BUTTON_NEUTRAL:
            MgTracker.trackGameWinnerShareButtonPushed();
            if (InternetChecker.isNetworkAvailable(gameActivity)) {
              MainMenuActivity.setOfferHighScore(gameActivity.getScore());
              FacebookSharer.shareScoreToFacebook(score, true);
              gameActivity.finish();
            } else {
              askUserToConnect(gameActivity, true, score);
            }
            break;
        }
      }
    };
    AlertDialog.Builder builder = new AlertDialog.Builder(gameActivity);
    builder.setCancelable(false).setView(userNameEditText).setTitle(
        gameActivity.getString(R.string.game_winner_title)).
        setMessage(gameActivity.getString(R.string.game_winner)).setPositiveButton(
        Html.fromHtml("<b>" + "OK" + "</b>"), dialogClickListener).setNegativeButton(
        gameActivity.getString(R.string.game_retry), dialogClickListener).setNeutralButton("Share",
        dialogClickListener).show();
  }

  public static void showWinnerDialogAfterShareWindow(final MainMenuActivity mainMenuActivity,
      final int score) {

    final EditText userNameEditText = new EditText(mainMenuActivity);
    userNameEditText.setSingleLine();
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        Intent intent;
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            String playerName = userNameEditText.getText().toString();
            if (playerName.equals("")) {
              playerName = mainMenuActivity.getString(R.string.button_hall_of_fame_multigame_fan);
            }

            HallofFameDatabaseHelper db = HallofFameDatabaseHelper.getInstance(mainMenuActivity);
            db.writeIntoHallOfFame(new HofItem(playerName, score));
            intent = new Intent(mainMenuActivity, HallOfFameActivity.class);
            mainMenuActivity.startActivity(intent);
            break;
          case DialogInterface.BUTTON_NEGATIVE:
            break;
        }
      }
    };
    AlertDialog.Builder builder = new AlertDialog.Builder(mainMenuActivity);
    builder.setCancelable(false).setView(userNameEditText).setMessage(
        mainMenuActivity.getString(R.string.game_ask_for_name)).setPositiveButton(
        Html.fromHtml("<b>" + "OK" + "</b>"), dialogClickListener).setNegativeButton(
        mainMenuActivity.getString(R.string.cancel), dialogClickListener).show();
  }

  public static void askUserToConnect(final Activity act, final boolean isWinner, final int score) {
    AlertDialog.Builder builder = new AlertDialog.Builder(act);

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            if (InternetChecker.isNetworkAvailable(act)) {
              if (isWinner) {
                MainMenuActivity.setOfferHighScore(score);
              }
              FacebookSharer.shareScoreToFacebook(score, isWinner);
            } else {
              askUserToConnect(act, isWinner, score);
            }
            break;
          case DialogInterface.BUTTON_NEGATIVE:
            getBackToMainMenu(act);
            break;
        }
        MainMenuActivity.isThereADialogToShow = false;
        GameActivity.isDialogPresent = false;
      }
    };

    builder.setCancelable(false).setMessage(act.getString(R.string.game_share_no_connection))
        .setPositiveButton(Html.fromHtml("<b>" + "OK" + "</b>"), dialogClickListener)
        .setNegativeButton(act.getString(R.string.cancel), dialogClickListener).show();
    MainMenuActivity.isThereADialogToShow = true;
    GameActivity.isDialogPresent = true;
    GameActivity.dialogType = GameDialogs.ASK_USER_TO_CONNECT;
    GameActivity.dialogScore = score;
    GameActivity.dialogIsWinner = isWinner;
  }

  private static void getBackToMainMenu(Activity act) {
    if (act instanceof MainMenuActivity) {
      return;
    }

    Context context = act.getApplicationContext();
    Intent intent = new Intent(context, MainMenuActivity.class);
    act.startActivity(intent);
  }

  private static void showStatusBar(GameActivity game) {
    try {
      ((View) game.findViewById(android.R.id.title).getParent()).setVisibility(View.VISIBLE);
    } catch (Exception e) {
    }
    game.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    game.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
  }
}
