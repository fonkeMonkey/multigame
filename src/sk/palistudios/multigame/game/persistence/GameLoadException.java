package sk.palistudios.multigame.game.persistence;

/**
 * @author virdzek
 */
public class GameLoadException extends Throwable {
  private final String mMessage;

  public GameLoadException(Exception e) {
    mMessage = e.getLocalizedMessage();
  }
}
