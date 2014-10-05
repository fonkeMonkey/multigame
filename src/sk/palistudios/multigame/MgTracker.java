package sk.palistudios.multigame;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Pali on 5. 10. 2014.
 */
public class MgTracker {
  private static final String CATEGORY_GAME_TUTORIAL = "game_tutorial";
  private static final String ACTION_TUTORIAL_LEVEL_STARTED = "category_tutorial_level_started";
  private static final String ACTION_TUTORIAL_WINDOW_SHOWN = "category_tutorial_window_shown";
  private static Tracker sTracker = null;

  public static synchronized Tracker getTracker(Context context) {
    if (sTracker == null) {
      GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
      sTracker = analytics.newTracker(R.xml.global_tracker);
    }
    return sTracker;
  }

  public static void trackTutorialLevelStarted(int levelNo){
    sTracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_GAME_TUTORIAL).setAction(
        ACTION_TUTORIAL_LEVEL_STARTED).setLabel(String.valueOf(levelNo)).build());
  }

  public static void trackTutorialWindowShown(int levelNo) {
    sTracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_GAME_TUTORIAL).setAction(
        ACTION_TUTORIAL_LEVEL_STARTED).setLabel(String.valueOf(levelNo)).build());
  }
}
