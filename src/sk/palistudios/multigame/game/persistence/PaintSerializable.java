package sk.palistudios.multigame.game.persistence;

// @author Pali
import android.graphics.Paint;
import android.graphics.Paint.Style;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class PaintSerializable implements Serializable {

    public Paint mPaint;
    int mColor;
    Style mStyle;
    String mStyleName;

    public PaintSerializable(int color, Style style) {
        mColor = color;
        mStyle = style;
        mStyleName = style.name();

        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setStyle(mStyle);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

    }

    public PaintSerializable(int color) {
        mColor = color;
        mStyle = Style.FILL_AND_STROKE;
        mStyleName = mStyle.name();

        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setStyle(mStyle);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

    }

    private void writeObject(ObjectOutputStream out) throws IOException {

        out.writeInt(mColor);
        out.writeObject(mStyleName);
        out.writeFloat(mPaint.getTextSize());
        out.writeFloat(mPaint.getStrokeWidth());

    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        mColor = in.readInt();
        mStyleName = (String) in.readObject();
        mStyle = Style.valueOf(mStyleName);

        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setStyle(mStyle);

        mPaint.setTextSize(in.readFloat());
        mPaint.setStrokeWidth(in.readFloat());
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    public float getStrokeWidth() {
        return mPaint.getStrokeWidth();
    }

    public void setStrokeWidth(int strokeWidth) {
        mPaint.setStrokeWidth(strokeWidth);
    }

    public float getTextSize() {
        return mPaint.getTextSize();
    }

    public void setTextSize(int textSize) {
        mPaint.setTextSize(textSize);
    }
}
