package sk.palistudios.multigame.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.preferences.PreferenceOnOffSwitcher;

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

  private SkinManager() {
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
    switch (getCurrentSkin()) {
      case QUAD:
        return context.getResources().getDrawable(R.drawable.xml_bg_quad);
      case THRESHOLD:
        return context.getResources().getDrawable(R.drawable.xml_bg_threshold);
      case DIFFUSE:
        return context.getResources().getDrawable(R.drawable.bg_diffuse);
      case CORRUPTED:
        return context.getResources().getDrawable(R.drawable.bg_corrupted);
    }
    throw new RuntimeException("Corrupted skin name!");
  }

  private void reskinTextsInView(Context context, ViewGroup view) {
    reskinTextsInView(context, view, getCurrentTextColor(context));
  }

  private void reskinTextsInView(Context context, ViewGroup view, int color) {
    for (int i = 0; i < view.getChildCount(); i++) {
      View child = view.getChildAt(i);
      if (child instanceof PreferenceOnOffSwitcher){
        //handled internally.
        continue;
      }
      if (child instanceof ViewGroup) {
        reskinTextsInView(context, (ViewGroup) child, color);
      } else if (child != null) {
        if (child.getClass() == PreferenceOnOffSwitcher.class) {
          //handled internally.
          return;
        }
        if (child.getClass() == TextView.class) {
          ((TextView) child).setTextColor(color);
        }
        if (child.getClass() == Button.class) {
          ((Button) child).setTextColor(color);
        }
        if (child.getClass() == CheckedTextView.class) {
          ((CheckedTextView) child).setTextColor(color);
        }
      }
    }
  }

  public int getCurrentTextColor(Context context) {
    return getCurrentTextColor(context.getResources());
  }

  public int getCurrentTextColor(Resources resources) {
    switch (getCurrentSkin()) {
      case QUAD:
        return resources.getColor(R.color.quad_text_color);
      case THRESHOLD:
        return resources.getColor(R.color.threshold_text_color);
      case DIFFUSE:
        return resources.getColor(R.color.diffuse_text_color);
      case CORRUPTED:
        return resources.getColor(R.color.corrupted_text_color);
    }
    throw new RuntimeException("Corrupted skin name!");
  }

  public int getCurrentTextColorDisabled(Context context) {
    return getCurrentTextColorDisabled(context.getResources());
  }

  public int getCurrentTextColorDisabled(Resources resources) {
    switch (getCurrentSkin()) {
      case QUAD:
        return resources.getColor(R.color.quad_text_color_disabled);
      case THRESHOLD:
        return resources.getColor(R.color.threshold_text_color_disabled);
      case DIFFUSE:
        return resources.getColor(R.color.diffuse_text_color_disabled);
      case CORRUPTED:
        return resources.getColor(R.color.corrupted_text_color_disabled);
    }
    throw new RuntimeException("Corrupted skin name!");
  }

  public int getCurrentTextColorListItemActive(Context context) {
    return getCurrentTextColorDisabled(context.getResources());
  }

  public int getCurrentTextColorListItemActive(Resources resources) {
    switch (getCurrentSkin()) {
      case QUAD:
        return resources.getColor(R.color.quad_text_color_li_active);
      case THRESHOLD:
        return resources.getColor(R.color.threshold_text_color_active);
      case DIFFUSE:
        return resources.getColor(R.color.diffuse_text_color_active);
      case CORRUPTED:
        return resources.getColor(R.color.corrupted_text_color_active);
    }
    throw new RuntimeException("Corrupted skin name!");
  }

  public int getCurrentTextColorListItemInactive(Context context) {
    return getCurrentTextColorDisabled(context.getResources());
  }

  public int getCurrentTextColorListItemInactive(Resources resources) {
    switch (getCurrentSkin()) {
      case QUAD:
        return resources.getColor(R.color.quad_text_color_li_inactive);
      case THRESHOLD:
        return resources.getColor(R.color.threshold_text_color_inactive);
      case DIFFUSE:
        return resources.getColor(R.color.diffuse_text_color_inactive);
      case CORRUPTED:
        return resources.getColor(R.color.corrupted_text_color_inactive);
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
