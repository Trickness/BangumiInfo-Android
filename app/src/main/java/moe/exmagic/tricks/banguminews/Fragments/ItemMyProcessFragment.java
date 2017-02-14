package moe.exmagic.tricks.banguminews.Fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType.*;

/**
 * Created by tricks on 17-2-8.
 */

public class ItemMyProcessFragment extends Fragment {
    public static ItemMyProcessFragment newInstance(){
        return new ItemMyProcessFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_myprocess,container,false);
        return v;
    }
    public void updateUI(DetailItem items){

    }
}
