package moe.exmagic.tricks.banguminews;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import moe.exmagic.tricks.banguminews.Utils.BgmDataType.*;
import moe.exmagic.tricks.banguminews.WebSpider.WebSpider;

/**
 * Created by tricks on 17-2-11.
 */

public class ActivityBlogView extends FragmentActivity {
    public static String EXTRA_BUNDLE_BLOG_ITEM = "bundle_blog_item";
    public static String EXTRA_BLOG_ITEM = "blog_item";

    private BlogItem mBlogItem;
    private TextView mBlogTextView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_item);

        mBlogItem = (BlogItem) getIntent().getBundleExtra(EXTRA_BUNDLE_BLOG_ITEM).getSerializable(EXTRA_BLOG_ITEM);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.blog_view_toolbar);
        toolbar.setTitle(mBlogItem.Title);
        setActionBar(toolbar);

        ((TextView)findViewById(R.id.blog_view_title)).setText(mBlogItem.Title);
        ((TextView)findViewById(R.id.blog_view_submit_datetime)).setText(mBlogItem.SubmitDatetime);
        ((TextView)findViewById(R.id.blog_view_submitter)).setText(mBlogItem.Submitter.UserNickname);
        Glide.with(this).load(mBlogItem.Submitter.UserHeaderUrl).diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_def_banner)
                .placeholder(R.drawable.ic_def_loading)
                .into((ImageView)findViewById(R.id.blog_view_header));
        mBlogTextView = (TextView)findViewById(R.id.blog_view_blog_text);
        WebSpider.get(this).GetBlogItem("http://bgm.tv/blog/" + mBlogItem.BlogID,this);
    }

    public void updateUI(BlogItem item){
        if(item == null){
            Toast toast = Toast.makeText(this,"加载失败", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        mBlogItem = item;
        mBlogTextView.setText(mBlogItem.BlogText);
    }
}
