package moe.exmagic.tricks.banguminews.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import moe.exmagic.tricks.banguminews.R;

/**
 * Created by Stern on 2018/1/21.
 */

public class FragmentDiscussion extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_discussion, container, false);
        return v;
    }
    public static FragmentDiscussion newInstance(){
        return new FragmentDiscussion();
    }
}
