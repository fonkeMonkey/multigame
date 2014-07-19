package sk.palistudios.multigame.game.minigames;

// @author Pali
public class MinigameInfoObject {

    private String name;
    private String type;
    private boolean isActive;

    public MinigameInfoObject(String name, String type, boolean isActive) {
        this.name = name;
        this.type = type;
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
