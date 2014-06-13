package sk.palistudios.multigame.customization_center.skins;

// @author Pali
import sk.palistudios.multigame.customization_center.AbstractItem;
import sk.palistudios.multigame.game.persistence.GameSharedPref;

public class SkinItem extends AbstractItem {

    int color1;
    int color2;
    int color3;
    int color4;
    int color5;
    int colorHeader;
    int colorChosen;
    int logoID;

    public SkinItem(String computerName, String humanName, int bar_bg_color, int bar_label_color, int bar_text_color, int bar_separator_color, int bar_separator_color_down, int colorHeader, int colorChosen, int logoID) {
        super(computerName, humanName, GameSharedPref.isSkinChosen(computerName));
        this.color1 = bar_bg_color;
        this.color2 = bar_label_color;
        this.color3 = bar_text_color;
        this.color4 = bar_separator_color;
        this.color5 = bar_separator_color_down;
        this.colorHeader = colorHeader;
        this.colorChosen = colorChosen;
        this.logoID = logoID;
    }

    SkinItem(String computerName, String humanName) {
        super(computerName, humanName, GameSharedPref.isSkinChosen(computerName));
    }

    SkinItem(String computerName, String humanName, String unlockDescription) {
        super(computerName, humanName, GameSharedPref.isSkinChosen(computerName), unlockDescription);
    }

    public int getBarBgColor() {
        return color1;
    }

    public int getBarLabelColor() {
        return color2;
    }

    public int getBarTextColor() {
        return color3;
    }

    public int getBarSeparatorColor() {
        return color4;
    }
    public int getBarSeparatorColorDown() {
        return color5;
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
