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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import moe.exmagic.tricks.banguminews.Utils.BgmDataType.*;
import moe.exmagic.tricks.banguminews.WebSpider.WebSpider;

/**
 * Created by tricks on 17-2-11.
 */

public class ActivityCommentList extends FragmentActivity implements SwipeRefreshLayout.OnRefreshListener{
    private ArrayList<CommentItem> mCommentItems = new ArrayList<>();
    public static String COMMENT_EXTRA_SUBJECT_ID = "comment_extra_subject_id";
    private int mItemID;
    private ItemsAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private int mCurrentPage;
    private ActivityCommentList mSelf = this;
    private SwipeRefreshLayout mRefreshLayout;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_comment);
        mItemID = getIntent().getExtras().getInt(COMMENT_EXTRA_SUBJECT_ID);
        mRecyclerView = (RecyclerView) findViewById(R.id.more_comment_list_view);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.more_comment_refresh_layout);
        Toolbar toolbar = (Toolbar)findViewById(R.id.more_comment_toolbar);
        toolbar.setTitle("更多短评");
        setActionBar(toolbar);
        mAdapter = new ItemsAdapter(mCommentItems);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setOnScrollListener(new scrollListener());

        mCurrentPage = 0;
        WebSpider.get(mSelf).GetCommentList("http://bgm.tv/subject/" + mItemID + "/comments?page=" + (mCurrentPage+1),mSelf);
        mRefreshLayout.setRefreshing(true);
        mRefreshLayout.setOnRefreshListener(this);
    }
    public void updateUI(ArrayList<CommentItem> result){
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
        mCommentItems.addAll(result);
        if(mCurrentPage == 0){
            mCommentItems = result;
            mAdapter = new ItemsAdapter(mCommentItems);
            mRecyclerView.setAdapter(mAdapter);
            mCurrentPage = 1;
            return;
        }
        int lastOffset = 0;
        int lastPosition = 0;
        View topView = mLayoutManager.getChildAt(0);          //获取可视的第一个view
        lastOffset = topView.getTop();                                   //获取与该view的顶部的偏移量
        lastPosition = mLayoutManager.getPosition(topView);  //得到该View的数组位置
        mAdapter.updateAdapter(mCommentItems);;
        mRecyclerView.setAdapter(mAdapter);
        ((LinearLayoutManager)mLayoutManager).scrollToPositionWithOffset(lastPosition, lastOffset);

        mCurrentPage += 1;
    }

    @Override
    public void onRefresh() {
        mCurrentPage = 0;
        WebSpider.get(mSelf).GetCommentList("http://bgm.tv/subject/" + mItemID + "/comments?page=" +  (mCurrentPage+1),mSelf);
    }

    class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemsHolder> {
        private ArrayList<CommentItem> mComments;
        public void updateAdapter(ArrayList<CommentItem> items){
            mComments = items;
        }
        public ItemsAdapter(ArrayList<CommentItem> blogs){
            mComments = blogs;
        }
        @Override
        public ItemsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemsHolder(LayoutInflater.from(
                    ActivityCommentList.this).inflate(R.layout.item_comment_compact, parent,
                    false));
        }
        @Override
        public void onBindViewHolder(ItemsHolder holder, int position) {
            CommentItem item = mComments.get(position);
            holder.pUserNickname.setText(item.User.UserNickname);
            holder.pSubmitDatetime.setText(item.SubmitDatetime);
            holder.pComment.setText(item.Comment);
            Glide.with(getApplicationContext()).load(item.User.UserHeaderUrl).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ic_def_banner)
                    .placeholder(R.drawable.ic_def_loading)
                    .centerCrop()
                    .into(holder.pHeader);
        }
        @Override
        public int getItemCount() {
            return mComments.size();
        }
        class ItemsHolder extends RecyclerView.ViewHolder {
            public TextView pUserNickname;
            public TextView pSubmitDatetime;
            public TextView pComment;
            public ImageView pHeader;
            public ItemsHolder(View view) {
                super(view);
                pUserNickname = (TextView) view.findViewById(R.id.comment_nickname);
                pSubmitDatetime = (TextView) view.findViewById(R.id.comment_submit_date);
                pComment = (TextView)view.findViewById(R.id.comment_text);
                pHeader = (ImageView)view.findViewById(R.id.comment_user_header);
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
                WebSpider.get(mSelf).GetCommentList("http://bgm.tv/subject/" + mItemID + "/comments?page=" +  (mCurrentPage+1),mSelf);
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
