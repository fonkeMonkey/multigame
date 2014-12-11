package sk.palistudios.multigame.hall_of_fame.leaderboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardScoreBuffer;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import sk.palistudios.multigame.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Responsible for getting top scores from online leaderboard.
 *
 * @author Patrik Mucha
 */
public class GetLeaderboardAsyncTask extends AsyncTask<Void, Void, List<LeaderboardScore>> {

  /**
   * Timeout in seconds for getting online scores.
   */
  private static final int TIMEOUT_SECONDS = 30;

  /**
   * The number of top online players to download.
   */
  private static final int TOP_SCORES_COUNT = 10;

  private Context mContext;
  private GoogleApiClient mGoogleApiClient;
  private GetLeaderboardCallback mCallback;

  private ProgressDialog mProgressDialog;

  public GetLeaderboardAsyncTask(Context context, GoogleApiClient googleApiClient,
      GetLeaderboardCallback callback) {
    mContext = context;
    mGoogleApiClient = googleApiClient;
    mCallback = callback;

    mProgressDialog = new ProgressDialog(mContext);
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();

    mProgressDialog.setIndeterminate(true);
    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    mProgressDialog.setMessage(mContext.getString(R.string.getting_leaderboard));
    mProgressDialog.setCancelable(false);
    mProgressDialog.show();
  }

  @Override
  protected List<LeaderboardScore> doInBackground(Void... params) {
    final String currentPlayerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
    final PendingResult<Leaderboards.LoadScoresResult> result = Games.Leaderboards.loadTopScores(
        mGoogleApiClient, mContext.getString(R.string.google_play_leaderboard_id),
        LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC,
        TOP_SCORES_COUNT, true);

    final Leaderboards.LoadScoresResult loadScoresResult = result.await(TIMEOUT_SECONDS, TimeUnit
        .SECONDS);

    final LeaderboardScoreBuffer buffer = loadScoresResult.getScores();

    final List<LeaderboardScore> scores = new ArrayList<LeaderboardScore>();

    boolean currentPlayerScoreFound = false;

    final int count = buffer.getCount();
    LeaderboardScore score;
    for(int i = 0; i < count; i++) {
      score = buffer.get(i);
      scores.add(score);
      if(score.getScoreHolder().getPlayerId().equals(currentPlayerId)) {
        currentPlayerScoreFound = true;
      }
    }

    if(!currentPlayerScoreFound) {
      final PendingResult<Leaderboards.LoadPlayerScoreResult> playerResult = Games.Leaderboards
          .loadCurrentPlayerLeaderboardScore(mGoogleApiClient, mContext.getString(
                  R.string.google_play_leaderboard_id), LeaderboardVariant.TIME_SPAN_ALL_TIME,
              LeaderboardVariant.COLLECTION_PUBLIC);
      final Leaderboards.LoadPlayerScoreResult loadPlayerScoresResult = playerResult.await(
          TIMEOUT_SECONDS, TimeUnit.SECONDS);
      scores.add(loadPlayerScoresResult.getScore());
    }

    return scores;
  }

  @Override
  protected void onPostExecute(List<LeaderboardScore> scores) {
    super.onPostExecute(scores);

    mProgressDialog.dismiss();

    if(mCallback != null && !isCancelled()) {
      mCallback.onGetLeaderboardFinish(scores);
    }
  }
}