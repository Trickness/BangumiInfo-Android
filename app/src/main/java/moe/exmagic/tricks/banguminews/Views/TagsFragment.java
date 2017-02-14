package moe.exmagic.tricks.banguminews.Views;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;

import moe.exmagic.tricks.banguminews.R;

/**
 * Created by tricks on 17-2-9.
 */

public class TagsFragment extends Fragment {
    private ArrayList<String> mTags;
    public void setTags(ArrayList<String> tags){
        mTags = tags;
    }
    public void updateUI(ArrayList<String> tags) {
        setTags(tags);
        if (mTags != null && mInflater != null) {
            mTagFlowLayout.setAdapter(new TagAdapter<String>(mTags) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView tv = (TextView) mInflater.inflate(R.layout.item_tag,mTagFlowLayout,false);
                    tv.setText(s);
                    return tv;
                }
            });
        }
    }
    private TagFlowLayout mTagFlowLayout;
    private LayoutInflater mInflater;
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tags,container,false);
        mTagFlowLayout = (TagFlowLayout) v.findViewById(R.id.tagsView);
        mInflater = inflater;
        if (mTags != null) {
            if (mTags != null && mInflater != null) {
                mTagFlowLayout.setAdapter(new TagAdapter<String>(mTags) {
                    @Override
                    public View getView(FlowLayout parent, int position, String s) {
                        TextView tv = (TextView) mInflater.inflate(R.layout.item_tag,mTagFlowLayout,false);
                        tv.setText(s);
                        return tv;
                    }
                });
            }
        }
        return v;
    }
    public static TagsFragment newInstance(){
        return new TagsFragment();
    }

}
