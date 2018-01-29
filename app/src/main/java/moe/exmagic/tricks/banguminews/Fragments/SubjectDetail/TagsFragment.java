package moe.exmagic.tricks.banguminews.Fragments.SubjectDetail;

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
import moe.exmagic.tricks.banguminews.Utils.BgmDataType;

/**
 * Created by tricks on 17-2-9.
 */

public class TagsFragment extends Fragment {
    private ArrayList<BgmDataType.TagItem> mTags;
    public void setTags(ArrayList<BgmDataType.TagItem> tags){
        mTags = tags;
    }
    public void updateUI(ArrayList<BgmDataType.TagItem> tags) {
        setTags(tags);
        final ArrayList<String> index = new ArrayList<>();
        for(BgmDataType.TagItem item : mTags){
            index.add(item.Tag);
        }
        if (mTags != null && mInflater != null) {
            mTagFlowLayout.setAdapter(new TagAdapter<String>(index) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    View v =  mInflater.inflate(R.layout.item_tag,mTagFlowLayout,false);
                    ((TextView)v.findViewById(R.id.tag_text_view)).setText(s);
                    ((TextView)v.findViewById(R.id.tags_num_view)).setText(mTags.get(position).Num);
                    return v;
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
            updateUI(mTags);
        }
        return v;
    }
    public static TagsFragment newInstance(){
        return new TagsFragment();
    }

}
