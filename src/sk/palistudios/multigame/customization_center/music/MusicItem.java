package sk.palistudios.multigame.customization_center.music;

// @author Pali

import sk.palistudios.multigame.customization_center.AbstractItem;

public class MusicItem extends AbstractItem {

    //    protected String computerName;
//    protected String humanName;
//    protected boolean chosen;
    public MusicItem(String computerName, String humanName, boolean chosen) {
        super(computerName, humanName, chosen);
//        this.computerName = PCname;
//        this.humanName = humanName;
//        this.chosen = chosen;
    }

    public MusicItem(String computerName, String humanName, boolean chosen, String lockedDescription) {
        super(computerName, humanName, chosen, lockedDescription);
//        this.humanName = humanName;
    }
}
