package sk.palistudios.multigame.customization_center.minigames;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import sk.palistudios.multigame.MgTracker;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.CustomizeFragment;
import sk.palistudios.multigame.game.minigames.BaseMiniGame;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.tools.SkinManager;
import sk.palistudios.multigame.tools.Toaster;

/**
 * @author Pali
 */
public class MinigamesFragment extends CustomizeFragment {

  public static char SYMBOL_MINIGAME_HORIZONTAL = '⇆';
  public static char SYMBOL_MINIGAME_TOUCH = '⇩';
  public static char SYMBOL_MINIGAME_VERTICAL = '⇅';

  private static String[] tmpChosenMinigames = new String[4];
  private ImageView mInvader;
  private ImageView mCatcher;
  private ImageView mBalance;
  private ImageView mGatherer;
  private ImageView mBird;
  private ImageView mBouncer;
  private MgcArrayAdapter mMinigamesAdapter;
  private ArrayList<MgcItem> mItems = new ArrayList<MgcItem>();

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    initAdapter();
    tmpChosenMinigames = GameSharedPref.getChosenMinigamesNames();
  }

  private void initAdapter() {
    mItems.add(new MgcItem(MinigamesFragment.SYMBOL_MINIGAME_HORIZONTAL, "HBalance", "Balance",
        GameSharedPref.isMinigameChosen("HBalance")));
    mItems.add(new MgcItem(MinigamesFragment.SYMBOL_MINIGAME_TOUCH, "TCatcher", "Catcher",
        GameSharedPref.isMinigameChosen("TCatcher")));
    mItems.add(new MgcItem(MinigamesFragment.SYMBOL_MINIGAME_TOUCH, "TGatherer", "Gatherer",
        GameSharedPref.isMinigameChosen("TGatherer")));
    mItems.add(new MgcItem(MinigamesFragment.SYMBOL_MINIGAME_TOUCH, "TInvader", "Invader",
        GameSharedPref.isMinigameChosen("TInvader"), (String) getResources().getString(
        R.string.cc_achievements_addicts_description) + (String) getResources().getString(
        R.string.cc_achievements_requirement_ending)));
    mItems.add(new MgcItem(MinigamesFragment.SYMBOL_MINIGAME_VERTICAL, "VBird", "Bird",
        GameSharedPref.isMinigameChosen("VBird")));
    mItems.add(new MgcItem(MinigamesFragment.SYMBOL_MINIGAME_VERTICAL, "VBouncer", "Bouncer",
        GameSharedPref.isMinigameChosen("VBouncer"), (String) getResources().getString(
        R.string.cc_achievements_good_start_description) + (String) getResources().getString(
        R.string.cc_achievements_requirement_ending)));
    mMinigamesAdapter = new MgcArrayAdapter(getActivity(), mItems);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.cust_minigames_layout, container,
        false);
    return rootView;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mBalance = (ImageView) getView().findViewById(R.id.balance);
    mCatcher = (ImageView) getView().findViewById(R.id.catcher);
    mGatherer = (ImageView) getView().findViewById(R.id.gatherer);
    mInvader = (ImageView) getView().findViewById(R.id.invader);
    mBird = (ImageView) getView().findViewById(R.id.bird);
    mBouncer = (ImageView) getView().findViewById(R.id.bouncer);
    refreshImageBorders(SkinManager.getInstance().getCurrentSkin());

    mInvader.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setNewActiveMinigameIfPossible(BaseMiniGame.Minigame.INVADER);
      }
    });
    mCatcher.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setNewActiveMinigameIfPossible(BaseMiniGame.Minigame.CATCHER);
      }
    });
    mBalance.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setNewActiveMinigameIfPossible(BaseMiniGame.Minigame.BALANCE);
      }
    });
    mGatherer.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setNewActiveMinigameIfPossible(BaseMiniGame.Minigame.GATHERER);
      }
    });
    mBird.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setNewActiveMinigameIfPossible(BaseMiniGame.Minigame.BIRD);
      }
    });
    mBouncer.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setNewActiveMinigameIfPossible(BaseMiniGame.Minigame.BOUNCER);
      }
    });
  }

  private void setNewActiveMinigameIfPossible(BaseMiniGame.Minigame minigame) {
    String minigameToChange;
    switch (minigame) {
      case BALANCE:
        MgTracker.trackMinigameChanged("Balance");
        tmpChosenMinigames[1] = "HBalance";
        mMinigamesAdapter.activateItem(SYMBOL_MINIGAME_HORIZONTAL, 1);
        break;
      case BOUNCER:
        if (mMinigamesAdapter.getItem(6 - 1).isLocked()) {
          Toaster.toastLong(mMinigamesAdapter.getItem(6 - 1).getLockedDescription(),
              getActivity().getApplicationContext());
          return;
        }
        MgTracker.trackMinigameChanged("Bouncer");
        tmpChosenMinigames[0] = "VBouncer";
        mMinigamesAdapter.activateItem(SYMBOL_MINIGAME_VERTICAL, 6);
        break;
      case BIRD:
        MgTracker.trackMinigameChanged("Bird");
        tmpChosenMinigames[0] = "VBird";
        mMinigamesAdapter.activateItem(SYMBOL_MINIGAME_VERTICAL, 5);
        break;
      case CATCHER:
        MgTracker.trackMinigameChanged("Catcher");
        minigameToChange = mMinigamesAdapter.activateItem(SYMBOL_MINIGAME_TOUCH, 2);
        if ("TGatherer".equals(minigameToChange)) {
          tmpChosenMinigames[2] = "TCatcher";
          tmpChosenMinigames[3] = "TInvader";
        }
        if ("TInvader".equals(minigameToChange)) {
          tmpChosenMinigames[2] = "TCatcher";
          tmpChosenMinigames[3] = "TGatherer";
        }
        break;
      case GATHERER:
        MgTracker.trackMinigameChanged("Gatherer");
        minigameToChange = mMinigamesAdapter.activateItem(SYMBOL_MINIGAME_TOUCH, 3);

        if ("TCatcher".equals(minigameToChange)) {
          tmpChosenMinigames[2] = "TGatherer";
          tmpChosenMinigames[3] = "TInvader";
        }
        if ("TInvader".equals(minigameToChange)) {
          tmpChosenMinigames[2] = "TCatcher";
          tmpChosenMinigames[3] = "TGatherer";
        }
        break;
      case INVADER:
        if (mMinigamesAdapter.getItem(4 - 1).isLocked()) {
          Toaster.toastLong(mMinigamesAdapter.getItem(4 - 1).getLockedDescription(),
              getActivity().getApplicationContext());
          return;
        }

        MgTracker.trackMinigameChanged("Invader");
        minigameToChange = mMinigamesAdapter.activateItem(SYMBOL_MINIGAME_TOUCH, 4);
        if ("TCatcher".equals(minigameToChange)) {
          tmpChosenMinigames[2] = "TGatherer";
          tmpChosenMinigames[3] = "TInvader";
        }
        if ("TGatherer".equals(minigameToChange)) {
          tmpChosenMinigames[2] = "TInvader";
          tmpChosenMinigames[3] = "TCatcher";
        }
        break;
    }
    GameSharedPref.SetChosenMinigamesNames(tmpChosenMinigames);
    mMinigamesAdapter.notifyDataSetChanged();
    refreshImageBorders(SkinManager.getInstance().getCurrentSkin());
  }

  //TODO M možno cez bitmapfactory, lebo toto dekóduje bitmapu na mainthreade (pomalé)
  //TODO a taktiež sa to volá skurvene často hlavne to načítavanie bitmap je useless pri zmene
  // skinov o.i
  private void refreshImageBorders(SkinManager.Skin currentSkin) {
    Drawable activeDrawable = getActiveDrawableBySkin(currentSkin);
    Drawable inactiveDrawable = getInactiveDrawableBySkin(currentSkin);

    if (mItems.get(0).isActive()) {
      mBalance.setBackgroundDrawable(activeDrawable);
    } else {
      mBalance.setBackgroundDrawable(inactiveDrawable);
    }

    if (mItems.get(1).isActive()) {
      mCatcher.setBackgroundDrawable(activeDrawable);
    } else {
      mCatcher.setBackgroundDrawable(inactiveDrawable);
    }

        /* Invader */
    if (mItems.get(2).isActive()) {
      mGatherer.setBackgroundDrawable(activeDrawable);
    } else {
      mGatherer.setBackgroundDrawable(inactiveDrawable);
    }

        /* Invader */
    if (mItems.get(3).isLocked()) {
      mInvader.setImageResource(R.drawable.icon_minigame_invader_disabled);
      mInvader.setOnClickListener(null);
    } else {
      if (mItems.get(3).isActive()) {
        mInvader.setBackgroundDrawable(activeDrawable);
      } else {
        mInvader.setBackgroundDrawable(inactiveDrawable);
      }
    }

    /* Bird */
    if (mItems.get(4).isActive()) {
      mBird.setBackgroundDrawable(activeDrawable);
    } else {
      mBird.setBackgroundDrawable(inactiveDrawable);
    }

    /* Bouncer */
    if (mItems.get(5).isLocked()) {
      mBouncer.setImageResource(R.drawable.icon_minigame_bouncer_disabled);
      mBouncer.setOnClickListener(null);
    } else {
      if (mItems.get(5).isActive()) {
        mBouncer.setBackgroundDrawable(activeDrawable);
      } else {
        mBouncer.setBackgroundDrawable(inactiveDrawable);
      }
    }

  }

  private Drawable getActiveDrawableBySkin(SkinManager.Skin currentSkin) {
    switch (currentSkin) {
      case QUAD:
        return getResources().getDrawable(R.drawable.xml_bg_cust_minigames_chosen_quad_diff);
      case THRESHOLD:
        return getResources().getDrawable(R.drawable.xml_bg_cust_minigames_chosen_thres);
      case DIFFUSE:
        return getResources().getDrawable(R.drawable.xml_bg_cust_minigames_chosen_quad_diff);
      case CORRUPTED:
        return getResources().getDrawable(R.drawable.xml_bg_cust_minigames_chosen_corr);
    }
    throw new RuntimeException("Invalid Skin");
  }

  private Drawable getInactiveDrawableBySkin(SkinManager.Skin currentSkin) {
    switch (currentSkin) {
      case QUAD:
        return getResources().getDrawable(R.drawable.xml_bg_cust_minigames_unchosen_quad_diff);
      case THRESHOLD:
        return getResources().getDrawable(R.drawable.xml_bg_cust_minigames_unchosen_thres);
      case DIFFUSE:
        return getResources().getDrawable(R.drawable.xml_bg_cust_minigames_unchosen_quad_diff);
      case CORRUPTED:
        return getResources().getDrawable(R.drawable.xml_bg_cust_minigames_unchosen_corr);
    }
    throw new RuntimeException("Invalid Skin");
  }

  @Override
  public void reskinLocally(SkinManager.Skin currentSkin) {
    super.reskinLocally(currentSkin);
    if (isAdded()) {
      refreshImageBorders(currentSkin);
    }
  }
}
