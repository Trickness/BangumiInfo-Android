package moe.exmagic.tricks.banguminews.Views;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;



import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType;
import moe.exmagic.tricks.banguminews.htmltextview.HtmlHttpImageGetter;
import moe.exmagic.tricks.banguminews.htmltextview.HtmlTextView;

/**
 * Created by Stern on 2018/1/28.
 */

public class ItemTopicReply extends Fragment {
    BgmDataType.TopicReplyItem mItemData;

    //Views
    private ImageView       mHeaderView;
    private TextView        mNicknameView;
    private TextView        mDateTimeView;
    private TextView        mSignView;
    private HtmlTextView mReplyView;
    private LinearLayout    mSubreplyContainer;
    public static Transformation mHeaderTransform = new RoundedTransformationBuilder()
            .cornerRadiusDp(2)
            .oval(false)
            .build();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_topic_reply, container, false);
        mHeaderView   = (ImageView) v.findViewById(R.id.item_topic_reply_header_view);
        mNicknameView = (TextView) v.findViewById(R.id.item_topic_reply_nickname_view);
        mDateTimeView = (TextView) v.findViewById(R.id.item_topic_reply_datetime);
        mSignView     = (TextView) v.findViewById(R.id.item_topic_reply_sign_view);
        mReplyView    = (HtmlTextView) v.findViewById(R.id.item_topic_reply_html_view);
        mSubreplyContainer = (LinearLayout)v.findViewById(R.id.item_topic_reply_subreply_container);
        updateUI();
        return v;
    }
    public void setData(BgmDataType.TopicReplyItem item){
        mItemData = item;
    }
    public void updateUI(){
        if(mItemData == null)
            return;
        mNicknameView.setText(mItemData.Submitter.UserNickname);
        mDateTimeView.setText(mItemData.ReplyDate);
        if(mItemData.SubmitterSignature != null)
            mSignView.setText(mItemData.SubmitterSignature);
        mReplyView.setHtml(mItemData.ReplyContent, new HtmlHttpImageGetter(mReplyView));
        Picasso.with(getActivity()).load(mItemData.Submitter.UserHeaderUrl)
                .transform(mHeaderTransform)
                .into(mHeaderView);

        if(mItemData.SubReplies != null){
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            for(BgmDataType.TopicReplyItem item : mItemData.SubReplies){
                fragmentTransaction.add(R.id.item_topic_reply_subreply_container, ItemTopicSubReply.newInstance(item));
            }
            fragmentTransaction.commit();
        }

    }
    public static ItemTopicReply newInstance(BgmDataType.TopicReplyItem item) {
        ItemTopicReply fragment = new ItemTopicReply();
        fragment.setData(item);
        return fragment;
    }
}
