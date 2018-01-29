package moe.exmagic.tricks.banguminews;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.squareup.picasso.Picasso;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import moe.exmagic.tricks.banguminews.Utils.BgmDataType;
import moe.exmagic.tricks.banguminews.Views.ItemTopicReply;
import moe.exmagic.tricks.banguminews.WebSpider.OnWebspiderReturnListener;
import moe.exmagic.tricks.banguminews.WebSpider.WebSpider;

/**
 * Created by Stern on 2018/1/28.
 */

public class ActivityTopicView extends FragmentActivity {
    public static String KEY_TOPIC_ID   = "key_topic_id";
    public static String KEY_TOPIC_DEPARTMENT = "key_topic_department";
    private String mTopicId;
    private String mTopicDepartment;
    private LinearLayout mRepliesContainer;

    private ImageView mHeaderView;
    private TextView mNicknameView;
    private TextView mDatetimeView;
    private TextView mSignView;
    private HtmlTextView mTextView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mTopicId = bundle.getString(KEY_TOPIC_ID,"");
            mTopicDepartment = bundle.getString(KEY_TOPIC_DEPARTMENT,"");
        }
        if(mTopicDepartment.equals(""))
            this.finish();
        setContentView(R.layout.activity_topic);
        Toolbar toolbar = findViewById(R.id.activity_topic_toolbar);
        toolbar.setTitle("话题-展开");
        setActionBar(toolbar);


        mHeaderView = (ImageView) findViewById(R.id.topic_header_view);
        mNicknameView = (TextView) findViewById(R.id.topic_nickname_view);
        mDatetimeView = (TextView) findViewById(R.id.topic_datetime);
        mSignView   = (TextView) findViewById(R.id.topic_sign_view);
        mTextView = (HtmlTextView) findViewById(R.id.topic_html_view);

        mRepliesContainer = (LinearLayout) findViewById(R.id.activity_topic_replies_container);
        WebSpider.get(getApplicationContext()).GetTopicItem(mTopicDepartment, mTopicId, new OnWebspiderReturnListener() {
            @Override
            public void onSuccess(Object object) {
                BgmDataType.TopicItem item = (BgmDataType.TopicItem)object;
                Picasso.with(getApplicationContext()).load(item.Submitter.UserHeaderUrl)
                        .transform(ItemTopicReply.mHeaderTransform)
                        .into(mHeaderView);
                mNicknameView.setText(item.Submitter.UserNickname);
                mDatetimeView.setText(item.SubmitDate);
                if(item.SubmitterSignature != null)
                    mSignView.setText(item.SubmitterSignature);
                mTextView.setHtml(item.TopicContent, new HtmlHttpImageGetter(mTextView));
                getActionBar().setTitle(item.TopicTitle);

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                for (BgmDataType.TopicReplyItem it : item.Replies){
                    fragmentTransaction.add(R.id.activity_topic_replies_container, ItemTopicReply.newInstance(it));
                }
                fragmentTransaction.commit();
            }
            @Override
            public void onFailed(Object object) {

            }
        });
    }
}
