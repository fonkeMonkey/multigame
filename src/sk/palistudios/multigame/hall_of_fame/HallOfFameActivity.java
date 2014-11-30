package sk.palistudios.multigame.hall_of_fame;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import sk.palistudios.multigame.BaseActivity;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.tools.DisplayHelper;
import sk.palistudios.multigame.tools.SkinManager;

/**
 * @author Pali
 */
public class HallOfFameActivity extends BaseActivity {
  private ListView mListView;
  private TextView mHeader;

  public static ProgressDialog mRingProgressDialog = null;
  private ToggleButton mSwitchLocal;
  private ToggleButton mSwitchOnline;
  private LinearLayout mSwitchLayout;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    if (!GameSharedPref.getDbInitialized()) {
      mRingProgressDialog = ProgressDialog.show(HallOfFameActivity.this, "Please wait..",
          "Initializing database..", true);
      mRingProgressDialog.setCancelable(true);
    }

    setContentView(R.layout.hof_layout);
    initViews();
    fillData();
  }

  private void initViews() {
    mHeader = (TextView) findViewById(R.id.hof_header);
    mListView = (ListView) findViewById(R.id.hof_list);

    mSwitchLayout = (LinearLayout) findViewById(R.id.hof_toggle_layout);
    mSwitchLocal = (ToggleButton) findViewById(R.id.hof_toggle_local);
    mSwitchOnline = (ToggleButton) findViewById(R.id.hof_toggle_online);

    mSwitchLocal.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mSwitchOnline.toggle();
      }
    });
    mSwitchOnline.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mSwitchLocal.toggle();
      }
    });

    mSwitchLocal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        buttonView.setClickable(!isChecked);
        mListView.setVisibility(View.INVISIBLE);
      }
    });

    mSwitchOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        buttonView.setClickable(!isChecked);
        mListView.setVisibility(View.VISIBLE);
      }
    });

    mSwitchLocal.setClickable(!mSwitchLocal.isChecked());
    mSwitchOnline.setClickable(!mSwitchOnline.isChecked());
  }

  private void fillData() {
    //cucni databazu
    HofDatabaseCenter mHofDb = new HofDatabaseCenter(this);
    mHofDb.open();
    ArrayList<HofItem> dbRows = mHofDb.fetchAllRows();
    mHofDb.close();

    //urob svoje itemy
    HofItem[] rows = new HofItem[dbRows.size()];
    for (int i = 0; i < dbRows.size(); i++) {
      rows[i] = dbRows.get(i);
      rows[i].setPosition(i + 1);
    }

    //adaptery sracky etc
    ArrayAdapter adapter = new HofArrayAdapter(this, rows);
    mListView.setAdapter(adapter);
  }

  @Override
  protected void reskinLocally(SkinManager.Skin currentSkin) {
    mHeader.setTextColor(mHeader.getTextColors().withAlpha(DisplayHelper.ALPHA_80pc));

    switch (currentSkin) {
      case QUAD:
        mSwitchLayout.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_layout_quad));
        mSwitchLocal.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_quad));
        mSwitchLocal.setTextColor(getResources().getColorStateList(
            R.color.xml_text_hof_switch_quad));
        mSwitchOnline.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_quad));
        mSwitchOnline.setTextColor(getResources().getColorStateList(
            R.color.xml_text_hof_switch_quad));
        break;
      case THRESHOLD:
        mSwitchLayout.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_layout_thres));
        mSwitchLocal.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_thres));
        mSwitchLocal.setTextColor(getResources().getColorStateList(
            R.color.xml_text_hof_switch_thres));
        mSwitchOnline.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_thres));
        mSwitchOnline.setTextColor(getResources().getColorStateList(
            R.color.xml_text_hof_switch_thres));
        break;
      case DIFFUSE:
        mSwitchLayout.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_layout_diffuse));
        mSwitchLocal.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_diffuse));
        mSwitchLocal.setTextColor(getResources().getColorStateList(
            R.color.xml_text_hof_switch_diffuse));
        mSwitchOnline.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_diffuse));
        mSwitchOnline.setTextColor(getResources().getColorStateList(
            R.color.xml_text_hof_switch_diffuse));
        break;
      case CORRUPTED:
        mSwitchLayout.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_layout_corrupt));
        mSwitchLocal.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_corrupt));
        mSwitchLocal.setTextColor(getResources().getColorStateList(
            R.color.xml_text_hof_switch_corrupt));
        mSwitchOnline.setBackgroundDrawable(getResources().getDrawable(
            R.drawable.xml_bg_hof_switch_corrupt));
        mSwitchOnline.setTextColor(getResources().getColorStateList(
            R.color.xml_text_hof_switch_corrupt));
        break;
    }
  }
}