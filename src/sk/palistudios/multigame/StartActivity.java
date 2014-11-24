/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.palistudios.multigame;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;

import sk.palistudios.multigame.tools.sound.SoundEffectsCenter;

/**
 * @author virdzek
 */
public class StartActivity extends Activity {
  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    /* Nastav zvuk a pusti to */
    setVolumeControlStream(AudioManager.STREAM_MUSIC);
    SoundEffectsCenter.setVolumeBasedOnRingVolume(this);

    Intent intent = new Intent(this, sk.palistudios.multigame.mainMenu.MainMenuActivity.class);
    startActivity(intent);
  }
}
