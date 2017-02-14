package moe.exmagic.tricks.banguminews.Fragments;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType.*;

import moe.exmagic.tricks.banguminews.Views.CharactersCompactItem;
import moe.exmagic.tricks.banguminews.Views.EpsFragment;
import moe.exmagic.tricks.banguminews.Views.KVSummaryFragment;
import moe.exmagic.tricks.banguminews.Views.SummaryFragment;
import moe.exmagic.tricks.banguminews.Views.TagsFragment;

/**
 * Created by tricks on 17-2-8.
 */

public class ItemDetailFragment extends Fragment {
    private SearchResultItem mBaseItem;
    private DetailItem mDetailItem;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item_detail,container,false);
        return v;
    }
    public void updateUI(DetailItem detail){
        mDetailItem = detail;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment mTagsFragment = TagsFragment.newInstance();
        Fragment mEpsFragment = EpsFragment.newInstance();
        if(mDetailItem.Eps!= null){
            ((EpsFragment)mEpsFragment).setEps(mDetailItem.Eps);
            ft.add(R.id.item_detail_fragment_container,mEpsFragment,"Eps");
        }
        if(mDetailItem.Tags!= null){
            ((TagsFragment)mTagsFragment).setTags(mDetailItem.Tags);
            ft.add(R.id.item_detail_fragment_container,mTagsFragment,"Tags");
        }
        Fragment summaryFragment = SummaryFragment.newInstance();
        Fragment kVSummaryFragment = KVSummaryFragment.newInstance();
        ((SummaryFragment)summaryFragment).setSummary(mDetailItem.Summary);
        ((KVSummaryFragment)kVSummaryFragment).setSummary(mDetailItem.KVInfo);
        ft.add(R.id.item_detail_fragment_container,summaryFragment);
        ft.add(R.id.item_detail_fragment_container,kVSummaryFragment);
        Fragment CharactersFragment = CharactersCompactItem.newInstance(mDetailItem.CharactersList,mDetailItem.BaseItem.ItemId);
        if(CharactersFragment != null && mDetailItem.CharactersList != null){
            ft.add(R.id.item_detail_fragment_container,CharactersFragment,"Characters");
        }
        ft.commit();
    }
    public static ItemDetailFragment newInstance(){
        ItemDetailFragment fragment = new ItemDetailFragment();
        return fragment;
    }

}
