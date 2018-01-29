package moe.exmagic.tricks.banguminews.Fragments.SubjectDetail;

import android.app.FragmentTransaction;
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

public class FragmentItemDetail extends Fragment {
    private SearchResultItem mBaseItem;
    private DetailItem mDetailItem;
    private DetailItem mWebSpiderDetailItem;            // 从爬虫那里返回的
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item_detail,container,false);
        return v;
    }
    public void updateUI(DetailItem detail){
        if(detail.KVInfo == null)
            mDetailItem = detail;
        else
            mWebSpiderDetailItem = detail;
        if(mDetailItem == null || mWebSpiderDetailItem == null)
            return;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment mTagsFragment = TagsFragment.newInstance();
        Fragment mEpsFragment = FragmentEps.newInstance();
        if(mDetailItem.Eps!= null){
            ((FragmentEps)mEpsFragment).setEps(mDetailItem.Eps);
            ft.add(R.id.item_detail_fragment_container,mEpsFragment,"Eps");
        }
        if(mWebSpiderDetailItem.Tags!= null){
            ((TagsFragment)mTagsFragment).setTags(mWebSpiderDetailItem.Tags);
            ft.add(R.id.item_detail_fragment_container,mTagsFragment,"Tags");
        }
        Fragment summaryFragment = SummaryFragment.newInstance();
        Fragment kVSummaryFragment = KVSummaryFragment.newInstance();
        ((SummaryFragment)summaryFragment).setSummary(mDetailItem.Summary);
        if(mWebSpiderDetailItem.KVInfo != null) {
            ((KVSummaryFragment) kVSummaryFragment).setSummary(mWebSpiderDetailItem.KVInfo);
        }else{
            // TODO: StaffInfo
        }
        ft.add(R.id.item_detail_fragment_container,summaryFragment);
        ft.add(R.id.item_detail_fragment_container,kVSummaryFragment);
        Fragment CharactersFragment = CharactersCompactItem.newInstance(mDetailItem.CharactersList,mDetailItem.BaseItem.ItemId);
        if(CharactersFragment != null && mDetailItem.CharactersList != null){
            ft.add(R.id.item_detail_fragment_container,CharactersFragment,"Characters");
        }
        ft.commit();
    }
    public static FragmentItemDetail newInstance(){
        FragmentItemDetail fragment = new FragmentItemDetail();
        return fragment;
    }

}
