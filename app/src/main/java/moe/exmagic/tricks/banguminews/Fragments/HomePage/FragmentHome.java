package moe.exmagic.tricks.banguminews.Fragments.HomePage;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType;
import moe.exmagic.tricks.banguminews.Views.Topic.TopicFragment;
import moe.exmagic.tricks.banguminews.WebSpider.WebSpider;

/**
 * Created by Stern on 2018/1/21.
 */

public class FragmentHome extends Fragment {
    private View mRootView;
    private LinearLayout mContainer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        if(mRootView != null)
            return mRootView;
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mContainer = v.findViewById(R.id.home_topic_container);
        mRootView = v;
        return v;
    }
    public static FragmentHome newInstance(){
        return new FragmentHome();
    }
    public void Refresh(){
        WebSpider.get(getActivity()).GetHomepage(this);
    }

    public void updateTopics(ArrayList<BgmDataType.TopicCompactItem> data){
        if(data == null){
            Log.d("DEBUG","Topics parser return null");
            return;
        }
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(R.id.home_topic_container, TopicFragment.newInstance(data));
        ft.commit();
    }
}
