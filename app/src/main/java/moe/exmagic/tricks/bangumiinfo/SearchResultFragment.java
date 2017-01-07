package moe.exmagic.tricks.bangumiinfo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import moe.exmagic.tricks.bangumiinfo.utils.DataType;
import moe.exmagic.tricks.bangumiinfo.utils.WebSpider;

/**
 * Created by tricks on 17-1-5.
 */

public class SearchResultFragment extends Fragment{
    private WebSpider.SearchResult mResult;
    private RecyclerView mItemListView;
    private ItemsAdapter mAdapter;
    private String mSearchKeyWord = "";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.LayoutManager mLayoutManager;
    private scrollListener mScrollListener;
    private String mSearchType;
    private String mTitle = "";
    private View mFragmentView;

    private static String KEY_SEARCH_RESULT = "moe.exmagic.searchResult";

    private Bundle savedState;
    private class storeState{
        public String   searchType;
        public int      maxPage;
        public int      currentPage;
        public String   keyWord;
        ArrayList<DataType.SearchResultItem> results;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mFragmentView != null){
            ((ViewGroup) mFragmentView.getParent()).removeView(mFragmentView);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        if(mResult == null)
            return ;
        Gson g = new Gson();

        storeState state = new storeState();
        state.searchType = mSearchType;
        state.maxPage = mResult.maxpage;
        state.currentPage = mResult.currentpage;
        state.keyWord = mSearchKeyWord;
        state.results = mResult.result;

        String s = g.toJson(state);
        savedInstanceState.putString(KEY_SEARCH_RESULT,s);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        restoreState(savedInstanceState);
    }

