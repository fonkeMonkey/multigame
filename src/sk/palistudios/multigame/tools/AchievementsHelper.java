package sk.palistudios.multigame.tools;

import java.util.ArrayList;

import android.content.Context;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.achievements.AchievementItem;
import sk.palistudios.multigame.game.persistence.GameSharedPref;

/**
 * Created by virdzek on 13/01/15.
 */
public class AchievementsHelper {
  private static ArrayList<AchievementItem> sAchievements = new ArrayList<AchievementItem>();

  public static ArrayList<AchievementItem> getAchievements(Context context) {
    initAchievementsIfNeeded(context);
    return sAchievements;
  }

  private static void initAchievementsIfNeeded(Context context) {
    if (sAchievements.isEmpty()) {
      sAchievements.add(new AchievementItem("good_start", (String) context.getResources().getString(
          R.string.cc_achievements_good_start_name), (String) context.getResources().getString(
          R.string.cc_achievements_good_start_description), "SCORE", 5000, "VBouncer",
          (String) context.getResources().getString(R.string.cc_minigames_bouncer_name),
          GameSharedPref.isAchievementFulfilled("good_start"),
          (String) context.getResources().getString(R.string.cc_minigames_minigame_name)));

      sAchievements.add(new AchievementItem("lucky_seven",
          (String) context.getResources().getString(R.string.cc_achievements_lucky_seven_name),
          (String) context.getResources().getString(
              R.string.cc_achievements_lucky_seven_description), "LEVEL", 7, "dst_cyberops",
          (String) context.getResources().getString(R.string.cc_music_blam_name),
          GameSharedPref.isAchievementFulfilled("lucky_seven"),
          (String) context.getResources().getString(R.string.cc_music_music_name)));

      sAchievements.add(new AchievementItem("magic_ten", (String) context.getResources().getString(
          R.string.cc_achievements_magin_ten_name), (String) context.getResources().getString(
          R.string.cc_achievements_magin_ten_description), "LEVEL", 10, "girl_power",
          (String) context.getResources().getString(R.string.cc_skins_girl_power_name),
          GameSharedPref.isAchievementFulfilled("magic_ten"),
          (String) context.getResources().getString(R.string.cc_skins_skin_name)));

      sAchievements.add(new AchievementItem("addict", (String) context.getResources().getString(
          R.string.cc_achievements_addict_name), (String) context.getResources().getString(
          R.string.cc_achievements_addicts_description), "GAMES", 10, "TInvader",
          (String) context.getResources().getString(R.string.cc_minigames_invader_name),
          GameSharedPref.isAchievementFulfilled("addict"),
          (String) context.getResources().getString(R.string.cc_minigames_minigame_name)));

      sAchievements.add(new AchievementItem("supporter", (String) context.getResources().getString(
          R.string.cc_achievements_supporter_name), (String) context.getResources().getString(
          R.string.cc_achievements_supporter_description), "RATE", -1, "dst_cv_x",
          (String) context.getResources().getString(R.string.cc_music_cv_x_name),
          GameSharedPref.isAchievementFulfilled("supporter"),
          (String) context.getResources().getString(R.string.cc_music_music_name)));

      sAchievements.add(new AchievementItem("competitive",
          (String) context.getResources().getString(R.string.cc_achievements_competitive_name),
          (String) context.getResources().getString(
              R.string.cc_achievements_competitive_description), "SHARE", -1, "blue_sky",
          (String) context.getResources().getString(R.string.cc_skins_blue_sky_name),
          GameSharedPref.isAchievementFulfilled("competitive"),
          (String) context.getResources().getString(R.string.cc_skins_skin_name)));

      sAchievements.add(new AchievementItem("champion", (String) context.getResources().getString(
          R.string.cc_achievements_champion_name), (String) context.getResources().getString(
          R.string.cc_achievements_champion_description), "SCORE", 1001, "summer",
          (String) context.getResources().getString(R.string.cc_skins_summer_name),
          GameSharedPref.isAchievementFulfilled("champion"),
          (String) context.getResources().getString(R.string.cc_skins_skin_name)));
    }
  }

  public static void checkAchievements(int score, int level, Context context) {
    initAchievementsIfNeeded(context);

    for (AchievementItem ach : sAchievements) {
      ach.checkAchievementFullfiled(score, level, context);
    }

  }
}
