package sk.palistudios.multigame.game.persistence;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Path;
import android.graphics.RectF;

/**
 * @author Patrik Mucha (pat.mucha@gmail.com)
 */
public class PathSerializable extends Path implements Serializable {

  private List<PathAction> actions = new ArrayList<PathSerializable.PathAction>();

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
    in.defaultReadObject();
    drawThisPath();
  }

  private void drawThisPath(){
    ActionArc arc;
    for(PathAction pathAction : actions){
      if(pathAction.getType().equals(PathAction.PathActionType.MOVE_TO)){
        super.moveTo(pathAction.getX(), pathAction.getY());
      } else if(pathAction.getType().equals(PathAction.PathActionType.LINE_TO)){
        super.lineTo(pathAction.getX(), pathAction.getY());
      } else if(pathAction.getType().equals(PathAction.PathActionType.ARC_TO)) {
        arc = (ActionArc) pathAction;
        super.arcTo(arc.getOval(), arc.getStartAngle(), arc.getSweepAngle());
      }
    }
  }

  @Override
  public void moveTo(float x, float y) {
    actions.add(new ActionMove(x, y));
    super.moveTo(x, y);
  }

  @Override
  public void lineTo(float x, float y) {
    actions.add(new ActionLine(x, y));
    super.lineTo(x, y);
  }

  @Override
  public void arcTo(RectF oval, float startAngle, float sweepAngle) {
    super.arcTo(oval, startAngle, sweepAngle);
  }

  public interface PathAction {
    public enum PathActionType {LINE_TO,MOVE_TO,ARC_TO};
    public PathActionType getType();
    public float getX();
    public float getY();
  }

  public class ActionMove implements PathAction, Serializable{

    private final float mPointX;
    private final float mPointY;

    public ActionMove(float x, float y){
      mPointX = x;
      mPointY = y;
    }

    @Override
    public PathActionType getType() {
      return PathActionType.MOVE_TO;
    }

    @Override
    public float getX() {
      return mPointX;
    }

    @Override
    public float getY() {
      return mPointY;
    }
  }

  public class ActionLine implements PathAction, Serializable{

    private final float mPointX;
    private final float mPointY;

    public ActionLine(float x, float y){
      mPointX = x;
      mPointY = y;
    }

    @Override
    public PathActionType getType() {
      return PathActionType.LINE_TO;
    }

    @Override
    public float getX() {
      return mPointX;
    }

    @Override
    public float getY() {
      return mPointY;
    }
  }

  public class ActionArc implements PathAction, Serializable{

    private final RectF mOval;
    private final float mStartAngle;
    private final float mSweepAngle;

    public ActionArc(RectF oval, float startAngle, float sweepAngle){
      mOval = oval;
      mStartAngle = startAngle;
      mSweepAngle = sweepAngle;
    }

    @Override
    public PathActionType getType() {
      return PathActionType.ARC_TO;
    }

    @Override
    public float getX() {
      return mOval.left;
    }

    @Override
    public float getY() {
      return mOval.top;
    }

    public RectF getOval() {
      return mOval;
    }

    public float getStartAngle() {
      return mStartAngle;
    }

    public float getSweepAngle() {
      return mSweepAngle;
    }
  }

}
