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
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import sk.palistudios.multigame.MgTracker;
import sk.palistudios.multigame.R;
import sk.palistudios.multigame.customization_center.CustomizeFragment;
import sk.palistudios.multigame.game.minigames.BaseMiniGame;
import sk.palistudios.multigame.game.persistence.MGSettings;
import sk.palistudios.multigame.tools.BitmapHelper;
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
  private ImageView mInvaderView;
  private ImageView mCatcherView;
  private ImageView mBalanceView;
  private ImageView mGathererView;
  private ImageView mBirdView;
  private ImageView mBouncerView;
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

    mBalanceView = (ImageView) rootView.findViewById(R.id.balance);
    mCatcherView = (ImageView) rootView.findViewById(R.id.catcher);
    mGathererView = (ImageView) rootView.findViewById(R.id.gatherer);
    mInvaderView = (ImageView) rootView.findViewById(R.id.invader);
    mBirdView = (ImageView) rootView.findViewById(R.id.bird);
    mBouncerView = (ImageView) rootView.findViewById(R.id.bouncer);

    //We need to what size the bitmap should be decoded. Lets say all of them have the same size.
    ViewTreeObserver viewTreeObserver = mBalanceView.getViewTreeObserver();
    if (viewTreeObserver.isAlive()) {
      viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
          mBalanceView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
          int width = mBalanceView.getWidth();
          int height = mBalanceView.getHeight();
          loadBitmaps(width, height);
        }
      });
    }

    return rootView;
  }

  private void loadBitmaps(int width, int height) {
    //TODO M Tieto imageviews zajebú 10Mb RAMky pre high a 5 pre low
    // tieto dekodóvačky zavolaj z BitmapHelperu s odmeranými veľkosťami pre tie obrázky (teraz
    // majú veľkosť fullscreen) -> Niekde v onMeasure alebo v nejakej kokocine(aka
    // ViewTreeObserver odmeraj
    // tie Imageviews
    mBalanceView.setImageBitmap(BitmapHelper
        .decodeSampledBitmapFromResource(getActivity(), R.drawable.icon_minigame_balance, width,
            height));
    mCatcherView.setImageBitmap(BitmapHelper
        .decodeSampledBitmapFromResource(getActivity(), R.drawable.icon_minigame_catcher, width,
            height));
    mGathererView.setImageBitmap(BitmapHelper
        .decodeSampledBitmapFromResource(getActivity(), R.drawable.icon_minigame_gatherer, width,
            height));

    if (mItems.get(3).isLocked()) {
      mInvaderView.setImageBitmap(BitmapHelper
          .decodeSampledBitmapFromResource(getActivity(), R.drawable.icon_minigame_invader_disabled,
              width, height));
    } else {
      mInvaderView.setImageBitmap(BitmapHelper
          .decodeSampledBitmapFromResource(getActivity(), R.drawable.icon_minigame_invader, width,
              height));
    }
    mBirdView.setImageBitmap(BitmapHelper
        .decodeSampledBitmapFromResource(getActivity(), R.drawable.icon_minigame_bird, width,
            height));

    if (mItems.get(5).isLocked()) {
      mBouncerView.setImageBitmap(BitmapHelper
          .decodeSampledBitmapFromResource(getActivity(), R.drawable.icon_minigame_bouncer_disabled,
              width, height));
    } else {
      mBouncerView.setImageBitmap(BitmapHelper
          .decodeSampledBitmapFromResource(getActivity(), R.drawable.icon_minigame_bouncer, width,
              height));
    }
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    refreshImageBorders(SkinManager.getInstance().getCurrentSkin());

    mInvaderView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setNewActiveMinigameIfPossible(BaseMiniGame.Minigame.INVADER);
      }
    });
    mCatcherView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setNewActiveMinigameIfPossible(BaseMiniGame.Minigame.CATCHER);
      }
    });
    mBalanceView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setNewActiveMinigameIfPossible(BaseMiniGame.Minigame.BALANCE);
      }
    });
    mGathererView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setNewActiveMinigameIfPossible(BaseMiniGame.Minigame.GATHERER);
      }
    });
    mBirdView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setNewActiveMinigameIfPossible(BaseMiniGame.Minigame.BIRD);
      }
    });
    mBouncerView.setOnClickListener(new View.OnClickListener() {
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
      mBalanceView.setBackgroundDrawable(activeDrawable);
    } else {
      mBalanceView.setBackgroundDrawable(inactiveDrawable);
    }

    if (mItems.get(1).isActive()) {
      mCatcherView.setBackgroundDrawable(activeDrawable);
    } else {
      mCatcherView.setBackgroundDrawable(inactiveDrawable);
    }

        /* Invader */
    if (mItems.get(2).isActive()) {
      mGathererView.setBackgroundDrawable(activeDrawable);
    } else {
      mGathererView.setBackgroundDrawable(inactiveDrawable);
    }

    {
      if (mItems.get(3).isActive()) {
        mInvaderView.setBackgroundDrawable(activeDrawable);
      } else {
        mInvaderView.setBackgroundDrawable(inactiveDrawable);
      }
    }

    /* Bird */
    if (mItems.get(4).isActive()) {
      mBirdView.setBackgroundDrawable(activeDrawable);
    } else {
      mBirdView.setBackgroundDrawable(inactiveDrawable);
    }

    /* Bouncer */
    if (mItems.get(5).isActive()) {
      mBouncerView.setBackgroundDrawable(activeDrawable);
    } else {
      mBouncerView.setBackgroundDrawable(inactiveDrawable);
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
  public void onDestroyView() {
    super.onDestroyView();
    recycleImages();
  }

  private void recycleImages() {
    if (mBirdView != null) {
      Drawable drawable = mBirdView.getDrawable();
      if (drawable instanceof BitmapDrawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bitmap.recycle();
      }
    }
    if (mGathererView != null) {
      Drawable drawable2 = mGathererView.getDrawable();
      if (drawable2 instanceof BitmapDrawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable2;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bitmap.recycle();
      }
    }
    if (mBalanceView != null) {
      Drawable drawable3 = mBalanceView.getDrawable();
      if (drawable3 instanceof BitmapDrawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable3;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bitmap.recycle();
      }
    }
    if (mCatcherView != null) {
      Drawable drawable4 = mCatcherView.getDrawable();
      if (drawable4 instanceof BitmapDrawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable4;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bitmap.recycle();
      }
    }
    if (mInvaderView != null) {
      Drawable drawable5 = mInvaderView.getDrawable();
      if (drawable5 instanceof BitmapDrawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable5;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bitmap.recycle();
      }
    }
    if (mBouncerView != null) {
      Drawable drawable6 = mBouncerView.getDrawable();
      if (drawable6 instanceof BitmapDrawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable6;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bitmap.recycle();
      }
    }

    mBirdView = null;
    mBalanceView = null;
    mCatcherView = null;
    mGathererView = null;
    mBouncerView = null;
    mInvaderView = null;
  }

  @Override
  public void reskinLocally(SkinManager.Skin currentSkin) {
    super.reskinLocally(currentSkin);
    if (isAdded()) {
      refreshImageBorders(currentSkin);
    }
  }
}
