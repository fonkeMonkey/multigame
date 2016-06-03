package sk.palistudios.multigame.customization_center.minigames;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import sk.palistudios.multigame.game.persistence.MGSettings;
import sk.palistudios.multigame.tools.BitmapHelper;
import sk.palistudios.multigame.tools.MemoryUtil;
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
    tmpChosenMinigames = MGSettings.getChosenMinigamesNames();
  }

  private void initAdapter() {
    mItems.add(new MgcItem(MinigamesFragment.SYMBOL_MINIGAME_HORIZONTAL, "HBalance", "Balance",
        MGSettings.isMinigameChosen("HBalance")));
    mItems.add(new MgcItem(MinigamesFragment.SYMBOL_MINIGAME_TOUCH, "TCatcher", "Catcher",
        MGSettings.isMinigameChosen("TCatcher")));
    mItems.add(new MgcItem(MinigamesFragment.SYMBOL_MINIGAME_TOUCH, "TGatherer", "Gatherer",
        MGSettings.isMinigameChosen("TGatherer")));
    mItems.add(new MgcItem(MinigamesFragment.SYMBOL_MINIGAME_TOUCH, "TInvader", "Invader",
        MGSettings.isMinigameChosen("TInvader"),
        (String) getResources().getString(R.string.cc_achievements_addicts_description) +
            (String) getResources().getString(R.string.cc_achievements_requirement_ending)));
    mItems.add(new MgcItem(MinigamesFragment.SYMBOL_MINIGAME_VERTICAL, "VBird", "Bird",
        MGSettings.isMinigameChosen("VBird")));
    mItems.add(new MgcItem(MinigamesFragment.SYMBOL_MINIGAME_VERTICAL, "VBouncer", "Bouncer",
        MGSettings.isMinigameChosen("VBouncer"),
        (String) getResources().getString(R.string.cc_achievements_good_start_description) +
            (String) getResources().getString(R.string.cc_achievements_requirement_ending)));
    mMinigamesAdapter = new MgcArrayAdapter(getActivity(), mItems);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.cust_minigames_layout, container,
        false);

    mBalance = (ImageView) rootView.findViewById(R.id.balance);
    mCatcher = (ImageView) rootView.findViewById(R.id.catcher);
    mGatherer = (ImageView) rootView.findViewById(R.id.gatherer);
    mInvader = (ImageView) rootView.findViewById(R.id.invader);
    mBird = (ImageView) rootView.findViewById(R.id.bird);
    mBouncer = (ImageView) rootView.findViewById(R.id.bouncer);

    //TODO M Tieto imageviews zajebú 10Mb RAMky pre high a 5 pre low
    // tieto dekodóvačky zavolaj z BitmapHelperu s odmeranými veľkosťami pre tie obrázky (teraz
    // majú veľkosť fullscreen) -> Niekde v onMeasure alebo v nejakej kokocine(aka
    // ViewTreeObserver odmeraj
    // tie Imageviews
    boolean highQuality = !MemoryUtil.isLowMemoryDevice(getActivity());
    mBalance.setImageBitmap(BitmapHelper
        .decodeSampledBitmapFromResource(getActivity(), R.drawable.icon_minigame_balance,
            highQuality));
    mCatcher.setImageBitmap(BitmapHelper
        .decodeSampledBitmapFromResource(getActivity(), R.drawable.icon_minigame_catcher,
            highQuality));
    mGatherer.setImageBitmap(BitmapHelper
        .decodeSampledBitmapFromResource(getActivity(), R.drawable.icon_minigame_gatherer,
            highQuality));

    if (mItems.get(3).isLocked()) {
      mInvader.setImageBitmap(BitmapHelper
          .decodeSampledBitmapFromResource(getActivity(), R.drawable.icon_minigame_invader_disabled,
              highQuality));
    } else {
      mInvader.setImageBitmap(BitmapHelper
          .decodeSampledBitmapFromResource(getActivity(), R.drawable.icon_minigame_invader,
              highQuality));
    }
    mBird.setImageBitmap(BitmapHelper
        .decodeSampledBitmapFromResource(getActivity(), R.drawable.icon_minigame_bird,
            highQuality));

    if (mItems.get(5).isLocked()) {
      mBouncer.setImageBitmap(BitmapHelper
          .decodeSampledBitmapFromResource(getActivity(), R.drawable.icon_minigame_bouncer_disabled,
              highQuality));
    } else {
      mBouncer.setImageBitmap(BitmapHelper
          .decodeSampledBitmapFromResource(getActivity(), R.drawable.icon_minigame_bouncer,
              highQuality));
    }

    return rootView;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
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
    MGSettings.SetChosenMinigamesNames(tmpChosenMinigames);
    mMinigamesAdapter.notifyDataSetChanged();
    refreshImageBorders(SkinManager.getInstance().getCurrentSkin());
  }

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

    {
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
    if (mItems.get(5).isActive()) {
      mBouncer.setBackgroundDrawable(activeDrawable);
    } else {
      mBouncer.setBackgroundDrawable(inactiveDrawable);
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

  public void recycleImages() {
    if (mBird != null) {
      Drawable drawable = mBird.getDrawable();
      if (drawable instanceof BitmapDrawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bitmap.recycle();
      }
    }
    if (mGatherer != null) {
      Drawable drawable2 = mGatherer.getDrawable();
      if (drawable2 instanceof BitmapDrawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable2;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bitmap.recycle();
      }
    }
    if (mBalance != null) {
      Drawable drawable3 = mBalance.getDrawable();
      if (drawable3 instanceof BitmapDrawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable3;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bitmap.recycle();
      }
    }
    if (mCatcher != null) {
      Drawable drawable4 = mCatcher.getDrawable();
      if (drawable4 instanceof BitmapDrawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable4;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bitmap.recycle();
      }
    }
    if (mInvader != null) {
      Drawable drawable5 = mInvader.getDrawable();
      if (drawable5 instanceof BitmapDrawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable5;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bitmap.recycle();
      }
    }
    if (mBouncer != null) {
      Drawable drawable6 = mBouncer.getDrawable();
      if (drawable6 instanceof BitmapDrawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable6;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bitmap.recycle();
      }
    }

    mBird = null;
    mBalance = null;
    mCatcher = null;
    mGatherer = null;
    mBouncer = null;
    mInvader = null;
  }

  @Override
  public void reskinLocally(SkinManager.Skin currentSkin) {
    super.reskinLocally(currentSkin);
    if (isAdded()) {
      refreshImageBorders(currentSkin);
    }
  }
}
