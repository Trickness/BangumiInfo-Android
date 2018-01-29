package moe.exmagic.tricks.banguminews.Fragments.HomePage;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import moe.exmagic.tricks.banguminews.R;

/**
 * Created by Stern on 2018/1/25.
 */

public class FragmentGroup extends Fragment {
    private View mRootView = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(mRootView != null)
            return mRootView;
        View v = inflater.inflate(R.layout.fragment_main_group, container, false);
        mRootView = v;
        return v;
    }

    public static FragmentGroup newInstance() {
        FragmentGroup fragment = new FragmentGroup();
        return fragment;
    }
}
