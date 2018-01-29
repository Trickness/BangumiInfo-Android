package moe.exmagic.tricks.banguminews.Views;

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

public class ItemDivider extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_divider, container, false);
    }

    public static ItemDivider newInstance() {
        ItemDivider fragment = new ItemDivider();
        return fragment;
    }
}
