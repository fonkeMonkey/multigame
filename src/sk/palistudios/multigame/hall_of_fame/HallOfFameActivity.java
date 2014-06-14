package sk.palistudios.multigame.hall_of_fame;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import sk.palistudios.multigame.BaseListActivity;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.skins.SkinsCenterListActivity;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.tools.SoundEffectsCenter;

import java.util.ArrayList;

/**
 * @author Pali
 */
public class HallOfFameActivity extends BaseListActivity {

    public static ProgressDialog mRingProgressDialog = null;
    HofDatabaseCenter hofDb = null;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        if (!GameSharedPref.getDbInitialized()) {
            mRingProgressDialog = ProgressDialog.show(HallOfFameActivity.this, "Please wait..", "Initializing database..", true);
            mRingProgressDialog.setCancelable(true);
        }

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

//        if(!GameSharedPref.isAchievementFulfilled("pro") && DebugSettings.adsActivated){
        setContentView(R.layout.list_layout);
//        }else{
//            setContentView(R.layout.list_layout_adfree);
//        }

        hofDb = new HofDatabaseCenter(this);
        hofDb.open();

        ArrayList<HofItem> dbRows = hofDb.fetchAllRows();
        hofDb.close();


        HofItem[] rows = new HofItem[dbRows.size()];

        for (int i = 0; i < dbRows.size(); i++) {
            rows[i] = dbRows.get(i);
            rows[i].setRank(i + 1);
        }

        ArrayAdapter adapter = new HofArrayAdapter(this, rows);
        TextView header = new TextView(this);
        header.setText(getString(R.string.top_10_players));
        header.setTextSize(35);
        header.setBackgroundColor(SkinsCenterListActivity.getCurrentSkin(this).getColorHeader());
        header.setGravity(Gravity.CENTER);
        getListView().addHeaderView(header);

        TextView footer = new TextView(this);
        footer.setTextSize(60);
        footer.setText(" ");
        getListView().addFooterView(footer, null, false);

        setListAdapter(adapter);


    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SoundEffectsCenter.playBackSound(this);
    }

    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
