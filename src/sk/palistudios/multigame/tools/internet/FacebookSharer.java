package sk.palistudios.multigame.tools.internet;

// @author Pali

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.WebDialog;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.GameDialogs;
import sk.palistudios.multigame.mainMenu.MainMenuActivity;
import sk.palistudios.multigame.tools.Toaster;

public class FacebookSharer {

  public final static String TAG = "FB Share";
  private static WebDialog dialog;
  private UiLifecycleHelper uiHelper;

  public static void shareScoreToFacebook(final int score, final boolean isWinner) {
    final MainMenuActivity mainMenu = MainMenuActivity.getInstance();
//    MainMenuActivity.setMainMenuFacebook(mainMenu);

    Session.openActiveSession(mainMenu, true, new Session.StatusCallback() {
      public void call(Session session, SessionState state, Exception exception) {

        //Keď som sa logol
        if (state == SessionState.OPENED) {
          Log.i("facebookState", "OPENED");
          openSessionAndPublish(mainMenu, score, isWinner);
          return;
        }

        if (state == SessionState.CLOSED_LOGIN_FAILED) {
          Log.e("facebookState", "failed");
          Log.e("facebookState", exception.getLocalizedMessage());
          Toaster.toastLong(mainMenu.getString(R.string.facebook_login_failed), mainMenu);
        }

        //                //Asi už mám permissiony
        //                if (state == SessionState.OPENED_TOKEN_UPDATED) {
        //                    publishStoryNew(game_layout);
        //                    return;
        //                }
      }
    });

  }

  private static void openSessionAndPublish(final Activity act, final int score,
      final boolean isWinner) {
    Session.openActiveSession(act, true, new Session.StatusCallback() {
      public void call(Session session, SessionState state, Exception exception) {
        Log.d(TAG, state.toString());
        if (state == SessionState.OPENED) {
          publishStoryNew(act, score, isWinner);
        } else if (state == SessionState.CLOSED_LOGIN_FAILED) {
          //					Toast.makeText(FacebookPostToFeedActivity.this, "Login failed.",
          // Toast.LENGTH_LONG)
          //							.show();
          //                    act.finish();
        }
      }
    });
  }

  private static void publishStoryNew(final Activity act, final int score, final boolean isWinner) {
    String name = act.getString(R.string.facebook_label);
    String caption = "http://play.google.com/store/apps/details?id=sk.palistudios.multigame";
    String description = act.getString(R.string.facebook_description1) + score + act.getString(
        R.string.facebook_description2);
    String picture = "http://s3.postimg.org/jks1t6zjj/logo.png";
    String link = "http://play.google.com/store/apps/details?id=sk.palistudios.multigame";

    Bundle params = new Bundle();
    params.putString("name", name);
    params.putString("caption", caption);
    params.putString("description", description);
    params.putString("link", link);
    params.putString("picture", picture);

    WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(act, Session.getActiveSession(),
        params)).setOnCompleteListener(new WebDialog.OnCompleteListener() {
      @Override
      public void onComplete(Bundle values, FacebookException error) {

        //TODO virdzek handle errors
        if (error == null) {
          // When the story is posted, echo the success
          // and the post Id.
          final String postId = values.getString("post_id");
          if (postId != null) {
            Log.d(TAG, "Posted");
            MainMenuActivity.setWallPostAchievementDone();
            //						Toast.makeText(FacebookPostToFeedActivity.this, "Posted",
            // Toast.LENGTH_LONG).show();
          } else {
            Log.d(TAG, "Canceled");
            //						Toast.makeText(FacebookPostToFeedActivity.this, "Canceled",
            // Toast.LENGTH_LONG).show();
          }
        } else if (error instanceof FacebookOperationCanceledException) {
          Log.d(TAG, "X button");
          //					Toast.makeText(FacebookPostToFeedActivity.this, "Closed",
          // Toast.LENGTH_LONG).show();
        } else {
          GameDialogs.askUserToConnect(act, isWinner, score);
          //                    Log.d(TAG, "Network error");
          //					Toast.makeText(act, "Sorry, there does not appear to be working internet
          // connection available.", Toast.LENGTH_LONG)
          //							.show();
        }
        //                act.finish();
      }
    }).build();
    feedDialog.show();
  }

  private static void showDialogWithoutNotificationBar(Activity act, String action, Bundle params) {
    dialog = new WebDialog.Builder(act, Session.getActiveSession(), action, params).
        setOnCompleteListener(new WebDialog.OnCompleteListener() {
          @Override
          public void onComplete(Bundle values, FacebookException error) {
            if (error != null && !(error instanceof FacebookOperationCanceledException)) {
              //                    ((HomeActivity) getActivity()).
              //                            showError(getResources().getString(R.string
              // .network_error), false);
            }

            dialog = null;
            MainMenuActivity.setWallPostAchievementDone();
            //                dialogAction = null;
            //                dialogParams = null;
          }
        }).build();

    Window dialog_window = dialog.getWindow();
    dialog_window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

    //        dialogAction = action;
    //        dialogParams = params;

    dialog.show();

  }

  public static void logout(Context context) {
    Session session = Session.getActiveSession();
    if (session != null) {

      if (!session.isClosed()) {
        session.closeAndClearTokenInformation();
        //clear your preferences if saved
      }
    } else {

      session = new Session(context);
      Session.setActiveSession(session);

      session.closeAndClearTokenInformation();
      //clear your preferences if saved

    }
  }
}
