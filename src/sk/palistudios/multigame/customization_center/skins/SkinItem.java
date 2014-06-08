package sk.palistudios.multigame.customization_center.skins;

// @author Pali
import sk.palistudios.multigame.customization_center.AbstractItem;
import sk.palistudios.multigame.game.persistence.GameSharedPref;

public class SkinItem extends AbstractItem {

//    protected String name;
//    protected boolean chosen;
    int color1;
    int color2;
    int color3;
    int color4;
    int colorHeader;
    int colorChosen;
    int logoID;

    public SkinItem(String computerName, String humanName, int color1, int color2, int color3, int color4, int colorHeader, int colorChosen, int logoID) {
        super(computerName, humanName, GameSharedPref.isSkinChosen(computerName));
//        this.name = computerName;
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
        this.color4 = color4;
        this.colorHeader = colorHeader;
        this.colorChosen = colorChosen;
//        chosen = GameSharedPref.isSkinChosen(computerName);
        this.logoID = logoID;
    }

    public SkinItem(String computerName, String humanName, int color1, int color2, int color3, int color4, int colorHeader, int colorChosen, int logoID, String unlockDescription) {
        super(computerName, humanName, GameSharedPref.isSkinChosen(computerName), unlockDescription);
//        this.name = computerName;
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
        this.color4 = color4;
        this.colorHeader = colorHeader;
        this.colorChosen = colorChosen;
//        chosen = GameSharedPref.isSkinChosen(computerName);
        this.logoID = logoID;
    }

    SkinItem(String computerName, String humanName) {
        super(computerName, humanName, GameSharedPref.isSkinChosen(computerName));
//        name = currentSkinName;
//        chosen = GameSharedPref.isSkinChosen(currentSkinName);
    }

    SkinItem(String computerName, String humanName, String unlockDescription) {
        super(computerName, humanName, GameSharedPref.isSkinChosen(computerName), unlockDescription);
//        name = currentSkinName;
//        chosen = GameSharedPref.isSkinChosen(currentSkinName);
    }

//       public String getName() {
//        return name;
//    }
    public int getColor1() {
        return color1;
    }

    public int getColor2() {
        return color2;
    }

    public int getColor3() {
        return color3;
    }

    public int getColor4() {
        return color4;
    }

    public int getColorHeader() {
        return colorHeader;
    }

    public int getColorChosen() {
        return colorChosen;
    }

    public int getLogoID() {
        return logoID;
    }
}
