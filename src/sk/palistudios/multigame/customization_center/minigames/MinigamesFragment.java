package sk.palistudios.multigame.customization_center.minigames;

import java.util.ArrayList;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import sk.palistudios.multigame.MgTracker;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.game.minigames.BaseMiniGame;
import sk.palistudios.multigame.game.persistence.GameSharedPref;
import sk.palistudios.multigame.tools.DisplayHelper;
import sk.palistudios.multigame.tools.Toaster;

/**
 * @author Pali
 */
public class MinigamesFragment extends Fragment {

  public static char SYMBOL_MINIGAME_HORIZONTAL = '⇆';
  public static char SYMBOL_MINIGAME_TOUCH = '⇩';
  public static char SYMBOL_MINIGAME_VERTICAL = '⇅';

  private static String[] tmpChosenMinigames = new String[4];
  private MgcArrayAdapter mMinigamesAdapter;
  private ImageView mInvader;
  private ImageView mCatcher;
  private ImageView mBalance;
  private ImageView mGatherer;
  private ImageView mBird;
  private ImageView mBouncer;
  private ArrayList<MgcItem> mItems = new ArrayList<MgcItem>();
  private LinearLayout mBirdBorder;
  private LinearLayout mBouncerBorder;

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
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mBalance = (ImageView) getView().findViewById(R.id.balance);
    mCatcher = (ImageView) getView().findViewById(R.id.catcher);
    mGatherer = (ImageView) getView().findViewById(R.id.gatherer);
    mInvader = (ImageView) getView().findViewById(R.id.invader);
    mBird = (ImageView) getView().findViewById(R.id.bird);
//    mBirdBorder = (LinearLayout) getView().findViewById(R.id.bird_border);
    mBouncer = (ImageView) getView().findViewById(R.id.bouncer);
//    mBouncerBorder = (LinearLayout) getView().findViewById(R.id.bouncer_border);
    refreshImageStates();

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
    refreshImageStates();
  }

  //TODO M možno cez bitmapfactory, lebo toto dekóduje bitmapu na mainthreade (pomalé)
  private void refreshImageStates() {
    Resources resources = getResources();

    if (mItems.get(0).isChosen()) {
      mBalance.setBackgroundDrawable(resources.getDrawable(
          R.drawable.xml_bg_cust_minigames_chosen));
    } else {
      mBalance.setBackgroundDrawable(resources.getDrawable(
          R.drawable.xml_bg_cust_minigames_unchosen));
    }

    if (mItems.get(1).isChosen()) {
      mCatcher.setBackgroundDrawable(resources.getDrawable(
          R.drawable.xml_bg_cust_minigames_chosen));
    } else {
      mCatcher.setBackgroundDrawable(resources.getDrawable(
          R.drawable.xml_bg_cust_minigames_unchosen));
    }

        /* Invader */
    if (mItems.get(2).isChosen()) {
      mGatherer.setBackgroundDrawable(resources.getDrawable(
          R.drawable.xml_bg_cust_minigames_chosen));
    } else {
      mGatherer.setBackgroundDrawable(resources.getDrawable(
          R.drawable.xml_bg_cust_minigames_unchosen));
    }

        /* Invader */
    if (mItems.get(3).isLocked()) {
      mInvader.setImageResource(R.drawable.icon_minigame_invader_disabled);
      mInvader.setOnClickListener(null);
    } else {
      if (mItems.get(3).isChosen()) {
        mInvader.setBackgroundDrawable(resources.getDrawable(
            R.drawable.xml_bg_cust_minigames_chosen));
      } else {
        mInvader.setBackgroundDrawable(resources.getDrawable(
            R.drawable.xml_bg_cust_minigames_unchosen));
      }
    }

    /* Bird */
    if (mItems.get(4).isChosen()) {
      mBird.setBackgroundDrawable(resources.getDrawable(R.drawable.xml_bg_cust_minigames_chosen));
    } else {
      mBird.setBackgroundDrawable(resources.getDrawable(R.drawable.xml_bg_cust_minigames_unchosen));
    }

    /* Bouncer */
    if (mItems.get(5).isLocked()) {
      mBouncer.setImageResource(R.drawable.icon_minigame_bouncer_disabled);
      mBouncer.setOnClickListener(null);
    } else {
      if (mItems.get(5).isChosen()) {
        mBouncer.setBackgroundDrawable(resources.getDrawable(
            R.drawable.xml_bg_cust_minigames_chosen));
      } else {
        mBouncer.setBackgroundDrawable(resources.getDrawable(
            R.drawable.xml_bg_cust_minigames_unchosen));
      }
    }

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.cust_minigames_layout, container,
        false);
    ((TextView) rootView.findViewById(R.id.header)).setTextColor(((TextView) rootView.findViewById(
        R.id.header)).getTextColors().withAlpha(DisplayHelper.ALPHA_80pc));
    return rootView;
  }
}
