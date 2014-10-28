/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.palistudios.multigame.tools.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.persistence.GameSharedPref;

/**
 * @author virdzek
 */
public class SoundEffectsCenter {

  /* Field to control the volume of the app sounds. */
  private static final float VOLUME_COEFICIENT = 0.3f;
  public static MediaPlayer mp_forward_old;
  public static MediaPlayer mp_forward;
  public static MediaPlayer mp_tab;
  public static AudioManager mAudioManager;
  private static boolean sIsInitialized = false;
  private static int mDesiredMusicVolume;

  public static void init(Context context) {
    float soundVolume = VOLUME_COEFICIENT;
    //TODO virdzek  * mDesiredMusicVolume nejak to ešte napasovať možno (alebo to robí
    // automaticky ten stream?

    mp_forward_old = MediaPlayer.create(context, R.raw.button_forward);
    mp_forward_old.setAudioStreamType(AudioManager.STREAM_MUSIC);
    mp_forward_old.setVolume(soundVolume, soundVolume);
    mp_forward = MediaPlayer.create(context, R.raw.button_back);
    mp_forward.setAudioStreamType(AudioManager.STREAM_MUSIC);
    mp_forward.setVolume(soundVolume, soundVolume);
    mp_tab = MediaPlayer.create(context, R.raw.button_tab);
    mp_tab.setAudioStreamType(AudioManager.STREAM_MUSIC);
    mp_tab.setVolume(soundVolume, soundVolume);

    sIsInitialized = true;
  }

  public static void muteSystemSounds(Context context, boolean status) {
    if (mAudioManager == null) {
      mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }
    mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, status);
  }

  public static void playForwardSound(Context context) {
    if (!sIsInitialized) {
      init(context);
    }
    if (GameSharedPref.isSoundOn()) {
      mp_forward.start();
    }
  }

  public static void playBackSound(Context context) {
    if (!sIsInitialized) {
      init(context);
    }

    if (GameSharedPref.isSoundOn()) {
      mp_forward.start();
    }
  }

  public static void playTabSound(Context context) {
    if (!sIsInitialized) {
      init(context);
    }

    if (GameSharedPref.isSoundOn()) {
      mp_tab.start();
    }
  }

  public static void releaseMediaPlayer() {
    if (mp_forward_old != null) {
      mp_forward_old.release();
      mp_forward_old = null;
    }
    if (mp_forward != null) {
      mp_forward.release();
      mp_forward = null;
    }
    if (mp_tab != null) {
      mp_tab.release();
      mp_tab = null;
    }
    sIsInitialized = false;
  }

  public static void setVolumeBasedOnRingVolume(Context context) {
    AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    // Get the current ringer volume as a percentage of the max ringer volume.
    int currentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
    int maxRingerVolume = audio.getStreamMaxVolume(AudioManager.STREAM_RING);
    double proportion = currentVolume / (double) maxRingerVolume;

    // Calculate a desired music volume as that same percentage of the max music volume.
    int maxMusicVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    mDesiredMusicVolume = (int) (proportion * maxMusicVolume);

    // Set the music stream volume.
    audio.setStreamVolume(AudioManager.STREAM_MUSIC, mDesiredMusicVolume, 0 /*flags*/);
  }

  public static int getCurrentVolume(Context context) {
    AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    return audio.getStreamVolume(AudioManager.STREAM_MUSIC);
  }

  public static void raiseCurrentVolume(Context context) {
    AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
  }
}