package moe.exmagic.tricks.banguminews.Views.Topic;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;


import moe.exmagic.tricks.banguminews.ActivityTopicView;
import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType;

/**
 * Created by tricks on 17-2-9.
 */

public class ItemTopicCompact extends Fragment {
    private TextView mTopicTitleView;
    private TextView mDepartmentNameView;
    private ImageView mSubmitterHeaderView;
    private TextView mReplyCountView;
    private LinearLayout mTopicOtherContainer;
    private BgmDataType.TopicCompactItem mItemData;
    private int     mParentWidth;
    private View mView;

    private Transformation mHeaderTransform = new RoundedTransformationBuilder()
            .cornerRadiusDp(2)
            .oval(false)
            .build();
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        if(mView != null)
            return mView;
        View v = inflater.inflate(R.layout.item_topic_compact, container,false);
        mTopicTitleView = (TextView)  v.findViewById(R.id.topic_compact_title_view);
        mSubmitterHeaderView = (ImageView) v.findViewById(R.id.topic_compact_header_view);
        mDepartmentNameView = (TextView) v.findViewById(R.id.topic_department_name_view);
        mReplyCountView = (TextView) v.findViewById(R.id.topic_reply_count_view);
        mTopicOtherContainer = (LinearLayout) v.findViewById(R.id.topic_other_container);
        mView = v;
        if(mItemData != null)
            updateUI(mItemData);
        v.post(new Runnable() {
            @Override
            public void run() {
                mParentWidth = mTopicOtherContainer.getMeasuredWidth();
                int repliesMeasuredWidth = mReplyCountView.getMeasuredWidth();
                int margin = (16+5)*getResources().getDisplayMetrics().densityDpi/160;
                if(mDepartmentNameView.getWidth() > mParentWidth -  (repliesMeasuredWidth + margin)){
                    mDepartmentNameView.setWidth(mParentWidth -  (repliesMeasuredWidth + margin));
                }
            }
        });
        mView = v;
        return v;
    }
    public void setItemData(BgmDataType.TopicCompactItem item){
        mItemData = item;
    }
    public void updateUI(BgmDataType.TopicCompactItem item){
        mItemData = item;
        mTopicTitleView.setText(item.Title);
        mReplyCountView.setText(item.RepliesNumber.replace("(","").replace(")","").replace(".",""));
        mDepartmentNameView.setText(item.DepartmentName);
        Picasso.with(getActivity()).load(mItemData.Submitter.UserHeaderUrl)
                .transform(mHeaderTransform)
                .into(mSubmitterHeaderView);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)mDepartmentNameView.getLayoutParams();
        if(params.width > (mParentWidth - mReplyCountView.getMeasuredWidth() - 32 - 8)){
            params.weight = mParentWidth - mReplyCountView.getMeasuredWidth() - 32 - 8;
        }
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(ActivityTopicView.KEY_TOPIC_ID, mItemData.TopicID);
                bundle.putString(ActivityTopicView.KEY_TOPIC_DEPARTMENT, mItemData.DepartmentType);
                Intent intent = new Intent();
                intent.setClass(getActivity(), ActivityTopicView.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public static ItemTopicCompact newInstance(BgmDataType.TopicCompactItem item) {
        ItemTopicCompact fragment = new ItemTopicCompact();
        fragment.setItemData(item);
        return fragment;
    }
}
