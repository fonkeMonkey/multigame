package sk.palistudios.multigame.hall_of_fame;

// @author Pali
public class HofItem {

  //not inicialized yet
  protected int rank = -1;
  protected String name;
  protected int score;

  public HofItem(String name, int score) {
    this.name = name;
    this.score = score;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }
}
