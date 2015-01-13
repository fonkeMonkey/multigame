package sk.palistudios.multigame.customization_center.music;

// @author Pali

import sk.palistudios.multigame.customization_center.CustomizeItem;

public class MusicItem extends CustomizeItem {

  public MusicItem(String computerName, String humanName, boolean chosen) {
    super(computerName, humanName, chosen);
  }

  public MusicItem(String computerName, String humanName, boolean chosen,
      String lockedDescription) {
    super(computerName, humanName, chosen, lockedDescription);
  }
}
