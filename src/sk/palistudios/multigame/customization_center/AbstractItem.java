package sk.palistudios.multigame.customization_center;

import sk.palistudios.multigame.game.persistence.GameSharedPref;

/**
 * @author Pali
 */
public class AbstractItem {

    protected boolean chosen;
    String computerName;
    String humanName;
    private String unlockDescription;

    public AbstractItem(String computerName, String humanName, boolean chosen) {
        this.computerName = computerName;
        this.chosen = chosen;
        this.humanName = humanName;
    }

    public AbstractItem(String computerName, String humanName, boolean chosen, String unlockDescription) {
        this.computerName = computerName;
        this.chosen = chosen;
        this.humanName = humanName;
        this.unlockDescription = unlockDescription;
    }

    public boolean isLocked() {
        return GameSharedPref.isItemLocked(computerName);
//        return locked;
    }

    void unlock() {
        GameSharedPref.unlockItem(computerName);
//        locked = false;
    }

    public String getComputerName() {
        return computerName;
    }

    public String getHumanName() {
        return humanName;
    }

    public void activate() {
        chosen = true;
    }

    public void inactivate() {
        chosen = false;
    }

    public boolean isChosen() {
        return chosen;
    }

    public String getLockedDescription() {
        return unlockDescription;
    }
}
