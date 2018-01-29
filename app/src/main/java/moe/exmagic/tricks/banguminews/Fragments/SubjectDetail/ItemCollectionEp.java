package moe.exmagic.tricks.banguminews.Fragments.SubjectDetail;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType;

/**
 * Created by Stern on 2018/1/27.
 */

public class ItemCollectionEp extends Fragment {
    private TextView mTitleView;
    private TextView mCnTitleView;
    private TextView mOnAirDateView;
    private TextView mDurationView;
    private TextView mCommentView;
    private CheckBox mWatchedCheckbox;
    private BgmDataType.EpItem mItem;
    private OnWatchedCheckBoxClickedListener mListener;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_collection_ep_item, container, false);
        mTitleView = (TextView) v.findViewById(R.id.ep_title_view);
        mCnTitleView = (TextView) v.findViewById(R.id.ep_cn_title_view);
        mOnAirDateView = (TextView) v.findViewById(R.id.ep_on_air_date);
        mDurationView = (TextView) v.findViewById(R.id.ep_duration);
        mCommentView = (TextView) v.findViewById(R.id.ep_comment_num);
        mWatchedCheckbox = (CheckBox) v.findViewById(R.id.ep_set_watched_checkbox);
        return v;
    }

    public static ItemCollectionEp newInstance(BgmDataType.EpItem item, OnWatchedCheckBoxClickedListener listener) {
        ItemCollectionEp fragment = new ItemCollectionEp();
        fragment.mItem = item;
        fragment.mListener = listener;
        return fragment;
    }
}
