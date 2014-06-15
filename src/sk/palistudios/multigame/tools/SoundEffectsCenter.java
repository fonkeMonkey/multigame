/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.palistudios.multigame.tools;

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
    private static final double VOLUME_COEFICIENT = 0.5;
    public static MediaPlayer mp_forward;
    public static MediaPlayer mp_back;
    public static MediaPlayer mp_tab;
    public static AudioManager audioManager;
    private static float VOLUME = 0.25f;
    private static boolean isInitialized = false;

    public static void init(Context context) {

        mp_forward = MediaPlayer.create(context, R.raw.button_forward);
        mp_forward.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp_back = MediaPlayer.create(context, R.raw.button_back);
        mp_back.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp_tab = MediaPlayer.create(context, R.raw.button_tab);
        mp_tab.setAudioStreamType(AudioManager.STREAM_MUSIC);
        isInitialized = true;
    }

    public static void muteSystemSounds(Context context, boolean status) {
        if (audioManager == null) {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, status);
    }

    public static void playForwardSound(Context context) {
        if (!isInitialized) {
            init(context);
        }
        if (GameSharedPref.isSoundOn()) {
            mp_forward.start();
        }
    }

    public static void playBackSound(Context context) {
        if (!isInitialized) {
            init(context);
        }

        if (GameSharedPref.isSoundOn()) {
            mp_back.start();
        }
    }

    public static void playTabSound(Context context) {
        if (!isInitialized) {
            init(context);
        }

        if (GameSharedPref.isSoundOn()) {
            mp_tab.start();
        }
    }

    public static void releaseMediaPlayer() {
        if (mp_forward != null) {
            mp_forward.release();
            mp_forward = null;
        }
        if (mp_back != null) {
            mp_back.release();
            mp_back = null;
        }
        if (mp_tab != null) {
            mp_tab.release();
            mp_tab = null;
        }
        isInitialized = false;
    }

    public static void setVolumeBasedOnRingVolume(Context context) {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        // Get the current ringer volume as a percentage of the max ringer volume.
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
        int maxRingerVolume = audio.getStreamMaxVolume(AudioManager.STREAM_RING);
        double proportion = currentVolume / (double) maxRingerVolume;

        // Calculate a desired music volume as that same percentage of the max music volume.
        int maxMusicVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int desiredMusicVolume = (int) (proportion * maxMusicVolume * VOLUME_COEFICIENT);

        // Set the music stream volume.
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, desiredMusicVolume, 0 /*flags*/);
    }
}