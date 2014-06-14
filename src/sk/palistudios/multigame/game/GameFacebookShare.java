package sk.palistudios.multigame.game;

// @author Pali

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import com.facebook.*;
import com.facebook.widget.WebDialog;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.mainMenu.MainMenuActivity;
import sk.palistudios.multigame.tools.Toaster;

public class GameFacebookShare {

    public final static String TAG = "FB Share";
    //    private static void publishStoryNew(Activity act, int score) {
//        // This function will invoke the Feed Dialog to post to a user's Timeline and News Feed
//// It will attempt to use the Facebook Native Share dialog
//// If that's not supported we'll fall back to the web based dialog.
//
////        GraphUser currentFBUser = application.getCurrentFBUser();
//
//// This first parameter is used for deep linking so that anyone who clicks the link will start smashing this user
//// who sent the post
////        String link = "https://apps.facebook.com/friendsmashsample/?challenge_brag=";
////        if (currentFBUser != null) {
////            link += currentFBUser.getId();
////        }
//
//// Define the other parameters
//        String name = "Check out my multitasking skills!";
//        String caption = "http://play.google.com/store/apps/details?id=sk.palistudios.multigame";
//        String description = "I just achieved incredible " + score + " points in Multigame! Can you beat that?";
//        String picture = "http://s24.postimg.org/9jb0yf9wh/logo_summer.png";
//        String link = "http://play.google.com/store/apps/details?id=sk.palistudios.multigame";
//
//
//        if (FacebookDialog.canPresentShareDialog(act, FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
//
//            // Create the Native Share dialog
//            FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(act)
//                    .setLink(link)
//                    .setName(name)
//                    .setCaption(caption)
//                    .setPicture(picture)
//                    .build();
//            MainMenu.setWallPostAchievementDone();
//            // Show the Native Share dialog
////        ((HomeActivity)(act)).getFbUiLifecycleHelper().trackPendingDialogCall(shareDialog.present());
//        } else {
//
//            // Prepare the web dialog parameters
//            Bundle params = new Bundle();
////            params.putString("link", link);
//            params.putString("name", name);
//            params.putString("caption", caption);
//            params.putString("description", description);
//            params.putString("picture", picture);
//            params.putString("link", link);
//
//            // Show FBDialog without a notification bar
//            showDialogWithoutNotificationBar(act, "feed", params);
//        }
//
//    }
    private static WebDialog dialog;
//    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
//    private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
//    private static boolean pendingPublishReauthorization = false;
    private UiLifecycleHelper uiHelper;

    public static void shareScoreToFacebook(final int score, final boolean isWinner) {
        final MainMenuActivity mainMenu = MainMenuActivity.getInstance();
        MainMenuActivity.setMainMenuFacebook(mainMenu);

        Session.openActiveSession(mainMenu, true, new Session.StatusCallback() {
            public void call(Session session, SessionState state, Exception exception) {
//                mainMenu.onSessionStateChange(session, state, exception);
//                session = Session.getActiveSession();
//                askForLogin(act, session);
//                publishStory(act);

//                if ( //                        pendingPublishReauthorization
                //                        &&
                //                        state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
//                        state.equals(SessionState.OPENED)) {
//                if (session.isOpened()) {

                //Keď som sa logol
                if (state == SessionState.OPENED) {
                    Log.i("facebookState", "OPENED");
//                    requestMorePermissions(mainMenu, score);
//                    publishStoryNew(mainMenu, score);
//                    Context context = mainMenu.getApplicationContext();
//                    Intent intent = new Intent(context, MainMenu.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    mainMenu.startActivity(intent);
                    openSessionAndPublish(mainMenu, score, isWinner);
                    return;
//                }
                }

//                //Keď som dostal viac permissions
//                if (state == SessionState.OPENED_TOKEN_UPDATED) {
//                    Log.e("facebookState", "OPENED + UPDATED");
//                    publishStoryNew(mainMenu, score);
//                    return;
//                }

                if (state == SessionState.CLOSED_LOGIN_FAILED) {
                    Log.e("facebookState", "failed");
                    Log.e("facebookState", exception.getLocalizedMessage());
                    Toaster.toastLong(mainMenu.getString(R.string.facebook_login_failed), mainMenu);
                }

//                //Asi už mám permissiony
//                if (state == SessionState.OPENED_TOKEN_UPDATED) {
//                    publishStoryNew(game);
//                    return;
//                }
            }
        });


    }

    private static void openSessionAndPublish(final Activity act, final int score, final boolean isWinner) {
        Session.openActiveSession(act, true, new Session.StatusCallback() {
            public void call(Session session, SessionState state, Exception exception) {
                Log.d(TAG, state.toString());
                if (state == SessionState.OPENED) {
                    publishStoryNew(act, score, isWinner);
                } else if (state == SessionState.CLOSED_LOGIN_FAILED) {
//					Toast.makeText(FacebookPostToFeedActivity.this, "Login failed.", Toast.LENGTH_LONG)
//							.show();
//                    act.finish();
                }
            }
        });
    }

    private static void publishStoryNew(final Activity act, final int score, final boolean isWinner) {
        String name = act.getString(R.string.facebook_label);
        String caption = "http://play.google.com/store/apps/details?id=sk.palistudios.multigame";
        String description = act.getString(R.string.facebook_description1) + score + act.getString(R.string.facebook_description2);
        String picture = "http://s24.postimg.org/9jb0yf9wh/logo_summer.png";
        String link = "http://play.google.com/store/apps/details?id=sk.palistudios.multigame";

        Bundle params = new Bundle();
        params.putString("name", name);
        params.putString("caption", caption);
        params.putString("description",
                description);
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
//						Toast.makeText(FacebookPostToFeedActivity.this, "Posted", Toast.LENGTH_LONG).show();
                    } else {
                        Log.d(TAG, "Canceled");
//						Toast.makeText(FacebookPostToFeedActivity.this, "Canceled", Toast.LENGTH_LONG).show();
                    }
                } else if (error instanceof FacebookOperationCanceledException) {
                    Log.d(TAG, "X button");
//					Toast.makeText(FacebookPostToFeedActivity.this, "Closed", Toast.LENGTH_LONG).show();
                } else {
                    GameDialogs.askUserToConnect(act, isWinner, score);
//                    Log.d(TAG, "Network error");
//					Toast.makeText(act, "Sorry, there does not appear to be working internet connection available.", Toast.LENGTH_LONG)
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
//                            showError(getResources().getString(R.string.network_error), false);
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
