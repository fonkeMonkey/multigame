package sk.palistudios.multigame.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.persistence.MGSettings;
import sk.palistudios.multigame.preferences.PreferenceOnOffSwitcher;

/**
 * Created by virdzek on 23/11/14.
 */

//TODO M toto celé v témach by bolo stokrát lepšie
public class SkinManager {
  public enum Skin {
    QUAD, THRESHOLD, DIFFUSE, CORRUPTED
  }

  //TODO njn dependency by bol lepší
  private static SkinManager sSkinManager;
  private int mTmpTextColor;
  private int TEXT_COLOR_NOT_RESOLVED;
  public Bitmap mCorruptedBitmap;
  public Bitmap mDiffuseBitmap;

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
        recycleCorrupted();
        recycleDiffuse();
        return context.getResources().getDrawable(R.drawable.xml_bg_quad);
      case THRESHOLD:
        recycleCorrupted();
        recycleDiffuse();
        return context.getResources().getDrawable(R.drawable.xml_bg_threshold);
      case DIFFUSE:
        recycleCorrupted();
        return getDiffuseBmp(context);
      case CORRUPTED:
        recycleDiffuse();
        return getCorruptedBmp(context);
    }
    throw new RuntimeException("Corrupted skin name!");
  }

  private void recycleDiffuse() {
    if (mDiffuseBitmap != null) {
      mDiffuseBitmap.recycle();
      mDiffuseBitmap = null;
    }
  }

  private void recycleCorrupted() {
    if (mCorruptedBitmap != null) {
      mCorruptedBitmap.recycle();
      mCorruptedBitmap = null;
    }
  }

  private Drawable getCorruptedBmp(Context context) {
    if (mCorruptedBitmap == null) {
      mCorruptedBitmap = BitmapHelper.decodeSampledBitmapFromResource(context,
          R.drawable.bg_corrupted, DisplayHelper.getScreenWidth(context),
          DisplayHelper.getScreenHeight(context));
    }
    return new BitmapDrawable(context.getResources(), mCorruptedBitmap);
  }

  private Drawable getDiffuseBmp(Context context) {
    if (mDiffuseBitmap == null) {
      mDiffuseBitmap = BitmapHelper.decodeSampledBitmapFromResource(context, R.drawable.bg_diffuse,
          DisplayHelper.getScreenWidth(context), DisplayHelper.getScreenHeight(context));
    }
    return new BitmapDrawable(context.getResources(), mDiffuseBitmap);
  }

  private void reskinTextsInView(Context context, ViewGroup view) {
    reskinTextsInView(context, view, getCurrentTextColor(context));
  }

  private void reskinTextsInView(Context context, ViewGroup view, int color) {
    for (int i = 0; i < view.getChildCount(); i++) {
      View child = view.getChildAt(i);
      if (child instanceof PreferenceOnOffSwitcher) {
        //handled internally.
        continue;
      }
      if (child instanceof ListView) {
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
          if (child.getId() == R.id.game_score || child.getId() == R.id.game_level) {
            return;
          }
          if (child.getId() == R.id.header) {
            //            ((TextView) child).setTextColor(((TextView) child).getTextColors()
            // .withAlpha(
            //                DisplayHelper.ALPHA_20pc))
            ((TextView) child).setTextColor(getCurrentTextHeaderColor(context.getResources()));
          } else {
            ((TextView) child).setTextColor(getCurrentTextColor(context.getResources()));
          }
        }
        if (child.getClass() == Button.class) {
          ((Button) child).setTextColor(color);
        }
        if (child.getClass() == CheckedTextView.class) {
          if (child.getId() == R.id.customize_minigames ||
              child.getId() == R.id.customize_achievements ||
              child.getId() == R.id.customize_music ||
              child.getId() == R.id.customize_skins) {
            //            //internal
            return;
          }
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

  public int getCurrentTextHeaderColor(Resources resources) {
    switch (getCurrentSkin()) {
      case QUAD:
        return resources.getColor(R.color.quad_text_color_header);
      case THRESHOLD:
        return resources.getColor(R.color.threshold_text_color_header);
      case DIFFUSE:
        return resources.getColor(R.color.diffuse_text_color_header);
      case CORRUPTED:
        return resources.getColor(R.color.corrupted_text_color_header);
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

  public int getCurrentListViewColorActive(Resources resources) {
    switch (getCurrentSkin()) {
      case QUAD:
        return resources.getColor(R.color.quad_text_color_listitem_active);
      case THRESHOLD:
        return resources.getColor(R.color.threshold_text_color_listitem_active);
      case DIFFUSE:
        return resources.getColor(R.color.diffuse_text_color_listitem_active);
      case CORRUPTED:
        return resources.getColor(R.color.corrupted_text_color_listitem_active);
    }
    throw new RuntimeException("Corrupted skin name!");
  }

  public int getCurrentListViewColorInactive(Resources resources) {
    switch (getCurrentSkin()) {
      case QUAD:
        return resources.getColor(R.color.quad_text_color_listitem_inactive);
      case THRESHOLD:
        return resources.getColor(R.color.threshold_text_color_listitem_inactive);
      case DIFFUSE:
        return resources.getColor(R.color.diffuse_text_color_listitem_inactive);
      case CORRUPTED:
        return resources.getColor(R.color.corrupted_text_color_listitem_inactive);
    }
    throw new RuntimeException("Corrupted skin name!");
  }

  public int getCurrentListViewColorLocked(Resources resources) {
    switch (getCurrentSkin()) {
      case QUAD:
        return resources.getColor(R.color.quad_text_color_listitem_locked);
      case THRESHOLD:
        return resources.getColor(R.color.threshold_text_color_listitem_locked);
      case DIFFUSE:
        return resources.getColor(R.color.diffuse_text_color_listitem_locked);
      case CORRUPTED:
        return resources.getColor(R.color.corrupted_text_color_listitem_locked);
    }
    throw new RuntimeException("Corrupted skin name!");
  }

  public Skin getCurrentSkin() {
    //TODO jj, staré názvy
    String skinName = MGSettings.getChosenSkin();

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

  public Drawable getTabDrawable(Context context) {
    switch (getCurrentSkin()) {
      case QUAD:
        return context.getResources().getDrawable(R.drawable.xml_bg_quad_tab);
      case THRESHOLD:
        return context.getResources().getDrawable(R.drawable.xml_bg_thres_tab);
      case DIFFUSE:
        return context.getResources().getDrawable(R.drawable.xml_bg_diff_tab);
      case CORRUPTED:
        return context.getResources().getDrawable(R.drawable.xml_bg_corr_tab);
    }
    throw new RuntimeException("Corrupted skin name!");
  }

  public int getTabActiveTextColor(Context context) {
    switch (getCurrentSkin()) {
      case QUAD:
        return context.getResources().getColor(R.color.quad_tab_active_text);
      case THRESHOLD:
        return context.getResources().getColor(R.color.thres_tab_active_text);
      case DIFFUSE:
        return context.getResources().getColor(R.color.diff_tab_active_text);
      case CORRUPTED:
        return context.getResources().getColor(R.color.corr_tab_active_text);
    }
    throw new RuntimeException("Corrupted skin name!");
  }

  public int getTabInActiveTextColor(Context context) {
    switch (getCurrentSkin()) {
      case QUAD:
        return context.getResources().getColor(R.color.quad_tab_active_text);
      case THRESHOLD:
        return context.getResources().getColor(R.color.thres_tab_inactive_text);
      case DIFFUSE:
        return context.getResources().getColor(R.color.diff_tab_active_text);
      case CORRUPTED:
        return context.getResources().getColor(R.color.corr_tab_inactive_text);
    }
    throw new RuntimeException("Corrupted skin name!");
  }
}
