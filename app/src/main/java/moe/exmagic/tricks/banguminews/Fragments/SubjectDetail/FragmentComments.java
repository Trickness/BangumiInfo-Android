package moe.exmagic.tricks.banguminews.Fragments.SubjectDetail;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
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
import com.idlestar.ratingstar.RatingStarView;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType.*;
import moe.exmagic.tricks.banguminews.WebSpider.OnWebspiderReturnListener;
import moe.exmagic.tricks.banguminews.WebSpider.WebSpider;

/**
 * Created by tricks on 17-2-8.
 * Show comments
 */

public class FragmentComments extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    public static FragmentComments newInstance(String subjectId){
        FragmentComments fragment = new FragmentComments();
        fragment.mItemID = subjectId;
        return fragment;
    }
    private DetailItem mDetailItem;

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_evaluation,container,false);
        if(mDetailItem != null)
            this.updateUI(mDetailItem);
        return v;
    }*/
    /*public void updateUI(DetailItem items){
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
    }*/
    private OnWebspiderReturnListener mListener = new OnWebspiderReturnListener() {
        @Override
        public void onSuccess(Object object) {
            updateUI(((ArrayList<CommentItem>)object));
        }

        @Override
        public void onFailed(Object object) {

        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_comments_compact_container, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.more_comment_list_view);
        mRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.more_comment_refresh_layout);
        mAdapter = new ItemsAdapter(mCommentItems);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setOnScrollListener(new scrollListener());

        mCurrentPage = 0;
        WebSpider.get(getActivity()).GetCommentList("http://bgm.tv/subject/" + mItemID + "/comments?page=" + (mCurrentPage+1),mListener);
        mRefreshLayout.setEnabled(false);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setRefreshing(true);
        return v;
    }

    private ArrayList<CommentItem> mCommentItems = new ArrayList<>();
    public static String COMMENT_EXTRA_SUBJECT_ID = "comment_extra_subject_id";
    private String  mItemID;
    private ItemsAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private int mCurrentPage;
    private SwipeRefreshLayout mRefreshLayout;
    private static Transformation mHeaderTransform = new RoundedTransformationBuilder()
            .cornerRadiusDp(8)
            .oval(false)
            .build();


    public void updateUI(ArrayList<CommentItem> result){
        mRefreshLayout.setRefreshing(false);
        if(result == null) {
            Toast toast = Toast.makeText(getActivity(),"载入失败！", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        if(result.size() == 0){
            Toast toast = Toast.makeText(getActivity(),"没有更多的评论", Toast.LENGTH_LONG);
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
        WebSpider.get(getActivity()).GetCommentList("http://bgm.tv/subject/" + mItemID + "/comments?page=" + (mCurrentPage + 1), mListener);
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
        public ItemsAdapter.ItemsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemsAdapter.ItemsHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_comment_compact, parent,
                    false));
        }
        @Override
        public void onBindViewHolder(ItemsAdapter.ItemsHolder holder, int position) {
            CommentItem item = mComments.get(position);
            holder.pUserNickname.setText(item.User.UserNickname);
            holder.pSubmitDatetime.setText(item.SubmitDatetime);
            holder.pComment.setText(item.Comment);
            Picasso.with(getActivity()).load(item.User.UserHeaderUrl)
                    .transform(mHeaderTransform)
                    .into(holder.pHeader);
            if(item.Score == -1){
                holder.pStarsView.setVisibility(View.INVISIBLE);
            }else {
                holder.pStarsView.setRating((float) item.Score / 2);
            }
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
            public RatingStarView pStarsView;
            public ItemsHolder(View view) {
                super(view);
                pUserNickname = (TextView) view.findViewById(R.id.comment_nickname);
                pSubmitDatetime = (TextView) view.findViewById(R.id.comment_submit_date);
                pComment = (TextView)view.findViewById(R.id.comment_text);
                pHeader = (ImageView)view.findViewById(R.id.comment_user_header);
                pStarsView = (RatingStarView) view.findViewById(R.id.comment_stars_view);
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
                WebSpider.get(getActivity()).GetCommentList("http://bgm.tv/subject/" + mItemID + "/comments?page=" + (mCurrentPage + 1), mListener);
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
