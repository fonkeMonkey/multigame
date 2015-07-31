package sk.palistudios.multigame.customization_center;

import sk.palistudios.multigame.game.persistence.MGSettings;

/**
 * @author Pali
 */
public class CustomizeItem {

  protected boolean active;
  String computerName;
  String humanName;
  private String unlockDescription;

  public CustomizeItem(String computerName, String humanName, boolean chosen) {
    this.computerName = computerName;
    this.active = chosen;
    this.humanName = humanName;
  }

  public CustomizeItem(String computerName, String humanName, boolean chosen,
      String unlockDescription) {
    this.computerName = computerName;
    this.active = chosen;
    this.humanName = humanName;
    this.unlockDescription = unlockDescription;
  }

  public String getComputerName() {
    return computerName;
  }

  public String getHumanName() {
    return humanName;
  }

  public void activate() {
    active = true;
  }

  public void inactivate() {
    active = false;
  }

  public boolean isActive() {
    return active;
  }

  void unlock() {
    MGSettings.unlockItem(computerName);
  }

  public boolean isLocked() {
    return MGSettings.isItemLocked(computerName);
  }

  public String getLockedDescription() {
    return unlockDescription;
  }
}
