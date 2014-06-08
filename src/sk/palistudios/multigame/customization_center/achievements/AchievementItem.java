package sk.palistudios.multigame.customization_center.achievements;

// @author Pali
import android.app.Activity;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.AbstractItem;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.tools.Toaster;

public class AchievementItem extends AbstractItem {

    private String description;
    private String whatToFulfill;
    private int minimumToFulfill;
    private String correspondingItem;
    private String correspondingItemHuman;
    private String correspondingType;

    public AchievementItem(String computerName, String humanName, String description, String whatToFulfill, int minimumToFulfill, String correspondingItem, String correspondingItemHuman, boolean chosen, String correspondingType) {
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

    public void checkAchievementFullfiled(int score, int level, Activity act) {
        if (!GameSharedPref.isAchievementFulfilled(getComputerName())) {
            if (whatToFulfill.compareTo("SHARE") == 0 || whatToFulfill.compareTo("RATE") == 0) {
                return;
            }

            if (whatToFulfill.compareTo("SCORE") == 0) {
                if (score >= minimumToFulfill) {
                    GameSharedPref.achievementFulfilled(getComputerName(), correspondingItem);
                    Toaster.toastLong(act.getResources().getString(R.string.game_achievement_fulfilled_1) + description + act.getResources().getString(R.string.game_achievement_fulfilled_2) + correspondingType + act.getResources().getString(R.string.game_achievement_fulfilled_3), act);
                }
            }

            if (whatToFulfill.compareTo("LEVEL") == 0) {
                if (level >= minimumToFulfill) {
                    GameSharedPref.achievementFulfilled(getComputerName(), correspondingItem);
                    if (minimumToFulfill == 15) {
                        Toaster.toastLong(act.getResources().getString(R.string.game_achievement_adfree_fulfilled), act);
                    } else {
                        Toaster.toastLong(act.getResources().getString(R.string.game_achievement_fulfilled_1) + description + act.getResources().getString(R.string.game_achievement_fulfilled_2) + correspondingType + act.getResources().getString(R.string.game_achievement_fulfilled_3), act);
                    }
                }
            }

            if (whatToFulfill.compareTo("GAMES") == 0) {
                if (GameSharedPref.getStatsGamesPlayed() >= minimumToFulfill) {
                    GameSharedPref.achievementFulfilled(getComputerName(), correspondingItem);
                    Toaster.toastLong(act.getResources().getString(R.string.game_achievement_fulfilled_1) + description + act.getResources().getString(R.string.game_achievement_fulfilled_2) + correspondingType + act.getResources().getString(R.string.game_achievement_fulfilled_3), act);
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

    public boolean isChosen() {
        return GameSharedPref.isAchievementFulfilled(getComputerName());
    }
}
