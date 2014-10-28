/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.palistudios.multigame;

import android.app.ListActivity;

import sk.palistudios.multigame.tools.sound.SoundEffectsCenter;

/**
 * @author virdzek
 */
public abstract class BaseListActivity extends ListActivity {

  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  protected void onResume() {
    super.onResume();
    SoundEffectsCenter.muteSystemSounds(this, true);
  }

  @Override
  protected void onStop() {
    super.onStop();
    SoundEffectsCenter.muteSystemSounds(this, false);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
//    SoundEffectsCenter.playBackSound(this);
  }
}