    protected boolean restoreState(Bundle savedInstanceState){
        if(savedInstanceState == null)
            return false;
        String s =savedInstanceState.getString(KEY_SEARCH_RESULT);
        if(s != null) {
            storeState state = new Gson().fromJson(s,storeState.class);
            this.mResult = new WebSpider.SearchResult();
            this.mResult.result = state.results;
            this.mSearchKeyWord = state.searchType;
            this.mResult.maxpage = state.maxPage;
            this.mResult.currentpage = state.currentPage;
            this.mSearchType = state.searchType;
            this.updateUI(this.mResult);
        }
        return true;
    }
    public String getKeyWord(){
        return mSearchKeyWord;
    }
    public SearchResultFragment setSearchType(String searchType){
        mSearchType = searchType;
        if(searchType.equals("all")) {
            mTitle = "综合";
        } else if(searchType.equals("person")) {
            mTitle = "人物";
        } else {
            mTitle = WebSpider.getStrType(Integer.parseInt(searchType));
        }
        return this;
    }
    public String getTitle(){
        return mTitle;
    }
    public void updateUI(WebSpider.SearchResult result){
        mSwipeRefreshLayout.setRefreshing(false);
        if(result == null) {
            Toast toast = Toast.makeText(getActivity(),"没有更多内容",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        mResult = result;
        mAdapter = new ItemsAdapter(result);
        mItemListView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }
    public void appendUI(WebSpider.SearchResult result){
        mSwipeRefreshLayout.setRefreshing(false);
        if(result == null) {
            Toast toast = Toast.makeText(getActivity(),"没有更多内容",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        View topView = mLayoutManager.getChildAt(0);          //获取可视的第一个view
        int lastOffset = topView.getTop();                                   //获取与该view的顶部的偏移量
        int lastPosition = mLayoutManager.getPosition(topView);  //得到该View的数组位置

        result.result.addAll(0,mAdapter.getListData().result);
        this.mAdapter.updateListData(result);
        mItemListView.setAdapter(mAdapter);

        ((LinearLayoutManager)mLayoutManager).scrollToPositionWithOffset(lastPosition, lastOffset);

        mResult = result;
    }
    public void search(String keyWord){
        this.mSearchKeyWord = keyWord;
        WebSpider spider = WebSpider.get(getActivity());
        spider.searchItem(mSearchKeyWord,mSearchType,1,this);
        ((SwipeRefreshLayout)mFragmentView.findViewById(R.id.swipeRefreshLayout)).setRefreshing(true);
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    private class scrollListener extends RecyclerView.OnScrollListener {
        private int lastVisibleItem;
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if(mAdapter == null)
                return;
            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && lastVisibleItem + 1 == mAdapter.getItemCount()) {
                mSwipeRefreshLayout.setRefreshing(true);
                mAdapter.getListData().getNext();   // It will refresh ui
            }
        }
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisibleItem = ((LinearLayoutManager)mLayoutManager).findLastCompletelyVisibleItemPosition();
        }
    }
    private class FetchBitmap extends AsyncTask<Void,Void,Bitmap>{
        private ImageView mV;
        private String    mStrUrl;
        private DataType.SearchResultItem mItem;
        FetchBitmap(ImageView view, String strUrl, DataType.SearchResultItem item){
            mV = view;
            mStrUrl = "http:" + strUrl;
            mItem = item;
        }
        @Override
        protected  Bitmap doInBackground(Void... v){
            URL url;
            try {
                url = new URL(mStrUrl);
            }catch (MalformedURLException e){
                Log.e("Bitmap",mStrUrl + " AND " + e.toString());
                return null;
            }
            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() == 200) {
                    InputStream inputStream = conn.getInputStream();
                    return BitmapFactory.decodeStream(inputStream);
                }
            }catch(IOException e){
                return null;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap){
            mItem.isLoading = false;
            if(bitmap == null){
                return ;
            }
            mV.setImageBitmap(bitmap);
            mItem.Cover = bitmap;
        }
    }
    private class ItemsHolder extends RecyclerView.ViewHolder{
        public TextView pTitleView;
        public TextView pOriginalTitleView;
        public TextView pRankView;
        public ImageView pCoverView;
        public TextView pItemTypeView;
        public ItemsHolder(View itemView){
            super(itemView);
            pTitleView = (TextView) itemView.findViewById(R.id.textViewTitle);
            pOriginalTitleView = (TextView) itemView.findViewById(R.id.textViewOriginalTitle);
            pRankView = (TextView) itemView.findViewById(R.id.textRankInfoView);
            pCoverView = (ImageView) itemView.findViewById(R.id.coverView);
            pItemTypeView = (TextView) itemView.findViewById(R.id.textItemTypeView);
            itemView.setClickable(true);
            itemView.setBackgroundResource(R.drawable.rect_gray);
        }
    }
    private class ItemsAdapter extends RecyclerView.Adapter<ItemsHolder> {
        private WebSpider.SearchResult mResults;
        public WebSpider.SearchResult getListData(){
            return mResults;
        }
        public ItemsAdapter(WebSpider.SearchResult results){
            mResults = results;
        }
        @Override
        public ItemsHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.search_result_item,parent,false);
            return new ItemsHolder(view);
        }
        @Override
        public int getItemCount(){
            return mResults.result.size();
        }
        @Override
        public void onBindViewHolder(ItemsHolder holder, int position){
            holder.pCoverView.setImageBitmap(null);
            DataType.SearchResultItem item = mResults.result.get(position);
            holder.pTitleView.setText(item.Title);
            holder.pOriginalTitleView.setText(item.OriginalTitle);
            holder.pItemTypeView.setText("（" + WebSpider.getStrType(item.ItemType) + "）");
            String tmpRankInfo = "";
            if(item.Rank != 0){
                tmpRankInfo = tmpRankInfo + "RANK:" + item.Rank;
            }
            if(item.Score != 0) {
                tmpRankInfo = tmpRankInfo + "  SCORE:" + item.Score;
            }
            if(tmpRankInfo.equals("")) {
                tmpRankInfo = "No Rank Information";
            }
            holder.pRankView.setText(tmpRankInfo);
            if(item.Cover != null){
                holder.pCoverView.setImageBitmap(item.Cover);
            }else if(item.CoverUrl.equals("")){
                return;
            } else {
                if (item.isLoading)
                    return;
                item.isLoading = true;
                new FetchBitmap(holder.pCoverView, item.CoverUrl,item).execute();
            }
        }

        public void updateListData(WebSpider.SearchResult result){
            this.mResults = result;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.d("BangumiInfo","Created:"+mSearchType);
        if(mFragmentView != null)
            return mFragmentView;
        View v = inflater.inflate(R.layout.fragment_search_result_list,container,false);
        mItemListView = (RecyclerView) v.findViewById(R.id.placeholder_rv_recycler);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mItemListView.setLayoutManager(mLayoutManager);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        final Fragment tmp = this;
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!mSearchKeyWord.equals(""))
                    WebSpider.get(getActivity()).searchItem(mSearchKeyWord,mSearchType,1,tmp);
                else
                    mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mScrollListener = new scrollListener();
        mItemListView.setOnScrollListener(mScrollListener);
        mFragmentView = v;
        return v;
    }
}
