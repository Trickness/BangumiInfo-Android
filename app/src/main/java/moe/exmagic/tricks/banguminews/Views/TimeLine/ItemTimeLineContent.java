package moe.exmagic.tricks.banguminews.Views.TimeLine;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idlestar.ratingstar.RatingStarView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lecho.lib.hellocharts.model.Line;
import moe.exmagic.tricks.banguminews.ActivityItemDetail;
import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType;
import moe.exmagic.tricks.banguminews.Views.ItemStars;
import moe.exmagic.tricks.banguminews.WebSpider.WebSpider;

/**
 * Created by Stern on 2018/1/24.
 */

public class ItemTimeLineContent extends Fragment {
    TextView mTopTextView;
    TextView mCommentView;
    TextView mDateView;
    LinearLayout mImageSetContainer;
    LinearLayout mCollectionContainer;
    BgmDataType.TimeLineCommon mTimeLineData;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_time_line_content, container, false);
        mTopTextView = (TextView) v.findViewById(R.id.time_line_base_text_view);
        mDateView = (TextView) v.findViewById(R.id.time_line_date_view);
        mImageSetContainer = (LinearLayout) v.findViewById(R.id.time_line_image_set_container);
        mCollectionContainer = (LinearLayout) v.findViewById(R.id.time_line_collection_info_container);

        if(mTimeLineData != null)
            updateUI();
        return  v;
    }
    public void updateUI(){
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        // For Top Text
        mTopTextView.setText(ItemTimeLine.generateTopStr(mTimeLineData));

        mDateView.setText(ItemTimeLine.generateTimeDate(mTimeLineData.StrDate));

        // Ending imageView

        if(mTimeLineData.TargetItems != null && mTimeLineData.TargetItems.size() != 0){
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(192,192);
            param.setMarginStart(8);
            param.setMarginEnd(8);
            for(final BgmDataType.TimeLineTargetItem item : mTimeLineData.TargetItems) {
                if(item.HeaderUrl == null || item.HeaderUrl.equals("")){
                    continue;
                }
                ImageView targetHeaderView = new ImageView(getActivity());
                if(item.DepartmentType.equals("subject")){
                    targetHeaderView.setClickable(true);
                    targetHeaderView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), ActivityItemDetail.class);
                            intent.putExtra(ActivityItemDetail.EXTRA_DETAIL_ITEM_ID,String.valueOf(item.DepartmentId));
                            startActivity(intent);
                        }
                    });
                }

                targetHeaderView.setLayoutParams(param);

                mImageSetContainer.addView(targetHeaderView);
                Picasso.with(getActivity()).load(item.HeaderUrl.replace("/l/","/g/"))
                        .into(targetHeaderView);
            }
        }
        if(mTimeLineData.Stars != null){
            RatingStarView starsView = new RatingStarView(getActivity());
            starsView.setRating(Float.valueOf(mTimeLineData.Stars) / 2);
            starsView.setStarNum(5);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 50);
            mCollectionContainer.addView(starsView, params);
            //fragmentTransaction.add(R.id.time_line_collection_info_container, ItemStars.newInstance(Integer.parseInt(mTimeLineData.Stars)));
        }
        if(mTimeLineData.Comment != null){
            mCommentView = new TextView(getActivity());
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mCommentView.setLayoutParams(param);
            mCommentView.setText(mTimeLineData.Comment);
            mCollectionContainer.addView(mCommentView);
        }
        fragmentTransaction.commit();
    }
    public void set(BgmDataType.TimeLineCommon data){
        mTimeLineData = data;
    }

    public static ItemTimeLineContent newInstance(BgmDataType.TimeLineCommon data) {
        ItemTimeLineContent fragment = new ItemTimeLineContent();
        fragment.set(data);
        return fragment;
    }
}
