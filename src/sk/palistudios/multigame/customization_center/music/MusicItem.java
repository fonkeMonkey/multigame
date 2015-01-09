package sk.palistudios.multigame.customization_center.music;

// @author Pali

import sk.palistudios.multigame.customization_center.CustomizeItem;

public class MusicItem extends CustomizeItem {

  //    protected String computerName;
  //    protected String humanName;
  //    protected boolean active;
  public MusicItem(String computerName, String humanName, boolean chosen) {
    super(computerName, humanName, chosen);
    //        this.computerName = PCname;
    //        this.humanName = humanName;
    //        this.active = active;
  }

  public MusicItem(String computerName, String humanName, boolean chosen,
      String lockedDescription) {
    super(computerName, humanName, chosen, lockedDescription);
    //        this.humanName = humanName;
  }
}
