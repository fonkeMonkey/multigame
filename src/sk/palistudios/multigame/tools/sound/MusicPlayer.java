package sk.palistudios.multigame.tools.sound;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class MusicPlayer {

  private MediaPlayer mMediaPlayer;
  String TAG = "MEdiaPlayer";

  public MusicPlayer(int musicRID, Context context) {
    mMediaPlayer = MediaPlayer.create(context, musicRID);
    mMediaPlayer.setVolume(0.25f, 0.25f);
    mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
      public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.w(TAG, what + ": " + extra);
        return true;
      }
    });
    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      public void onCompletion(MediaPlayer mp) {
        mp.reset(); // LOOP

      }
    });
  }

  public void startMusic() {
    if (mMediaPlayer == null) {
      return;
    }

    if (!mMediaPlayer.isPlaying()) {
      mMediaPlayer.start();
    }
  }

  public void pauseMusic() {
    if (mMediaPlayer == null) {
      return;
    }

    if (mMediaPlayer.isPlaying()) {
      mMediaPlayer.pause();
    }
  }

  public void resumeMusic() {
    if (mMediaPlayer == null) {
      return;
    }

    if (!mMediaPlayer.isPlaying()) {
      mMediaPlayer.start();
    }
  }

  public void stopMusic() {
    if (mMediaPlayer == null) {
      return;
    }

    if (mMediaPlayer.isPlaying()) {
      mMediaPlayer.stop();
    }
    mMediaPlayer.reset();
    mMediaPlayer.release();
    mMediaPlayer = null;
  }
}
