package sk.palistudios.multigame.hall_of_fame.leaderboard;

import com.google.android.gms.games.leaderboard.LeaderboardScore;

import java.util.List;

/**
 * Callback to pass results from {@link GetLeaderboardAsyncTask} to calling activity.
 *
 * @author Patrik Mucha
 */
public interface GetLeaderboardCallback {

  /**
   * Called on getting leaderboard finish.
   *
   * @param scoresList The list with obtained scores.
   */
  void onGetLeaderboardFinish(List<LeaderboardScore> scoresList);
}