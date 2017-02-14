package moe.exmagic.tricks.banguminews.Views;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType.*;

/**
 * Created by tricks on 17-2-9.
 */

public class EpsFragment extends Fragment {
    private Map<String,EpItem> mEps;
    public void setEps(Map<String,EpItem> eps){
        mEps = eps;
    }
    public void updateUI(Map<String,EpItem> eps) {
        setEps(eps);
        if (mEps != null && mInflater != null) {
            List<String> keyList = new ArrayList<>(mEps.keySet());
            mEpFlowLayout.setAdapter(new TagAdapter<String>(keyList) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    EpItem item = mEps.get(s);
                    TextView tv = (TextView) mInflater.inflate(R.layout.item_episode, mEpFlowLayout, false);
                    tv.setText(item.Episode);
                    if (item.isAvailable) {
                        tv.setBackgroundResource(R.drawable.episode_unwatched);
                        tv.setTextColor(getResources().getColor(R.color.colorEpisodeUnwatchedText));
                    } else {
                        tv.setBackgroundResource(R.drawable.episode_unavailable);
                        tv.setTextColor(getResources().getColor(R.color.colorEpisodeUnavailableText));
                    }
                    return tv;
                }
            });
        }
    }
    private TagFlowLayout mEpFlowLayout;
    private LayoutInflater mInflater;
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_eps,container,false);
        mEpFlowLayout = (TagFlowLayout) v.findViewById(R.id.episodesView);
        mInflater = inflater;
        if (mEps != null) {
            List<String> keyList = new ArrayList<>(mEps.keySet());
            mEpFlowLayout.setAdapter(new TagAdapter<String>(keyList) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    EpItem item = mEps.get(s);
                    TextView tv = (TextView) mInflater.inflate(R.layout.item_episode, mEpFlowLayout, false);
                    tv.setText(item.Episode);
                    if (item.isAvailable) {
                        tv.setBackgroundResource(R.drawable.episode_unwatched);
                        tv.setTextColor(getResources().getColor(R.color.colorEpisodeUnwatchedText));
                    } else {
                        tv.setBackgroundResource(R.drawable.episode_unavailable);
                        tv.setTextColor(getResources().getColor(R.color.colorEpisodeUnavailableText));
                    }
                    return tv;
                }
            });
        }
        return v;
    }
    public static EpsFragment newInstance(){
        return new EpsFragment();
    }

}
