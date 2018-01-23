package moe.exmagic.tricks.banguminews.Views;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import moe.exmagic.tricks.banguminews.ActivityBlogView;
import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType.*;

/**
 * Created by tricks on 17-2-9.
 */

public class ItemBlogCompact extends Fragment {
    private BlogItem mBlog;
    public void setBlog(BlogItem item){
        mBlog = item;
    }
    public static ItemBlogCompact newInstance(BlogItem item){
        ItemBlogCompact fragment = new ItemBlogCompact();
        fragment.setBlog(item);
        return fragment;
    }
    private TextView mBlogTitle;
    private TextView mBlogSubmitInfo;
    private TextView mBlogSummary;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_blog_compact,container,false);
        mBlogTitle = (TextView) v.findViewById(R.id.blog_compact_title);
        mBlogSubmitInfo = (TextView) v.findViewById(R.id.blog_compact_submit_info);
        mBlogSummary = (TextView) v.findViewById(R.id.blog_compact_preview);
        if(mBlog != null) {
            mBlogTitle.setText(mBlog.Title);
            mBlogSummary.setText(mBlog.BlogPreview);
            mBlogSubmitInfo.setText("by " + mBlog.Submitter.UserNickname + " " + mBlog.SubmitDatetime + mBlog.BlogCommentNumber);
        }
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityBlogView.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ActivityBlogView.EXTRA_BLOG_ITEM,mBlog);
                intent.putExtra(ActivityBlogView.EXTRA_BUNDLE_BLOG_ITEM,bundle);
                startActivity(intent);
            }
        });
        return v;
    }
}
