package moe.exmagic.tricks.banguminews.Views;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;



import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType;
import moe.exmagic.tricks.banguminews.htmltextview.HtmlHttpImageGetter;
import moe.exmagic.tricks.banguminews.htmltextview.HtmlTextView;

/**
 * Created by Stern on 2018/1/28.
 */

public class ItemTopicSubReply extends Fragment {
    BgmDataType.TopicReplyItem mItemData;

    //Views
    private ImageView       mHeaderView;
    private TextView        mNicknameView;
    private TextView        mDateTimeView;
    private HtmlTextView mReplyView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_topic_subreply, container, false);
        mHeaderView = (ImageView) v.findViewById(R.id.item_topic_sub_reply_header_view);
        mNicknameView = (TextView) v.findViewById(R.id.item_topic_sub_reply_nickname_view);
        mDateTimeView = (TextView) v.findViewById(R.id.item_topic_sub_reply_datetime);
        mReplyView = (HtmlTextView) v.findViewById(R.id.item_topic_sub_reply_html_view);
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
        mReplyView.setHtml(mItemData.ReplyContent, new HtmlHttpImageGetter(mReplyView));
        Picasso.with(getActivity()).load(mItemData.Submitter.UserHeaderUrl)
                .transform(ItemTopicReply.mHeaderTransform)
                .into(mHeaderView);
    }
    public static ItemTopicSubReply newInstance(BgmDataType.TopicReplyItem item) {
        ItemTopicSubReply fragment = new ItemTopicSubReply();
        fragment.setData(item);
        return fragment;
    }
}
