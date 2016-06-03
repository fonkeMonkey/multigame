package sk.palistudios.multigame.customization_center;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import sk.palistudios.multigame.BaseActivity;
import sk.palistudios.multigame.MgTracker;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.achievements.AchievementsFragment;
import sk.palistudios.multigame.customization_center.minigames.MinigamesFragment;
import sk.palistudios.multigame.customization_center.music.MusicFragment;
import sk.palistudios.multigame.customization_center.skins.SkinsFragment;
import sk.palistudios.multigame.tools.SkinManager;
import sk.palistudios.multigame.tools.sound.SoundEffectsCenter;

public class CustomizationCenterActivity extends BaseActivity
    implements ViewPager.OnPageChangeListener {
  /**
   * The number of pages (wizard steps) to show in this demo.
   */
  private static final int NUM_PAGES = 4;

  /**
   * The pager widget, which handles animation and allows swiping horizontally to access previous
   * and next wizard steps.
   */
  private ViewPager mPager;

  /**
   * The pager adapter, which provides the pages to the view pager widget.
   */
  private ScreenSlidePagerAdapter mPagerAdapter;
  private CheckedTextView minigamesButton;
  private CheckedTextView achievementsButton;
  private CheckedTextView musicButton;
  private CheckedTextView skinsButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    MgTracker.init(this);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.customization_center);

    // Instantiate a ViewPager and a PagerAdapter.
    mPager = (ViewPager) findViewById(R.id.pager);
    mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
    mPager.setAdapter(mPagerAdapter);

    mPager.setOnPageChangeListener(this);

    minigamesButton = (CheckedTextView) findViewById(R.id.customize_minigames);
    achievementsButton = (CheckedTextView) findViewById(R.id.customize_achievements);
    musicButton = (CheckedTextView) findViewById(R.id.customize_music);
    skinsButton = (CheckedTextView) findViewById(R.id.customize_skins);
    mPager.setCurrentItem(0);
    checkTextView(minigamesButton);

    minigamesButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SoundEffectsCenter.playTabSound(CustomizationCenterActivity.this);
        mPager.setCurrentItem(0);
        checkTextView(minigamesButton);
      }
    });
    skinsButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SoundEffectsCenter.playTabSound(CustomizationCenterActivity.this);
        mPager.setCurrentItem(1);
        checkTextView(skinsButton);
      }
    });
    musicButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SoundEffectsCenter.playTabSound(CustomizationCenterActivity.this);
        mPager.setCurrentItem(2);
        checkTextView(musicButton);
      }
    });
    achievementsButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SoundEffectsCenter.playTabSound(CustomizationCenterActivity.this);
        mPager.setCurrentItem(3);
        checkTextView(achievementsButton);
      }
    });
  }

  private void checkTextView(CheckedTextView textView) {
    minigamesButton.setChecked(textView.equals(minigamesButton) ? true : false);
    achievementsButton.setChecked(textView.equals(achievementsButton) ? true : false);
    musicButton.setChecked(textView.equals(musicButton) ? true : false);
    skinsButton.setChecked(textView.equals(skinsButton) ? true : false);
    refreshTabColors();
  }

  @Override
  protected void onResume() {
    super.onResume();
    refreshTabColors();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mPagerAdapter = null;
    mPager = null;
  }

  @Override
  public void reskinLocally(SkinManager.Skin currentSkin) {
    refreshTabColors();
    reskinFragments(currentSkin);
  }

  private void refreshTabColors() {
    minigamesButton.setBackgroundDrawable(SkinManager.getInstance().getTabDrawable(this));
    skinsButton.setBackgroundDrawable(SkinManager.getInstance().getTabDrawable(this));
    musicButton.setBackgroundDrawable(SkinManager.getInstance().getTabDrawable(this));
    achievementsButton.setBackgroundDrawable(SkinManager.getInstance().getTabDrawable(this));

    minigamesButton.setTextColor(SkinManager.getInstance().getTabInActiveTextColor(this));
    skinsButton.setTextColor(SkinManager.getInstance().getTabInActiveTextColor(this));
    musicButton.setTextColor(SkinManager.getInstance().getTabInActiveTextColor(this));
    achievementsButton.setTextColor(SkinManager.getInstance().getTabInActiveTextColor(this));

    if (minigamesButton.isChecked() || mPager.getCurrentItem() == 0) {
      minigamesButton.setTextColor(SkinManager.getInstance().getTabActiveTextColor(this));
    }
    if (skinsButton.isChecked() || mPager.getCurrentItem() == 1) {
      skinsButton.setTextColor(SkinManager.getInstance().getTabActiveTextColor(this));
    }
    if (musicButton.isChecked() || mPager.getCurrentItem() == 2) {
      musicButton.setTextColor(SkinManager.getInstance().getTabActiveTextColor(this));
    }
    if (achievementsButton.isChecked() || mPager.getCurrentItem() == 3) {
      achievementsButton.setTextColor(SkinManager.getInstance().getTabActiveTextColor(this));
    }
  }

  public void reskinFragments(SkinManager.Skin currentSkin) {
    if (mPagerAdapter.getFragments() != null) {
      for (CustomizeFragment fragment : mPagerAdapter.getFragments()) {
        fragment.reskinLocally(currentSkin);
      }
    }
  }

  @Override
  public void onPageScrolled(int i, float v, int i1) {

  }

  @Override
  public void onPageSelected(int i) {
    switch (i) {
      case 0:
        checkTextView(minigamesButton);
        break;
      case 1:
        checkTextView(skinsButton);
        break;
      case 2:
        checkTextView(musicButton);
        break;
      case 3:
        checkTextView(achievementsButton);
        break;
      default:
        throw new RuntimeException("Pager's onPageSelected() out of bounds.");
    }
  }

  @Override
  public void onPageScrollStateChanged(int i) {

  }

  /**
   * A simple pager adapter that represents 4 ScreenSlidePageFragment objects, in sequence.
   */
  private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    CustomizeFragment[] mFragments = new CustomizeFragment[NUM_PAGES];

    public ScreenSlidePagerAdapter(FragmentManager fm) {
      super(fm);
      mFragments[0] = new MinigamesFragment();
      //      mFragments[0] = new SkinsFragment();
      mFragments[1] = new SkinsFragment();
      mFragments[2] = new MusicFragment();
      mFragments[3] = new AchievementsFragment();
    }

    private CustomizeFragment[] getFragments() {
      return mFragments;
    }

    @Override
    public Fragment getItem(int position) {
      switch (position) {
        case 0:
          return mFragments[0];
        case 1:
          return mFragments[1];
        case 2:
          return mFragments[2];
        case 3:
          return mFragments[3];
      }
      throw new RuntimeException("Pager's getItem() out of bounds.");
    }

    @Override
    public int getCount() {
      return NUM_PAGES;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      ////      super.destroyItem(container,position,object);
      //TODO M handle more sexy, this is to not populate viewpagers fragments everytime viewpager
      // is scrolled. Yea and the method of parent is not called, so could destory a fet things,
      // startup ftw.
    }
  }
}