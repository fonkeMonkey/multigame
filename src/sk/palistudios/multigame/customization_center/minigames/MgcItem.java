package sk.palistudios.multigame.customization_center.minigames;

// @author Pali

import sk.palistudios.multigame.customization_center.CustomizeItem;

public class MgcItem extends CustomizeItem {

  protected char type;

  public MgcItem(char type, String computerName, String humanName, boolean status) {
    super(computerName, humanName, status);
    this.type = type;
  }

  public MgcItem(char type, String computerName, String humanName, boolean status,
      String unlockDescription) {
    super(computerName, humanName, status, unlockDescription);
    this.type = type;
  }
}
