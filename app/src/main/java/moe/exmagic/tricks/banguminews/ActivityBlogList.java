package moe.exmagic.tricks.banguminews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;

import moe.exmagic.tricks.banguminews.Utils.BgmDataType.*;
import moe.exmagic.tricks.banguminews.WebSpider.WebSpider;

/**
 * Created by tricks on 17-2-11.
 */

public class ActivityBlogList extends FragmentActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ArrayList<BlogItem> mBlogItems = new ArrayList<>();
    public static String BLOG_EXTRA_SUBJECT_ID = "blog_extra_subject_id";
    private int mItemID;
    private ItemsAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private int mCurrentPage;
    private ActivityBlogList mSelf = this;
    private SwipeRefreshLayout mRefreshLayout;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_blog);
        mItemID = getIntent().getExtras().getInt(BLOG_EXTRA_SUBJECT_ID);
        mRecyclerView = (RecyclerView) findViewById(R.id.more_blog_list_view);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.more_blog_refresh_layout);
        Toolbar toolbar = (Toolbar)findViewById(R.id.more_blog_toolbar);
        toolbar.setTitle("更多长评");
        setActionBar(toolbar);
        mAdapter = new ItemsAdapter(mBlogItems);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setOnScrollListener(new scrollListener());

        mCurrentPage = 0;
        WebSpider.get(this).GetBlogList("http://bgm.tv/subject/" + mItemID + "/reviews/" + (mCurrentPage + 1) +".html",this);
        mRefreshLayout.setRefreshing(true);
    }
    public void updateUI(ArrayList<BlogItem> result){
        mRefreshLayout.setRefreshing(false);
        if(result == null) {
            Toast toast = Toast.makeText(this,"载入失败！", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        if(result.size() == 0){
            Toast toast = Toast.makeText(this,"没有更多的评论", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        mBlogItems.addAll(result);
        if(mCurrentPage == 0){
            mBlogItems = result;
            mAdapter = new ItemsAdapter(mBlogItems);
            mRecyclerView.setAdapter(mAdapter);
            mCurrentPage = 1;
            return;
        }
        int lastOffset = 0;
        int lastPosition = 0;
        View topView = mLayoutManager.getChildAt(0);          //获取可视的第一个view
        lastOffset = topView.getTop();                                   //获取与该view的顶部的偏移量
        lastPosition = mLayoutManager.getPosition(topView);  //得到该View的数组位置
        mAdapter.updateAdapter(mBlogItems);;
        mRecyclerView.setAdapter(mAdapter);
        ((LinearLayoutManager)mLayoutManager).scrollToPositionWithOffset(lastPosition, lastOffset);

        mCurrentPage += 1;
    }

    @Override
    public void onRefresh() {
        mCurrentPage = 0;
        WebSpider.get(this).GetBlogList("http://bgm.tv/subject/" + mItemID + "/reviews/" + (mCurrentPage+1) +".html",this);
    }

    class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemsHolder> {
        private ArrayList<BlogItem> mBlogs;
        public void updateAdapter(ArrayList<BlogItem> items){
            mBlogs = items;
        }
        public ItemsAdapter(ArrayList<BlogItem> blogs){
            mBlogs = blogs;
        }
        @Override
        public ItemsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemsHolder(LayoutInflater.from(
                    ActivityBlogList.this).inflate(R.layout.item_blog_compact, parent,
                    false));
        }
        @Override
        public void onBindViewHolder(ItemsHolder holder, int position) {
            BlogItem item = mBlogs.get(position);
            holder.mBlogTitle.setText(item.Title);
            holder.mBlogPreview.setText(item.BlogPreview);
            holder.mSubmitInfo.setText("by " + item.Submitter.UserNickname + " " + item.SubmitDatetime + item.BlogCommentNumber);
            holder.pBlogItem = mBlogs.get(position);
        }
        @Override
        public int getItemCount() {
            return mBlogs.size();
        }
        class ItemsHolder extends RecyclerView.ViewHolder {
            TextView mBlogTitle;
            TextView mSubmitInfo;
            TextView mBlogPreview;
            public BlogItem pBlogItem;
            public ItemsHolder(View view) {
                super(view);
                mBlogTitle = (TextView) view.findViewById(R.id.blog_compact_title);
                mSubmitInfo = (TextView) view.findViewById(R.id.blog_compact_submit_info);
                mBlogPreview = (TextView) view.findViewById(R.id.blog_compact_preview);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(),ActivityBlogView.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(ActivityBlogView.EXTRA_BLOG_ITEM,pBlogItem);
                        intent.putExtra(ActivityBlogView.EXTRA_BUNDLE_BLOG_ITEM,bundle);
                        startActivity(intent);
                    }
                });
            }
        }
    }
    private RecyclerView.LayoutManager mLayoutManager;
    private class scrollListener extends RecyclerView.OnScrollListener {
        private int lastVisibleItem;
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if(mAdapter == null)
                return;
            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && lastVisibleItem + 1 == mAdapter.getItemCount()
                    && !mRefreshLayout.isRefreshing()) {
                WebSpider.get(mSelf).GetBlogList("http://bgm.tv/subject/" + mItemID + "/reviews/" + (mCurrentPage + 1) +".html",mSelf);
                mRefreshLayout.setRefreshing(true);
            }
        }
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisibleItem = ((LinearLayoutManager)mLayoutManager).findLastCompletelyVisibleItemPosition();
        }
    }
}
