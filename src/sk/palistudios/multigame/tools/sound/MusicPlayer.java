package sk.palistudios.multigame.tools.sound;

// @author Pali

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class MusicPlayer {
  //    String musicName;

  MediaPlayer mp = null;
  String TAG = "MEdiaPlayer";

  public MusicPlayer(int musicRID, Context context) {
    mp = MediaPlayer.create(context, musicRID);
    mp.setVolume(0.25f, 0.25f);
    mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
      public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.w(TAG, what + ": " + extra);
        return true;
      }
    });
    //        mp.setLooping(true);
    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      public void onCompletion(MediaPlayer mp) {
        mp.reset(); // LOOP

      }
    });
    //        });
  }

  public void startMusic() {
    if (mp == null) {
      return;
    }
    //        if (ready) {
    if (!mp.isPlaying()) {
      mp.start();
      //        }
    }
  }

  public void pauseMusic() {
    if (mp == null) {
      return;
    }
    if (mp.isPlaying()) {
      mp.pause();
    }
  }

  public void resumeMusic() {
    if (mp == null) {
      return;
    }

    if (!mp.isPlaying()) {
      mp.start();
    }
  }

  public void stopMusic() {
    if (mp == null) {
      return;
    }
    if (mp.isPlaying()) {
      mp.stop();
    }
    mp.release();
    mp = null;
  }
}
