package moe.exmagic.tricks.banguminews.Fragments.HomePage;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

import moe.exmagic.tricks.banguminews.ActivityItemDetail;

import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType;
import moe.exmagic.tricks.banguminews.Views.TimeLine.ItemTimeLine;
import moe.exmagic.tricks.banguminews.WebSpider.WebSpider;

/**
 * Created by Stern on 2018/1/25.
 */

public class FragmentTimeLine extends Fragment {
    private View mRootView;
    private boolean mWebLoginReady = false;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(mRootView != null)
            return mRootView;
        View v = inflater.inflate(R.layout.fragment_main_timeline, container, false);
        if(mWebLoginReady)
            WebSpider.get(getActivity()).GetTimeLine(this, 1);
        mRootView = v;
        return v;
    }
    public void updateUI(ArrayList<BgmDataType.TimeLineCommon> data){
        if(data == null){
            Log.d("DEBUG","NULL!!");
            return;
        }
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        BgmDataType.UserItem userItem = null;
        ItemTimeLine fragment = null;
        for(BgmDataType.TimeLineCommon item : data){
            if(userItem == null || fragment == null){
                userItem = item.Submitter;
                fragment = ItemTimeLine.newInstance(item);
            }else if(userItem == item.Submitter){
                fragment.addTimeLine(item);
            }else{
                fragmentTransaction.add(R.id.main_time_line_container, fragment);
                userItem = item.Submitter;
                fragment = ItemTimeLine.newInstance(item);
            }
        }
        fragmentTransaction.add(R.id.main_time_line_container,fragment);
        fragmentTransaction.commit();
    }

    public static FragmentTimeLine newInstance() {
        FragmentTimeLine fragment = new FragmentTimeLine();
        return fragment;
    }
    public void startRefresh(){
        mWebLoginReady = true;
        if(mRootView != null)
            WebSpider.get(getActivity()).GetTimeLine(this, 1);
    }

    private class ItemsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView pTitleView;
        private TextView pOriginalTitleView;
        private TextView pRankView;
        private ImageView pCoverView;
        private TextView pItemInfoView;
        public BgmDataType.SearchResultItem pBaseItem;
        private ItemsHolder(View itemView){
            super(itemView);
            pTitleView = (TextView) itemView.findViewById(R.id.search_result_item_title);
            pOriginalTitleView = (TextView) itemView.findViewById(R.id.search_result_item_original_title);
            pRankView = (TextView) itemView.findViewById(R.id.search_result_item_rank);
            pCoverView = (ImageView) itemView.findViewById(R.id.search_result_item_image_view);
            pItemInfoView = (TextView) itemView.findViewById(R.id.search_result_item_info);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ActivityItemDetail.class);
            Bundle bundle = new Bundle();
            // TODO: Fix this cookies reference
//            bundle.putSerializable(ActivityItemDetail.EXTRA_WEBSPIDER_COOKIES,WebSpider.get(getContext()).WebCookies);
            intent.putExtra(ActivityItemDetail.EXTRA_DETAIL_ITEM_ID,String.valueOf(pBaseItem.ItemId));
            startActivity(intent);
        }
    }
}
