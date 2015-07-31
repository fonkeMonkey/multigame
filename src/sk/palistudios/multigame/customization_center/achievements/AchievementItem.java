package sk.palistudios.multigame.customization_center.achievements;

// @author Pali

import android.content.Context;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.CustomizeItem;
import sk.palistudios.multigame.game.persistence.MGSettings;
import sk.palistudios.multigame.tools.Toaster;

public class AchievementItem extends CustomizeItem {

  private String description;
  private String whatToFulfill;
  private int minimumToFulfill;
  private String correspondingItem;
  private String correspondingItemHuman;
  private String correspondingType;

  public AchievementItem(String computerName, String humanName, String description,
      String whatToFulfill, int minimumToFulfill, String correspondingItem,
      String correspondingItemHuman, boolean chosen, String correspondingType) {
    super(computerName, humanName, chosen);
    this.description = description;
    this.whatToFulfill = whatToFulfill;
    this.minimumToFulfill = minimumToFulfill;
    this.correspondingItem = correspondingItem;
    this.correspondingItemHuman = correspondingItemHuman;
    this.correspondingType = correspondingType;
  }

  public String getDescription() {
    return description;
  }

  public void checkAchievementFullfiled(int score, int level, Context context) {
    if (!MGSettings.isAchievementFulfilled(getComputerName())) {
      if (whatToFulfill.compareTo("SHARE") == 0 || whatToFulfill.compareTo("RATE") == 0) {
        return;
      }

      if (whatToFulfill.compareTo("SCORE") == 0) {
        if (score >= minimumToFulfill) {
          MGSettings.achievementFulfilled(getComputerName(), correspondingItem);
          Toaster.toastLong(context.getResources().getString(R.string.game_achievement_fulfilled_1) +
              description + context.getResources().getString(R.string.game_achievement_fulfilled_2) +
              correspondingType + context.getResources().getString(
              R.string.game_achievement_fulfilled_3), context);
        }
      }

      if (whatToFulfill.compareTo("LEVEL") == 0) {
        if (level >= minimumToFulfill) {
          MGSettings.achievementFulfilled(getComputerName(), correspondingItem);
          if (minimumToFulfill == 15) {
            Toaster.toastLong(context.getResources().getString(
                R.string.game_achievement_adfree_fulfilled), context);
          } else {
            Toaster.toastLong(context.getResources().getString(R.string.game_achievement_fulfilled_1) +
                description + context.getResources().getString(R.string.game_achievement_fulfilled_2) +
                correspondingType + context.getResources().getString(
                R.string.game_achievement_fulfilled_3), context);
          }
        }
      }

      if (whatToFulfill.compareTo("GAMES") == 0) {
        if (MGSettings.getStatsGamesPlayed() >= minimumToFulfill) {
          MGSettings.achievementFulfilled(getComputerName(), correspondingItem);
          Toaster.toastLong(context.getResources().getString(R.string.game_achievement_fulfilled_1) +
              description + context.getResources().getString(R.string.game_achievement_fulfilled_2) +
              correspondingType + context.getResources().getString(
              R.string.game_achievement_fulfilled_3), context);
        }
      }
    }
  }

  public String getCorrespondingItem() {
    return correspondingItem;
  }

  public String getCorrespondingType() {
    return correspondingType;
  }

  public String getCorrespondingItemHuman() {
    return correspondingItemHuman;
  }

  public boolean isActive() {
    return MGSettings.isAchievementFulfilled(getComputerName());
  }
}
