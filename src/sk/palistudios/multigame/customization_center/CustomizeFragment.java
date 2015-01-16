package sk.palistudios.multigame.customization_center;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sk.palistudios.multigame.R;
import sk.palistudios.multigame.tools.SkinManager;

/**
 * @author Pali
 */
abstract public class CustomizeFragment extends Fragment {
  protected TextView mHeader;

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mHeader = (TextView) view.findViewById(R.id.header);
  }

  @Override
  public void onResume() {
    super.onResume();
    reskinLocally(SkinManager.getInstance().getCurrentSkin());
  }

  public void reskinLocally(SkinManager.Skin currentSkin) {
    if (isAdded()) {
      SkinManager.reskin(getActivity(), (ViewGroup) ((ViewGroup) (getActivity().findViewById(
          android.R.id.content))));
//      SkinManager.reskin(getActivity(), (ViewGroup) ((ViewGroup) (getActivity().findViewById(
//          android.R.id.content))).getChildAt(0));
    }
  }
}
