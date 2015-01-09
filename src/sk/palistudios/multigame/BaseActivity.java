/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.palistudios.multigame;

import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;

import sk.palistudios.multigame.tools.SkinManager;
import sk.palistudios.multigame.tools.sound.SoundEffectsCenter;

/**
 * @author virdzek
 */
public abstract class BaseActivity extends FragmentActivity {
  //TODO VL mutey nie sú akoby 'oproti' mal by byť v onStart nie?

  /*
   * RESKIN sem pretože musí sa reskinovať aj po backu z customize MainMenu.
   */
  @Override
  protected void onStart() {
    super.onStart();
    SkinManager.Skin currentSkin = SkinManager.reskin(this, (ViewGroup) ((ViewGroup) (findViewById(
        android.R.id.content))).getChildAt(0));
    reskinLocally(currentSkin);
  }

  public abstract void reskinLocally(SkinManager.Skin currentSkin);

  @Override
  protected void onResume() {
    super.onResume();
    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    SoundEffectsCenter.muteSystemSounds(this, true);
  }

  @Override
  protected void onPause() {
    super.onPause();
    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
  }

  @Override
  protected void onStop() {
    super.onStop();
    SoundEffectsCenter.muteSystemSounds(this, false);
  }
}
