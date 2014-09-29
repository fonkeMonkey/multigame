package sk.palistudios.multigame.game;

// @author Pali

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.widget.EditText;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import sk.palistudios.multigame.MgApplication;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.mgc.MinigamesCenterListActivity;
import sk.palistudios.multigame.game.minigames.MinigamesManager;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.hall_of_fame.HallOfFameActivity;
import sk.palistudios.multigame.hall_of_fame.HofDatabaseCenter;
import sk.palistudios.multigame.hall_of_fame.HofItem;
import sk.palistudios.multigame.mainMenu.DebugSettings;
import sk.palistudios.multigame.mainMenu.MainMenuActivity;
import sk.palistudios.multigame.tools.internet.FacebookSharer;
import sk.palistudios.multigame.tools.internet.InternetChecker;

public class GameDialogs {

  public final static int ASK_USER_TO_CONNECT = 1;
  public static boolean sLostGame = true;

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
    builder.setMessage(game.getString(R.string.tutorial_welcome))
        //        .setNegativeButton(game.getString(R.string.tutorial_negative),
        // dialogClickListener)
        .setPositiveButton(Html.fromHtml("<b>" + game.getString(R.string.tutorial_positive) +
            "</b>"), dialogClickListener).show().setCanceledOnTouchOutside(false);
  }

  public static void showNextTutorialWindow(final GameActivity game, boolean showPopup) {
    MinigamesManager.deactivateAllMiniGames(game);
    if (!GameDialogs.sLostGame) {
      GameActivity.sTutorialLastLevel++;
    }
    sLostGame = false;

    if (showPopup) {
      DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
              game.startGameTutorial();
              break;
            //            case DialogInterface.BUTTON_NEGATIVE:
            //              game.stopTutorial();
            //              game.finish();
            //              break;
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
          symbol =
              "<font color=#D98179><b>(" + MinigamesCenterListActivity.SYMBOL_MINIGAME_VERTICAL +
                  ")</b></font>";
          break;
        case 1:
          symbol =
              "<font color=#D98179><b>(" + MinigamesCenterListActivity.SYMBOL_MINIGAME_HORIZONTAL +
                  ")</b></font>";
          break;
        case 2:
        case 3:
          symbol = "<font color=#D98179><b>(" + MinigamesCenterListActivity.SYMBOL_MINIGAME_TOUCH +
              ")</b></font>";
          break;

      }
      String title = symbol + game.getString(R.string.minigame) +
          (GameActivity.sTutorialLastLevel + 1) + ": " +
          MinigamesManager.getMinigamesObjects()[GameActivity.sTutorialLastLevel].getName();

      String message =
          MinigamesManager.getMinigamesObjects()[GameActivity.sTutorialLastLevel].getDescription(
              game);
      builder.setTitle(Html.fromHtml(title));
      builder.setMessage(Html.fromHtml(message)).setPositiveButton(Html.fromHtml(
          "<b>" + game.getString(R.string.tutorial_try) + "</b>"), dialogClickListener)
          //          .setNegativeButton(game.getString(R.string.tutorial_negative),
          // dialogClickListener)
          .show().setCanceledOnTouchOutside(false);
    } else {
      game.startGameTutorial();
    }
  }

  public static void showTutorialWinnerWindow(final GameActivity game) {
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
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            //                        SoundEffectsCenter.playForwardSound(game);
            sLostGame = true;
            //                        Game.sTutorialLastLevel--;
            //                        showNextTutorialWindow(game, false);
            game.stopTutorial();
            GameActivity.sTutorialRestart = true;
            restartGame(game);
            break;
          //                    case DialogInterface.BUTTON_NEGATIVE:
          //                        Intent intent = new Intent(game.getApplicationContext(),
          // MainMenu.class);
          //                        game.startActivity(intent);
          //                        break;
        }
      }
    };
    game.mMusicPlayer.stopMusic();

    AlertDialog.Builder builder = new AlertDialog.Builder(game);
    builder.setCancelable(false).setTitle(game.getString(
        R.string.tutorial_failed_title)).setMessage(game.getString(
        R.string.tutorial_failed)).setPositiveButton(Html.fromHtml("<b>OK</b>"),
        dialogClickListener)
        //                //.setNegativeButton(game.getString(R.string.tutorial_negative),
        // dialogClickListener)
        .show();

  }

  public static void showLoserDialogWindow(final GameActivity game) {
    //        final Activity mainMenu = MainMenu.getInstance();
    if (DebugSettings.alwaysWinner) {
      showWinnerDialogWindow(game);
      return;
    }
    final int score = game.getScore();

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            restartGame(game);
            break;

          case DialogInterface.BUTTON_NEUTRAL:
            if (InternetChecker.isNetworkAvailable(game)) {

              FacebookSharer.shareScoreToFacebook(score, false);
              game.finish();
            } else {
              askUserToConnect(game, false, score);
            }
            break;

          case DialogInterface.BUTTON_NEGATIVE:
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
    Tracker t = ((MgApplication)(game.getApplication())).getTracker();
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
            String playerName = userNameEditText.getText().toString();
            GameSharedPref.setLastHofName(playerName);

            HofDatabaseCenter db = HofDatabaseCenter.getHofDb();
            db.writeIntoHallOfFame(new HofItem(playerName, game.getScore()));
            intent = new Intent(act, HallOfFameActivity.class);
            game.startActivity(intent);

            break;
          case DialogInterface.BUTTON_NEGATIVE:
            restartGame(game);
            break;
          case DialogInterface.BUTTON_NEUTRAL:
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

            HofDatabaseCenter db = HofDatabaseCenter.getHofDb();
            //                        db.open();
            db.writeIntoHallOfFame(new HofItem(playerName, score));
            //                        game.hofDb.close();
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
    //        AlertDialog dialog = builder.create();
    //        dialog.show();
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
              //                            game.hasDialogShown = false;
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
}
