package sk.palistudios.multigame.tools.internet;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import sk.palistudios.multigame.R;

public class LeaderBoardActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE_NEVIEM = 69;
    private static final int REQUEST_LEADERBOARD = 17;
    private final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1009;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    private String mLeaderBoardId;

    GoogleApiClient mClient;
    private boolean mResolvingError;


    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        mLeaderBoardId = getString(R.string.main_leaderboard);

        // create an instance of Google API client and specify the Play services
        // and scopes to use. In this example, we specify that the app wants
        // access to the Games, Plus, and Cloud Save services and scopes.
        GoogleApiClient.Builder builder =
                new GoogleApiClient.Builder(this, this, this);
        builder.addApi(Games.API)
                .addApi(Plus.API)
                .addScope(Games.SCOPE_GAMES)
                .addScope(Plus.SCOPE_PLUS_LOGIN);
//                .addApi(AppStateManager.API)
//                .addScope(AppStateManager.SCOPE_APP_STATE);
        mClient = builder.build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        int gpServicesResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (gpServicesResult != ConnectionResult.SUCCESS && GooglePlayServicesUtil.isUserRecoverableError(gpServicesResult)) {
//            android.app.Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(gpServicesResult, this, REQUEST_CODE_RECOVER_PLAY_SERVICES);
//            if (errorDialog != null) {
            showErrorDialog(gpServicesResult);
            mResolvingError = true;
//            }
        }

        if (!mClient.isConnected()) {
            mClient.connect();
            Log.i("Multigame", "connecting");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("Multigame", "connected");
        if(mClient.isConnected()){
            Games.Leaderboards.submitScore(mClient, mLeaderBoardId, 12345);
            /* Show table */
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mClient,
                    mLeaderBoardId), REQUEST_LEADERBOARD);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Multigame", "suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("Multigame", "failed " + connectionResult.toString());

        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        }
        if (connectionResult.hasResolution() == true) {
            mResolvingError = true;
            try {
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                Log.i("Multigame", "SendIntentException " + connectionResult.toString());
                mClient.connect();
            }
        }
        else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
    }

    @Override
    protected void onStop() {
        mClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == ConnectionResult.SIGN_IN_REQUIRED && resultCode == RESULT_OK) {
//            if (mClient != null) {
//                mClient.connect();
//            }
//        }
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                if (!mClient.isConnecting() &&
                        !mClient.isConnected()) {
                    mClient.connect();
                }
            }
        }
    }

    //TODO virdzek sign out
//        @Override
//        public void onClick(View view) {
//            if (view.getId() == R.id.sign_out_button) {
//                // user explicitly signed out, so turn off auto sign in
//                mExplicitSignOut = true;
//                mClient.signOut(this);
//            }
//            // ...
//        }

    // The rest of this code is all about building the error dialog

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment(this);
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {

    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        private LeaderBoardActivity mActivity;

        public ErrorDialogFragment(LeaderBoardActivity activity) {
            mActivity = activity;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            if (isAdded()) {
                mActivity.mResolvingError = false;
            }
            mActivity = null;
        }
    }
}
