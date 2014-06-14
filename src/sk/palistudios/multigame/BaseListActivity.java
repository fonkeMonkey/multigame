/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.palistudios.multigame;

import android.app.ListActivity;
import com.google.analytics.tracking.android.EasyTracker;
import sk.palistudios.multigame.tools.SoundEffectsCenter;

/**
 * @author virdzek
 */
public abstract class BaseListActivity extends ListActivity {

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
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
        EasyTracker.getInstance(this).activityStop(this);
    }
}
    
