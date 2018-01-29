package moe.exmagic.tricks.banguminews.Fragments.SubjectDetail;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import moe.exmagic.tricks.banguminews.R;

/**
 * Created by Stern on 2018/1/27.
 */

public class ItemTagMain extends Fragment {
    private String mTag;
    private TextView mTagView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_main_tag, container, false);
        mTagView = (TextView)v.findViewById(R.id.detail_main_tag);
        updateUI();
        return v;
    }

    public void updateUI(){
        if(mTag != null){
            mTagView.setText(mTag);
        }
    }

    public static ItemTagMain newInstance(String tag) {
        ItemTagMain fragment = new ItemTagMain();
        fragment.mTag  = tag;
        return fragment;
    }
}
