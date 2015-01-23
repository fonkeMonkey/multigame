package sk.palistudios.multigame.game;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import sk.palistudios.multigame.MgTracker;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.minigames.MinigamesFragment;
import sk.palistudios.multigame.game.minigames.MinigamesManager;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.hall_of_fame.HallOfFameActivity;
import sk.palistudios.multigame.hall_of_fame.HofDatabaseCenter;
import sk.palistudios.multigame.hall_of_fame.HofItem;
import sk.palistudios.multigame.mainMenu.DebugSettings;
import sk.palistudios.multigame.mainMenu.MainMenuActivity;
import sk.palistudios.multigame.tools.internet.FacebookSharer;
import sk.palistudios.multigame.tools.internet.InternetChecker;

//TODO títo chlapci jedného dňa by mohli dediť od MultiGameDialog a mať každý samostatnú classu
public class GameDialogs {

  public final static int ASK_USER_TO_CONNECT = 1;
  public static boolean sLostGame = true;
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
    MinigamesManager.deactivateAllMiniGames(game);

    AlertDialog.Builder builder = new AlertDialog.Builder(game);
    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
      @Override
      public void onCancel(DialogInterface dialog) {
        game.stopTutorial();
        game.finish();
      }
    });
    builder.setTitle(game.getString(R.string.tutorial_welcome_title));
    builder.setMessage(game.getString(R.string.tutorial_welcome)).setPositiveButton(Html.fromHtml(
        "<b>" + game.getString(R.string.tutorial_positive) +
            "</b>"), dialogClickListener).show().setCanceledOnTouchOutside(false);
  }

  public static void showNextTutorialWindow(final GameActivity game, boolean showPopup) {
    MinigamesManager.deactivateAllMiniGames(game);
    if (!GameDialogs.sLostGame) {
      GameActivity.sTutorialLastLevel++;
    }
    MgTracker.trackTutorialWindowShown(GameActivity.sTutorialLastLevel);
    sLostGame = false;

    if (showPopup) {
      DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
              game.startGameTutorial();
              MgTracker.trackTutorialLevelStarted(GameActivity.sTutorialLastLevel);
              break;
          }
        }
      };

      AlertDialog.Builder builder = new AlertDialog.Builder(game);
      builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
          game.stopTutorial();
          game.finish();
        }
      });

      String symbol = "";

      switch (GameActivity.sTutorialLastLevel) {
        case 0:
          symbol = "<font color=#D98179><b>(" + MinigamesFragment.SYMBOL_MINIGAME_VERTICAL +
              ")</b></font>";
          break;
        case 1:
          symbol = "<font color=#D98179><b>(" + MinigamesFragment.SYMBOL_MINIGAME_HORIZONTAL +
              ")</b></font>";
          break;
        case 2:
        case 3:
          symbol = "<font color=#D98179><b>(" + MinigamesFragment.SYMBOL_MINIGAME_TOUCH +
              ")</b></font>";
          break;

      }
      String title = symbol + game.getString(R.string.minigame) +
          (GameActivity.sTutorialLastLevel + 1) + ": " +
          MinigamesManager.getMinigames()[GameActivity.sTutorialLastLevel].getName();

      String message =
          MinigamesManager.getMinigames()[GameActivity.sTutorialLastLevel].getDescription(game);
      builder.setTitle(Html.fromHtml(title));
      builder.setMessage(Html.fromHtml(message)).setPositiveButton(Html.fromHtml(
          "<b>" + game.getString(R.string.tutorial_try) + "</b>"), dialogClickListener).show()
          .setCanceledOnTouchOutside(false);
    } else {
      game.startGameTutorial();
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
            GameSharedPref.onTutorialCompleted();
            GameActivity.sTutorialLastLevel = 0;
            sLostGame = true;
            game.finish();
            break;
        }
      }
    };
    MinigamesManager.deactivateAllMiniGames(game);
    game.stopTutorial();
    AlertDialog.Builder builder = new AlertDialog.Builder(game);
    builder.setCancelable(false).setTitle(game.getString(R.string.tutorial_finished_title))
        .setMessage(game.getString(R.string.tutorial_finished)).setPositiveButton(Html.fromHtml(
        "<b>OK</b>"), dialogClickListener).show();
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
            sLostGame = true;
            game.stopTutorial();
            GameActivity.sTutorialRestart = true;
            restartGame(game);
            break;
        }
      }
    };
    game.stopMusic();

    AlertDialog.Builder builder = new AlertDialog.Builder(game);
    builder.setCancelable(false).setTitle(game.getString(R.string.tutorial_failed_title))
        .setMessage(game.getString(R.string.tutorial_failed)).setPositiveButton(Html.fromHtml(
        "<b>OK</b>"), dialogClickListener).show();

  }

  public static void showLoserDialogWindow(final GameActivity game) {
    if (DebugSettings.alwaysWinner) {
      showWinnerDialogWindow(game);
      return;
    }
    final int score = game.getScore();
    if (GameSharedPref.getHighestScore() < game.getScore()) {
      GameSharedPref.setHighestScore(game.getScore());
      GameSharedPref.setHighestScoreSubmitted(false);
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            MgTracker.trackGameLoserRetryButtonPushed();
            restartGame(game);
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

  public static void showWinnerDialogWindow(final GameActivity game) {
    Tracker t = MgTracker.getTracker(game);
    t.setScreenName("Winner");
    t.send(new HitBuilders.AppViewBuilder().build());

    final EditText userNameEditText = new EditText(game);
    userNameEditText.setSingleLine();
    userNameEditText.setText(game.getString(R.string.button_hall_of_fame_multigame_fan));
    if (!(("").equals(GameSharedPref.getLastHofName()))) {
      userNameEditText.setText(GameSharedPref.getLastHofName());
    }
    userNameEditText.setSelection(userNameEditText.getText().length());

    final Activity act = game;
    final int score = game.getScore();
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        Intent intent;
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            /* Showing so that animation does not look that bad when moving to other activity */
            showStatusBar(game);

            MgTracker.trackGameWinnerOkButtonPushed();
            String playerName = userNameEditText.getText().toString();
            GameSharedPref.setLastHofName(playerName);

            HofDatabaseCenter db = HofDatabaseCenter.getsHofDb();
            int playerPosition = db.writeIntoHallOfFame(new HofItem(playerName, game.getScore()));
            if (GameSharedPref.getHighestScore() < game.getScore()) {
              GameSharedPref.setHighestScore(game.getScore());
              GameSharedPref.setHighestScoreSubmitted(false);
            }
            intent = new Intent(act, HallOfFameActivity.class);
            intent.putExtra(HallOfFameActivity.SCROLL_TO_POSITION, playerPosition);
            game.startActivity(intent);
            break;
          case DialogInterface.BUTTON_NEGATIVE:
            MgTracker.trackGameWinnerRetryButtonPushed();
            restartGame(game);
            break;
          case DialogInterface.BUTTON_NEUTRAL:
            MgTracker.trackGameWinnerShareButtonPushed();
            if (InternetChecker.isNetworkAvailable(game)) {
              MainMenuActivity.setOfferHighScore(game.getScore());
              FacebookSharer.shareScoreToFacebook(score, true);
              game.finish();
            } else {
              askUserToConnect(game, true, score);
            }
            break;
        }
      }
    };
    AlertDialog.Builder builder = new AlertDialog.Builder(game);
    builder.setCancelable(false).setView(userNameEditText).setTitle(game.getString(
        R.string.game_winner_title)).
        setMessage(game.getString(R.string.game_winner)).setPositiveButton(Html.fromHtml(
        "<b>" + "OK" + "</b>"), dialogClickListener).setNegativeButton(game.getString(
        R.string.game_retry), dialogClickListener).setNeutralButton("Share", dialogClickListener)
        .show();
  }

  public static void showWinnerDialogAfterShareWindow(final MainMenuActivity act, final int score) {

    final EditText userNameEditText = new EditText(act);
    userNameEditText.setSingleLine();
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        Intent intent;
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            String playerName = userNameEditText.getText().toString();
            if (playerName.equals("")) {
              playerName = act.getString(R.string.button_hall_of_fame_multigame_fan);
            }

            HofDatabaseCenter db = HofDatabaseCenter.getsHofDb();
            db.writeIntoHallOfFame(new HofItem(playerName, score));
            intent = new Intent(act, HallOfFameActivity.class);
            act.startActivity(intent);
            break;
          case DialogInterface.BUTTON_NEGATIVE:
            break;
        }
      }
    };
    AlertDialog.Builder builder = new AlertDialog.Builder(act);
    builder.setCancelable(false).setView(userNameEditText).setMessage(act.getString(
        R.string.game_ask_for_name)).setPositiveButton(Html.fromHtml("<b>" + "OK" + "</b>"),
        dialogClickListener).setNegativeButton(act.getString(R.string.cancel), dialogClickListener)
        .show();
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

  //TODO L Toto nejak na oldschoola genymotione s malou pamatou padalo (recreovanie activity)
  @SuppressLint("NewApi")
  private static void restartGame(GameActivity game) {
    int apiVersion = Build.VERSION.SDK_INT;
    if (apiVersion >= 11) {
      game.recreate();
    } else {
      Intent intent = game.getIntent();
      game.finish();
      game.startActivity(intent);
    }
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
