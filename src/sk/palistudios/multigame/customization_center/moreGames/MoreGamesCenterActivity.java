package sk.palistudios.multigame.customization_center.moreGames;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import sk.palistudios.multigame.mainMenu.DebugSettings;
import sk.palistudios.multigame.tools.sound.SoundEffectsCenter;

//import com.appflood.AppFlood;

/**
 * @author Pali
 */
public class MoreGamesCenterActivity extends ActivityGroup {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        View view = getLocalActivityManager()
                .startActivity("myActivity", new Intent(this, myActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                .getDecorView();
        this.setContentView(view);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SoundEffectsCenter.playBackSound(this);
    }

    private class myActivity extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            if (DebugSettings.adsActivated) {
//              AppFlood.showList(this, AppFlood.LIST_GAME);
            }
        }
    }
}
