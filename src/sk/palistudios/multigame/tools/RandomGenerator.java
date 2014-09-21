package sk.palistudios.multigame.tools;

// @author Pali

import java.util.Random;

public class RandomGenerator {

  public static Random r = new Random();
  private static RandomGenerator rg;

  public static RandomGenerator getInstance() {
    if (rg == null) {
      rg = new RandomGenerator();
    }
    return rg;
  }

  public int generateInt(int start, int end) {
    return r.nextInt(end - start + 1) + start;
  }

  public float generateFloat(float minimum, float maximum) {
    return (minimum + (r.nextFloat() * (maximum - minimum)));
  }

  //return true/false depending on probablity (0-100)
  public boolean tossACoin(int probability) {
    int random = rg.generateInt(0, 100);

    if (random < probability) {
      return true;
    }
    return false;
  }
}
