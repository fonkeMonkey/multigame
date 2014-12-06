package sk.palistudios.multigame.hall_of_fame;

import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.plus.Plus;
import sk.palistudios.multigame.BaseActivity;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.tools.DisplayHelper;
import sk.palistudios.multigame.tools.SkinManager;

/**
 * @author Pali
 */
public class HallOfFameActivity extends BaseActivity implements GoogleApiClient
    .ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

  // Client used to interact with Google APIs
  private GoogleApiClient mGoogleApiClient;

  private boolean mFirstConnect;

  private HofArrayAdapter mLocalLeaderboardAdapter;
  private HofArrayAdapter mOnlineLeaderboardAdapter;

  private ViewGroup mSignInBar;
  private ViewGroup mSignOutBar;

  private SignInButton mSignInButton;
  private Button mSignOutButton;

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

    mSignInButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mGoogleApiClient.connect();
      }
    });

    mSignOutButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Games.signOut(mGoogleApiClient);
        if (mGoogleApiClient.isConnected()) {
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

        if(mFirstConnect && mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
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
    mLocalLeaderboardAdapter = new HofArrayAdapter(this, rows);
    mListView.setAdapter(mLocalLeaderboardAdapter);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if (mGoogleApiClient.isConnected()) {
      mGoogleApiClient.disconnect();
    }
  }

  @Override
  public void onConnected(Bundle bundle) {
    refreshSignInLayout(mSwitchOnline.isChecked());
  }

  @Override
  public void onConnectionSuspended(int i) {
    mGoogleApiClient.connect();
  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
    Toast.makeText(this, getString(R.string.signin_other_error), Toast.LENGTH_LONG).show();
  }

  public void refreshSignInLayout(boolean onlineChecked) {
    if(onlineChecked) {
      if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
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
  protected void reskinLocally(SkinManager.Skin currentSkin) {
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
        break;
    }
  }
}