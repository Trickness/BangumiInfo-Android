package moe.exmagic.tricks.banguminews.Fragments.SubjectDetail;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType;

/**
 * Created by Stern on 2018/1/27.
 */

public class FragmentCollectionInfo extends Fragment {
    private View mRootView;
    private BgmDataType.DetailItem mDetailItem;
    private ItemVotesChartFragment mVotesChart;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(mRootView != null)
            return mRootView;
        View v = inflater.inflate(R.layout.item_collection_box, container, false);
        mRootView = v;
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        mVotesChart = ItemVotesChartFragment.newInstance(mDetailItem);
        fragmentTransaction.add(R.id.collection_box_vote_info_container, mVotesChart).commit();
        return v;
    }

    public static FragmentCollectionInfo newInstance(BgmDataType.DetailItem item) {
        FragmentCollectionInfo fragment = new FragmentCollectionInfo();
        fragment.mDetailItem = item;
        return fragment;
    }

    public void updateUI(BgmDataType.DetailItem item){
        mDetailItem = item;
        mVotesChart.setDetail(item);
        mVotesChart.updateUI();
    }
}
