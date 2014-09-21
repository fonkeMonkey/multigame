package sk.palistudios.multigame.game.persistence;

// @author Pali

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.graphics.Point;

public class PointSerializable implements Serializable {

  public Point mPoint;

  public PointSerializable(int x, int y) {
    mPoint = new Point(x, y);
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeInt(mPoint.x);
    out.writeInt(mPoint.y);
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    mPoint = new Point();
    mPoint.x = in.readInt();
    mPoint.y = in.readInt();
  }
}
