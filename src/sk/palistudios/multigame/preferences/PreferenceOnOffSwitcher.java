package sk.palistudios.multigame.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.tools.SkinManager;

public class PreferenceOnOffSwitcher extends LinearLayout implements Checkable {

  public static interface OnCheckedChangeListener {
    void onCheckedChanged(PreferenceOnOffSwitcher buttonView, boolean isChecked);
  }

  public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
    mOnCheckedChangeListener = listener;
  }

  private View mSwitcherButton;
  private boolean mChecked = false;

  private TextView mTvTitle;
  private TextView mTvStatus;
  private String mTitleText;

  private OnCheckedChangeListener mOnCheckedChangeListener;

  public PreferenceOnOffSwitcher(Context context) {
    this(context, null);
  }

  public PreferenceOnOffSwitcher(Context context, AttributeSet attrs) {
    super(context, attrs);

    TypedArray a = context.obtainStyledAttributes(attrs,
        R.styleable.PreferenceOnOffSwitcher, 0, 0);

    int titleTextId = a.getResourceId(R.styleable.PreferenceOnOffSwitcher_switcherTitle, 0);
    if (titleTextId != 0) {
      mTitleText = getResources().getString(titleTextId);
    } else {
      mTitleText = a.getString(R.styleable.PreferenceOnOffSwitcher_switcherTitle);
    }

    mChecked = a.getBoolean(R.styleable.PreferenceOnOffSwitcher_switcherChecked, false);

    a.recycle();

    createViews();
  }

  private void createViews() {
    inflate(getContext(), R.layout.preference_switcher, this);

    LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    params.setMargins(0, 0, 0, 0);
    setLayoutParams(params);
    setPadding(0, 0, 0, 0);
    setGravity(Gravity.CENTER_VERTICAL);
    setOrientation(LinearLayout.HORIZONTAL);

    mSwitcherButton = findViewById(R.id.preference_switcher);
    mSwitcherButton.setId(NO_ID);
    mSwitcherButton.setClickable(true);
    mSwitcherButton.setFocusable(true);
    mSwitcherButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        PreferenceOnOffSwitcher.this.performClick();
      }
    });

    mTvTitle = (TextView) findViewById(R.id.title);
    mTvTitle.setText(mTitleText);

    mTvStatus = (TextView) findViewById(R.id.status);
    mTvStatus.setText(mChecked ? getResources().getString(R.string.pref_on)
        : getResources().getString(R.string.pref_off));

  }

  @Override
  public boolean performClick() {
    toggle();
    return super.performClick();
  }

  @Override
  public void setChecked(boolean checked) {
    if(mChecked != checked) {
      mChecked = checked;

      if(mOnCheckedChangeListener != null) {
        mOnCheckedChangeListener.onCheckedChanged(this, checked);
      }

      mTvStatus.setText(mChecked ? getResources().getString(R.string.pref_on)
          : getResources().getString(R.string.pref_off));

      reskinDynamically();

      invalidate();
    }
  }

  public void reskinDynamically() {
    mTvStatus.setTextColor(mChecked ? SkinManager.getInstance().getCurrentTextColor
        (getResources()) : SkinManager.getInstance().getCurrentTextColorDisabled(getResources()));

    mTvTitle.setTextColor(mChecked ? SkinManager.getInstance().getCurrentTextColor
        (getResources()) : SkinManager.getInstance().getCurrentTextColorDisabled(
        getResources()));

    mTvStatus.invalidate();
    mTvTitle.invalidate();
  }

  @Override
  public boolean isChecked() {
    return mChecked;
  }

  @Override
  public void toggle() {
    setChecked(!isChecked());
  }

  @Override
  protected Parcelable onSaveInstanceState() {
    Parcelable superState = super.onSaveInstanceState();

    SavedState ss = new SavedState(superState);

    ss.checked = mChecked;

    return ss;
  }

  @Override
  protected void onRestoreInstanceState(Parcelable state) {
    SavedState ss = (SavedState) state;
    super.onRestoreInstanceState(ss.getSuperState());
    setChecked(ss.checked);
    requestLayout();
  }

  static class SavedState extends BaseSavedState {
    boolean checked;

    SavedState(Parcelable superState) {
      super(superState);
    }

    private SavedState(Parcel in) {
      super(in);
      this.checked = (Boolean) in.readValue(null);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
      super.writeToParcel(out, flags);
      out.writeValue(checked);
    }

    //required field that makes Parcelables from a Parcel
    public static final Parcelable.Creator<SavedState> CREATOR =
        new Parcelable.Creator<SavedState>() {
          public SavedState createFromParcel(Parcel in) {
            return new SavedState(in);
          }
          public SavedState[] newArray(int size) {
            return new SavedState[size];
          }
        };
  }
}
