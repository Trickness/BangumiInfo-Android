package moe.exmagic.tricks.banguminews.Views.TimeLine;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType;
import moe.exmagic.tricks.banguminews.Views.ItemDivider;

/**
 * Created by Stern on 2018/1/24.
 */

public class ItemTimeLine extends Fragment{
    RoundedImageView mSubmitterHeaderView;
    ArrayList<BgmDataType.TimeLineCommon>  mTimeLineData;
    private static Transformation mHeaderTransform = new RoundedTransformationBuilder()
            .cornerRadiusDp(8)
            .oval(false)
            .build();
    private View mRootView;
    public static String generateTopStr(BgmDataType.TimeLineCommon data){
        String topText = data.Submitter.UserNickname
                + " " + data.StrAction + " ";
        if(data.TargetItems != null){
            for(int i = 0; i < data.TargetItems.size(); ++i){
                BgmDataType.TimeLineTargetItem item = data.TargetItems.get(i);
                topText = topText.concat(item.Title);
                if(i < data.TargetItems.size() -1){
                    topText.concat("、");
                }
            }
        }
        if(data.StrObject != null)
            topText = topText.concat(data.StrObject);
        return  topText;
    }
    public static String generateTimeDate(String date){
        return  date.replace("天","d")
                .replace("小时","h")
                .replace("分钟","m")
                .replace("分","m")
                .replace("秒","s")
                .replace("前","");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(mRootView != null)
            return mRootView;
        View v = inflater.inflate(R.layout.item_time_line, container,  false);
        mSubmitterHeaderView = (RoundedImageView) v.findViewById(R.id.time_line_submitter_header_view);
        if(mTimeLineData != null){
            updateUI();
        }
        mRootView = v;
        return v;
    }
    void updateUI(){
        Picasso.with(getActivity()).load(mTimeLineData.get(0).Submitter.UserHeaderUrl)
                .transform(mHeaderTransform)
                .into(mSubmitterHeaderView);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        for(BgmDataType.TimeLineCommon data : mTimeLineData) {
            fragmentTransaction.add(R.id.time_line_content_container, ItemTimeLineContent.newInstance(data));
        }
        fragmentTransaction.add(R.id.time_line_item_container, ItemDivider.newInstance());
        fragmentTransaction.commit();
    }
    void set(BgmDataType.TimeLineCommon data){
        mTimeLineData = new ArrayList<>();
        mTimeLineData.add(data);
    }
    public void addTimeLine(BgmDataType.TimeLineCommon data){
        mTimeLineData.add(data);
    }

    public static ItemTimeLine newInstance(BgmDataType.TimeLineCommon data) {
        ItemTimeLine fragment = new ItemTimeLine();
        fragment.set(data);
        return fragment;
    }
}
