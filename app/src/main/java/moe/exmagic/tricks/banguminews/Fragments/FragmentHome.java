package moe.exmagic.tricks.banguminews.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONArray;

import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.WebSpider.WebSpider;

/**
 * Created by Stern on 2018/1/21.
 */

public class FragmentHome extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        return v;
    }
    public static FragmentHome newInstance(){
        return new FragmentHome();
    }
    public void Refresh(){
        WebSpider.get(getActivity()).GetHomepage(this);
    }

    public void updateUI(JSONArray data){

    }
}
