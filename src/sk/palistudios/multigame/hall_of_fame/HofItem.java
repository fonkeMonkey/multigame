package sk.palistudios.multigame.hall_of_fame;

// @author Pali
public class HofItem {
  //not inicialized yet
  private int position = -1;
  private String name;
  private int score;

  public HofItem(String name, int score) {
    this.name = name;
    this.score = score;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public String getName() {
    return name;
  }

  public int getScore() {
    return score;
  }
}
