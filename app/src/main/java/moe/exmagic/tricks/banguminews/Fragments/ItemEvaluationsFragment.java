package moe.exmagic.tricks.banguminews.Fragments;

import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType.*;
import moe.exmagic.tricks.banguminews.Views.BlogsFragment;
import moe.exmagic.tricks.banguminews.Views.CommentsCompactFragment;
import moe.exmagic.tricks.banguminews.Views.ItemVotesChartFragment;

/**
 * Created by tricks on 17-2-8.
 * Show comments
 */

public class ItemEvaluationsFragment extends Fragment {
    public static ItemEvaluationsFragment newInstance(){
        return new ItemEvaluationsFragment();
    }
    private DetailItem mDetailItem;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_evaluations,container,false);
        if(mDetailItem != null)
            this.updateUI(mDetailItem);
        return v;
    }
    public void updateUI(DetailItem items){
        mDetailItem = items;
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .add(R.id.evaluation_container, ItemVotesChartFragment.newInstance(items))
                .commit();
        if(mDetailItem.Blogs != null && mDetailItem.Blogs.size() > 0) {
            fm.beginTransaction()
                    .add(R.id.evaluation_container, BlogsFragment.newInstance(items.Blogs,mDetailItem.BaseItem.ItemId))
                    .commit();
        }
        if(mDetailItem.Comments != null && mDetailItem.Comments.size() > 0) {
            fm.beginTransaction().add(R.id.evaluation_container, CommentsCompactFragment.newInstance(items.Comments,mDetailItem.BaseItem.ItemId))
                    .commit();
        }
    }
}
