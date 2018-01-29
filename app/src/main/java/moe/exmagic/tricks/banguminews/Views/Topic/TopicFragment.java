package moe.exmagic.tricks.banguminews.Views.Topic;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType;

/**
 * Created by Stern on 2018/1/22.
 */

public class TopicFragment extends Fragment {
    private View mRootView;
    private ArrayList<BgmDataType.TopicCompactItem> mTopics;
    private String                                  mTitle;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(mRootView != null)
            return mRootView;
        View v = inflater.inflate(R.layout.fragment_home_topic,container,false);
        if(mTopics != null)
            updateUI();
        mRootView = v;
        return v;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setTopics(ArrayList<BgmDataType.TopicCompactItem> mTopics) {
        this.mTopics = mTopics;
    }

    public void updateUI(){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for(BgmDataType.TopicCompactItem item : mTopics){
            if(item.DepartmentType.equals("group"))
                fragmentTransaction.add(R.id.group_topic_container,ItemTopicCompact.newInstance(item));
            else
                fragmentTransaction.add(R.id.subject_topic_container,ItemTopicCompact.newInstance(item));
        }
        fragmentTransaction.commit();
    }

    public static TopicFragment newInstance(ArrayList<BgmDataType.TopicCompactItem> data) {
        TopicFragment fragment = new TopicFragment();
        fragment.setTopics(data);
        return fragment;
    }
}
