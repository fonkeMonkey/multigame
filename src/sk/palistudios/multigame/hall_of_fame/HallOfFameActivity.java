package sk.palistudios.multigame.hall_of_fame;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;
import sk.palistudios.multigame.BaseActivity;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.hall_of_fame.leaderboard.GetLeaderboardAsyncTask;
import sk.palistudios.multigame.hall_of_fame.leaderboard.GetLeaderboardCallback;
import sk.palistudios.multigame.tools.DisplayHelper;
import sk.palistudios.multigame.tools.SkinManager;

/**
 * @author Pali
 */
public class HallOfFameActivity extends BaseActivity implements GoogleApiClient
    .ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GetLeaderboardCallback {

  private static final int REQUEST_SIGN_IN = 9001;

  // Client used to interact with Google APIs
  private GoogleApiClient mGoogleApiClient;

  private String mPlayerIdentifier;

  private boolean mResolvingConnectionFailure = false;
  private boolean mAutoStartSignInflow = true;
  private boolean mSignInClicked = false;

  private GetLeaderboardAsyncTask mGetLeaderboardAsyncTask;

  private boolean mFirstConnect;

  private HofArrayAdapter mLocalLeaderboardAdapter;
  private HofArrayAdapter mOnlineLeaderboardAdapter;

  private ViewGroup mSignInBar;
  private ViewGroup mSignOutBar;

  private SignInButton mSignInButton;
  private Button mSignOutButton;

  private TextView mSignInMessage;
  private TextView mSignOutMessage;

  private ListView mListView;
  private TextView mHeader;

  public static ProgressDialog mRingProgressDialog = null;
  private ToggleButton mSwitchLocal;
  private ToggleButton mSwitchOnline;
  private LinearLayout mSwitchLayout;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    if (!GameSharedPref.getDbInitialized()) {
      mRingProgressDialog = ProgressDialog.show(HallOfFameActivity.this, "Please wait..",
          "Initializing database..", true);
      mRingProgressDialog.setCancelable(true);
    }

    setContentView(R.layout.hof_layout);
    initGoogleApiCLient();
    initViews();
    fillData();
  }

  private void initGoogleApiCLient() {
    mFirstConnect = true;

    // Create the Google API Client with access to Plus and Games
    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
        .addApi(Games.API).addScope(Games.SCOPE_GAMES)
        .build();
  }

  private void initViews() {
    mHeader = (TextView) findViewById(R.id.hof_header);
    mListView = (ListView) findViewById(R.id.hof_list);

    mSwitchLayout = (LinearLayout) findViewById(R.id.hof_toggle_layout);
    mSwitchLocal = (ToggleButton) findViewById(R.id.hof_toggle_local);
    mSwitchOnline = (ToggleButton) findViewById(R.id.hof_toggle_online);

    mSignInBar = (LinearLayout) findViewById(R.id.sign_in_bar);
    mSignOutBar = (LinearLayout) findViewById(R.id.sign_out_bar);

    mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
    mSignOutButton = (Button) findViewById(R.id.sign_out_button);

    mSignInMessage = (TextView) findViewById(R.id.sign_in_message);
    mSignOutMessage = (TextView) findViewById(R.id.sign_out_message);

    mSignInButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mSignInClicked = true;
        mGoogleApiClient.connect();
      }
    });

    mSignOutButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mSignInClicked = false;
        Games.signOut(mGoogleApiClient);
        if (isConnected()) {
          mGoogleApiClient.disconnect();
        }
        refreshSignInLayout(true);
      }
    });

    mSwitchLocal.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mSwitchOnline.toggle();
      }
    });
    mSwitchOnline.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mSwitchLocal.toggle();
      }
    });

    mSwitchLocal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        buttonView.setClickable(!isChecked);
        if(isChecked) {
          mListView.setAdapter(mLocalLeaderboardAdapter);
        }
        refreshSignInLayout(!isChecked);
      }
    });

    mSwitchOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        buttonView.setClickable(!isChecked);
        if(isChecked) {
          mListView.setAdapter(mOnlineLeaderboardAdapter);
        }

        if(mFirstConnect && !isConnected()) {
          mGoogleApiClient.connect();
          mFirstConnect = false;
        }
      }
    });

    mSwitchLocal.setClickable(!mSwitchLocal.isChecked());
    mSwitchOnline.setClickable(!mSwitchOnline.isChecked());
  }

  private void fillData() {
    //cucni databazu
    final HofDatabaseCenter mHofDb = new HofDatabaseCenter(this);
    mHofDb.open();
    final List<HofItem> dbRows = mHofDb.fetchAllRows();
    mHofDb.close();

    //urob svoje itemy
    HofItem[] rows = new HofItem[dbRows.size()];
    for (int i = 0; i < dbRows.size(); i++) {
      rows[i] = dbRows.get(i);
      rows[i].setPosition(i + 1);
    }

    //adaptery sracky etc
    mLocalLeaderboardAdapter = new HofArrayAdapter(this, rows, false, null);
    mListView.setAdapter(mLocalLeaderboardAdapter);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    if (requestCode == REQUEST_SIGN_IN) {
      mSignInClicked = false;
      mResolvingConnectionFailure = false;
      if (resultCode == RESULT_OK) {
        mGoogleApiClient.connect();
      } else {
        BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.signin_other_error);
      }
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if(mGetLeaderboardAsyncTask != null) {
      mGetLeaderboardAsyncTask.cancel(true);
    }

    if (isConnected()) {
      mGoogleApiClient.disconnect();
    }
  }

  private boolean isConnected() {
    return (mGoogleApiClient != null && mGoogleApiClient.isConnected());
  }

  @Override
  public void onConnected(Bundle bundle) {
    refreshSignInLayout(mSwitchOnline.isChecked());

    final Player player = Games.Players.getCurrentPlayer(mGoogleApiClient);
    if (player != null) {
      mPlayerIdentifier = player.getPlayerId();
    }

    final boolean shouldSubmit = ((GameSharedPref.getHighestScore() > 0) && !GameSharedPref
        .getHighestScoreSubmitted());
    if (shouldSubmit) {
      Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.google_play_leaderboard_id),
          GameSharedPref.getHighestScore());
      GameSharedPref.setHighestScoreSubmitted(true);
    }

    mGetLeaderboardAsyncTask = new GetLeaderboardAsyncTask(this,
        mGoogleApiClient, this);
    mGetLeaderboardAsyncTask.execute();
  }

  @Override
  public void onConnectionSuspended(int i) {
    mGoogleApiClient.connect();
  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
    if (mResolvingConnectionFailure) {
      // already resolving
      return;
    }

    // if the sign-in button was clicked or if auto sign-in is enabled,
    // launch the sign-in flow
    if (mSignInClicked || mAutoStartSignInflow) {
      mAutoStartSignInflow = false;
      mSignInClicked = false;
      mResolvingConnectionFailure = true;

      // Attempt to resolve the connection failure using BaseGameUtils.
      // The R.string.signin_other_error value should reference a generic
      // error string in your strings.xml file, such as "There was
      // an issue with sign-in, please try again later."
      if (!BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient, connectionResult,
          REQUEST_SIGN_IN, getString(R.string.signin_other_error))) {
        mResolvingConnectionFailure = false;
      }
    }
  }

  public void refreshSignInLayout(boolean onlineChecked) {
    if(onlineChecked) {
      if (isConnected()) {
        mSignInBar.setVisibility(View.GONE);
        mSignOutBar.setVisibility(View.VISIBLE);
      } else {
        mSignOutBar.setVisibility(View.GONE);
        mSignInBar.setVisibility(View.VISIBLE);
      }
    } else {
      mSignInBar.setVisibility(View.GONE);
      mSignOutBar.setVisibility(View.GONE);
    }
  }

  @Override
  public void onGetLeaderboardFinish(List<LeaderboardScore> scoresList) {
    if (!scoresList.isEmpty()) {
      final int size = scoresList.size();
      final HofItem[] items = new HofItem[size];
      LeaderboardScore score;
      for(int i = 0; i < size; i++) {
        score = scoresList.get(i);
        long rawScore = score.getRawScore();
        if(rawScore > Integer.MAX_VALUE) {
          rawScore = Integer.MAX_VALUE;
        }
        items[i] = new HofItem(score.getScoreHolderDisplayName(), (int) rawScore);
        items[i].setPosition((int)score.getRank());
        items[i].setGooglePlayerIdentifier(score.getScoreHolder().getPlayerId());
      }
      mOnlineLeaderboardAdapter = new HofArrayAdapter(this, items, true, mPlayerIdentifier);
      mListView.setAdapter(mOnlineLeaderboardAdapter);
    } else {
      Toast.makeText(this, getString(R.string.leaderboard_no_data), Toast.LENGTH_LONG).show();
    }
  }

  @Override
  public void reskinLocally(SkinManager.Skin currentSkin) {
    mHeader.setTextColor(mHeader.getTextColors().withAlpha(DisplayHelper.ALPHA_80pc));

    switch (currentSkin) {
      case QUAD:
        mSwitchLayout.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_layout_quad));
        mSwitchLocal.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_quad));
        mSwitchLocal.setTextColor(getResources().getColorStateList(
            R.color.xml_text_hof_switch_quad));
        mSwitchOnline.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_quad));
        mSwitchOnline.setTextColor(getResources().getColorStateList(
            R.color.xml_text_hof_switch_quad));
        if(mSignInMessage != null) {
          mSignInBar.setBackgroundColor(getResources().getColor(android.R.color.white));
          mSignOutBar.setBackgroundColor(getResources().getColor(android.R.color.white));
//          mSignOutButton.setBackgroundResource(R.drawable.xml_bg_hof_sign_out_button_quad);
          mSignOutButton.setTextColor(getResources().getColor(android.R.color.white));
          mSignInMessage.setTextColor(getResources().getColor(android.R.color.black));
          mSignOutMessage.setTextColor(getResources().getColor(android.R.color.black));
        }
        break;
      case THRESHOLD:
        mSwitchLayout.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_layout_thres));
        mSwitchLocal.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_thres));
        mSwitchLocal.setTextColor(getResources().getColorStateList(
            R.color.xml_text_hof_switch_thres));
        mSwitchOnline.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_thres));
        mSwitchOnline.setTextColor(getResources().getColorStateList(
            R.color.xml_text_hof_switch_thres));
        if(mSignInMessage != null) {
          mSignInBar.setBackgroundColor(getResources().getColor(android.R.color.white));
          mSignOutBar.setBackgroundColor(getResources().getColor(android.R.color.white));
//          mSignOutButton.setBackgroundResource(R.drawable.xml_bg_hof_sign_out_button_thres);
          mSignOutButton.setTextColor(getResources().getColor(android.R.color.white));
          mSignInMessage.setTextColor(getResources().getColor(android.R.color.black));
          mSignOutMessage.setTextColor(getResources().getColor(android.R.color.black));
        }
        break;
      case DIFFUSE:
        mSwitchLayout.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_layout_diffuse));
        mSwitchLocal.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_diffuse));
        mSwitchLocal.setTextColor(getResources().getColorStateList(
            R.color.xml_text_hof_switch_diffuse));
        mSwitchOnline.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_diffuse));
        mSwitchOnline.setTextColor(getResources().getColorStateList(
            R.color.xml_text_hof_switch_diffuse));
        if(mSignInMessage != null) {
          mSignInBar.setBackgroundColor(getResources().getColor(android.R.color.white));
          mSignOutBar.setBackgroundColor(getResources().getColor(android.R.color.white));
//          mSignOutButton.setBackgroundResource(R.drawable.xml_bg_hof_sign_out_button_diffuse);
          mSignOutButton.setTextColor(getResources().getColor(android.R.color.white));
          mSignInMessage.setTextColor(getResources().getColor(android.R.color.black));
          mSignOutMessage.setTextColor(getResources().getColor(android.R.color.black));
        }
        break;
      case CORRUPTED:
        mSwitchLayout.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_layout_corrupt));
        mSwitchLocal.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_corrupt));
        mSwitchLocal.setTextColor(getResources().getColorStateList(
            R.color.xml_text_hof_switch_corrupt));
        mSwitchOnline.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_corrupt));
        mSwitchOnline.setTextColor(getResources().getColorStateList(
            R.color.xml_text_hof_switch_corrupt));
        if(mSignInMessage != null) {
          mSignInBar.setBackgroundColor(getResources().getColor(android.R.color.white));
          mSignOutBar.setBackgroundColor(getResources().getColor(android.R.color.white));
//          mSignOutButton.setBackgroundResource(R.drawable.xml_bg_hof_sign_out_button_corrupt);
          mSignOutButton.setTextColor(getResources().getColor(android.R.color.white));
          mSignInMessage.setTextColor(getResources().getColor(android.R.color.black));
          mSignOutMessage.setTextColor(getResources().getColor(android.R.color.black));
        }
        break;
    }
  }
}