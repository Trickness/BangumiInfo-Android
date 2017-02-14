package moe.exmagic.tricks.banguminews.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import moe.exmagic.tricks.banguminews.ActivityItemDetail;
import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType.*;
import moe.exmagic.tricks.banguminews.WebSpider.WebSpider;
import moe.exmagic.tricks.banguminews.ActivitySearch;

/**
 * Created by SternWZhang on 17-2-8.
 * SearchFragment is an activity
 */

public class FragmentSearchResult extends Fragment {
    public String pKeyWord = "";
    public String pSearchType = WebSpider.SEARCH_ALL;
    private RecyclerView mItemListView;
    public SwipeRefreshLayout pSwipeRefreshLayout;
    private Fragment mRootSearchFragment;
    private View     mRootView;
    private ItemsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ActivitySearch mParent;
    private boolean mFirst = false;
    public  boolean isSearching = false;
    SearchResult mSearchResult;

    private class scrollListener extends RecyclerView.OnScrollListener {
        private int lastVisibleItem;
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if(mAdapter == null)
                return;
            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && lastVisibleItem + 1 == mAdapter.getItemCount()) {
                pSwipeRefreshLayout.setRefreshing(true);
                WebSpider.get(getActivity()).Search(mSearchResult.keyWord,mSearchResult.searchType,mSearchResult.currentPage+1, (FragmentSearchResult) mRootSearchFragment);
            }
        }
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisibleItem = ((LinearLayoutManager)mLayoutManager).findLastCompletelyVisibleItemPosition();
        }
    }


    public void updateUI(SearchResult result) {
        isSearching = false;
        pSwipeRefreshLayout.setRefreshing(false);
        if(result == null) {
            Toast toast = Toast.makeText(getActivity(),"载入失败！",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        mSearchResult = result;
        mAdapter = new ItemsAdapter(result);
        mItemListView.setAdapter(mAdapter);
        mParent.pSearchResults.put(pSearchType,result);
    }

    public void appendUI(SearchResult result) {
        pSwipeRefreshLayout.setRefreshing(false);
        if(result == null || result.result.size() == 0) {
            Toast toast = Toast.makeText(getActivity(),"没有更多内容",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        View topView = mLayoutManager.getChildAt(0);          //获取可视的第一个view
        int lastOffset = topView.getTop();                                   //获取与该view的顶部的偏移量
        int lastPosition = mLayoutManager.getPosition(topView);  //得到该View的数组位置

        result.result.addAll(0,mSearchResult.result);
        mParent.pSearchResults.put(pSearchType,result);
        this.mAdapter.updateListData(result);
        mItemListView.setAdapter(mAdapter);
        ((LinearLayoutManager)mLayoutManager).scrollToPositionWithOffset(lastPosition, lastOffset);

        mSearchResult = result;
    }

    public void setParent(ActivitySearch parent){
        mParent = parent;
    }

    public static FragmentSearchResult newInstance(String keyWord, String searchType, ActivitySearch parent) {
        FragmentSearchResult fragment = new FragmentSearchResult();
        fragment.pKeyWord = keyWord;
        fragment.pSearchType = searchType;
        fragment.setParent(parent);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_searchresult, container, false);
        mRootSearchFragment = this;
        mItemListView = (RecyclerView) v.findViewById(R.id.search_result_recycler_list);
        pSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.search_result_swipe_refresh);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mItemListView.setLayoutManager(mLayoutManager);
        mItemListView.setOnScrollListener(new scrollListener());
        SearchResult r = mParent.pSearchResults.get(pSearchType);
        mRootView = v;
        if(!isSearching){
            if(mParent.pSearchResults.get(pSearchType) != null){
                mAdapter = new ItemsAdapter(mParent.pSearchResults.get(pSearchType));
                mItemListView.setAdapter(mAdapter);
                pSwipeRefreshLayout.setRefreshing(false);
            }else if(mFirst){
                doSearch();
            }
        }

        return v;
    }

    public void doSearch(){
        isSearching = true;
        WebSpider.get(getActivity()).Search(pKeyWord,pSearchType,1,this);
        pSwipeRefreshLayout.setRefreshing(true);
    }

    public void setFirst(){
        mFirst = true;
    }

    private class ItemsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView pTitleView;
        private TextView pOriginalTitleView;
        private TextView pRankView;
        private ImageView pCoverView;
        private TextView pItemInfoView;
        public SearchResultItem pBaseItem;
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
            Intent intent = new Intent(getContext(), ActivityItemDetail.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ActivityItemDetail.EXTRA_ITEM_DETAIL_BASEITEM,pBaseItem);
            bundle.putSerializable(ActivityItemDetail.EXTRA_WEBSPIDER_COOKIES,WebSpider.get(getContext()).Cookies);
            intent.putExtra(ActivityItemDetail.EXTRA_ITEM_DETAIL,bundle);
            startActivity(intent);
        }
    }
    private class ItemsAdapter extends RecyclerView.Adapter<ItemsHolder> {
        private SearchResult mResults;

        private ItemsAdapter(SearchResult results) {
            mResults = results;
        }
        public void updateListData(SearchResult result){
            this.mResults = result;
        }

        @Override
        public ItemsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.item_search_result, parent, false);
            return new ItemsHolder(view);
        }

        @Override
        public int getItemCount() {
            if(mResults == null)
                return 0;
            return mResults.result.size();
        }

        @Override
        public void onBindViewHolder(ItemsHolder holder, int position) {
            SearchResultItem item = mResults.result.get(position);
            holder.pTitleView.setText(item.Title);
            holder.pOriginalTitleView.setText(item.OriginalTitle);
            holder.pItemInfoView.setText("（" + WebSpider.getStrType(item.ItemType) + "）");
            String tmpRankInfo = "";
            if (item.Rank != 0) {
                tmpRankInfo = tmpRankInfo + "RANK:" + item.Rank;
            }
            if (item.Score != 0) {
                tmpRankInfo = tmpRankInfo + "  SCORE:" + item.Score;
            }
            if (tmpRankInfo.equals("")) {
                tmpRankInfo = "No Rank Information";
            }
            holder.pRankView.setText(tmpRankInfo);
            if (item.CoverUrl.equals("")) {
                return;
            } else {
                Glide.with(getContext()).load(item.CoverUrl).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.ic_def_banner)
                        .placeholder(R.drawable.ic_def_loading)
                        .centerCrop()
                        .into(holder.pCoverView);
            }
            holder.pBaseItem = item;
        }
    }
}
