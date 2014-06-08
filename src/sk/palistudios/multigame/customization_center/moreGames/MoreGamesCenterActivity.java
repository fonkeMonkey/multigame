package sk.palistudios.multigame.customization_center.moreGames;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
//import com.appflood.AppFlood;
import sk.palistudios.multigame.mainMenu.GlobalSettings;
import sk.palistudios.multigame.tools.SoundEffectsCenter;

/**
 *
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

    private class myActivity extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            if (GlobalSettings.adsActivated) {
//              AppFlood.showList(this, AppFlood.LIST_GAME);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SoundEffectsCenter.playBackSound(this);
    }
}
