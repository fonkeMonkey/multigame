package sk.palistudios.multigame.tools;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.persistence.GameSharedPref;

/**
 * Created by virdzek on 23/11/14.
 */
public class SkinManager {
  public enum Skin {
    QUAD, THRESHOLD, DIFFUSE, CORRUPTED
  }

  //TODO njn dependency by bol lepší
  private static SkinManager sSkinManager;
  private int mTmpTextColor;
  private int TEXT_COLOR_NOT_RESOLVED;

  public static synchronized SkinManager getInstance() {
    if (sSkinManager == null) {
      sSkinManager = new SkinManager();
    }
    return sSkinManager;
  }

  private SkinManager(){
  }

  public static Skin reskin(Context context, ViewGroup containerView) {
    getInstance().reskinBackground(context, containerView);
    getInstance().reskinTextsInView(context, containerView);
    return getInstance().getCurrentSkin();
  }

  private void reskinBackground(Context context, View containerView) {
    if (Build.VERSION.SDK_INT >= 16) {
      containerView.setBackground(getCurrentBackground(context));
    } else {
      containerView.setBackgroundDrawable(getCurrentBackground(context));
    }
  }

  private Drawable getCurrentBackground(Context context) {
        switch (getCurrentSkin()){
          case QUAD: return context.getResources().getDrawable(R.drawable.xml_bg_quad);
          case THRESHOLD: return context.getResources().getDrawable(R.drawable.xml_bg_threshold);
          case DIFFUSE: return context.getResources().getDrawable(R.drawable.bg_diffuse);
          case CORRUPTED: return context.getResources().getDrawable(R.drawable.bg_corrupted);
        }
    throw new RuntimeException("Corrupted skin name!");
  }

  private void reskinTextsInView(Context context, ViewGroup view) {
    reskinTextsInView(context, view, getCurrentTextColor(context));
  }

  private void reskinTextsInView(Context context, ViewGroup view, int color) {
    for (int i = 0; i < view.getChildCount(); i++) {
      View child = view.getChildAt(i);
      if (child instanceof ViewGroup) {
        reskinTextsInView(context, (ViewGroup) child, color);
      } else if (child != null) {
        if (child.getClass() == TextView.class) {
          ((TextView) child).setTextColor(color);
        }
        if (child.getClass() == Button.class) {
          ((Button) child).setTextColor(color);
        }
      }
    }
  }

  private int getCurrentTextColor(Context context) {
    switch (getCurrentSkin()) {
      case QUAD:
        return context.getResources().getColor(R.color.quad_text_color);
      case THRESHOLD:
        return context.getResources().getColor(R.color.threshold_text_color);
      case DIFFUSE:
        return context.getResources().getColor(R.color.diffuse_text_color);
      case CORRUPTED:
        return context.getResources().getColor(R.color.corrupted_text_color);
    }
    throw new RuntimeException("Corrupted skin name!");
  }

  private Skin getCurrentSkin() {
    //TODO jj, staré názvy
    String skinName = GameSharedPref.getChosenSkin();

    if (skinName.equals("kuba")) {
      return Skin.QUAD;
    }
    if (skinName.equals("summer")) {
      return Skin.THRESHOLD;
    }
    if (skinName.equals("girl_power")) {
      return Skin.DIFFUSE;
    }
    if (skinName.equals("blue_sky")) {
      return Skin.CORRUPTED;
    }
    throw new RuntimeException("Corrupted skin name!");
  }
}
