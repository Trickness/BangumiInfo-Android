package moe.exmagic.tricks.bangumiinfo.utils.view;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import moe.exmagic.tricks.bangumiinfo.R;
import moe.exmagic.tricks.bangumiinfo.utils.DataType;

/**
 * Created by tricks on 17-2-2.
 */

public class CardSummaryFragment extends Fragment {
    private TextView                    mTitleView;
    private TextView                    mIntroView;
    private TextView                    mScoreView;
    private TextView                    mSummaryView;
    private TextView                    mKVSummaryView;
    private TagFlowLayout               mTagFlowLayout;
    private TagFlowLayout               mEpFlowLayout;

    private DataType.SearchResultItem   mBaseItem;
    private DataType.DetailItem mDetailItem;
    public void setBaseItem(DataType.DetailItem item){
        if(item != null) {
            mBaseItem = item.BaseItem;
            mDetailItem = item;
        }
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.card_summary,container,false);
        mTagFlowLayout = (TagFlowLayout) v.findViewById(R.id.tagsView);
        mTitleView = (TextView) v.findViewById(R.id.textDetailTitle);
        mIntroView = (TextView) v.findViewById(R.id.textDetailIntro);
        mScoreView = (TextView) v.findViewById(R.id.textDetailScore);
        mSummaryView = (TextView) v.findViewById(R.id.textDetailSummary);
        mKVSummaryView = (TextView) v.findViewById(R.id.textDetailKVSummary);
        mEpFlowLayout = (TagFlowLayout) v.findViewById(R.id.episodesView);

        mTitleView.setText(mBaseItem.Title);
        mIntroView.setText(mBaseItem.Info);
        mScoreView.setText("Rank: " + mBaseItem.Rank + "  Score: " + mBaseItem.Score);

        mTagFlowLayout.setAdapter(new TagAdapter<String>(mDetailItem.Tags) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) inflater.inflate(R.layout.item_tag,mTagFlowLayout,false);
                tv.setText(s);
                return tv;
            }
        });
        List<String> keyList = new ArrayList<>(mDetailItem.Eps.keySet());
        mEpFlowLayout.setAdapter(new TagAdapter<String>(keyList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                DataType.EpItem item = mDetailItem.Eps.get(s);
                TextView tv = (TextView) inflater.inflate(R.layout.item_episode,mEpFlowLayout,false);
                tv.setText(item.Episode);
                if(item.isAvailable){
                    tv.setBackgroundResource(R.drawable.episode_unwatched);
                    tv.setTextColor(getResources().getColor(R.color.colorEpisodeUnwatchedText));
                }else{
                    tv.setBackgroundResource(R.drawable.episode_unavailable);
                    tv.setTextColor(getResources().getColor(R.color.colorEpisodeUnavailableText));
                }
                return tv;
            }
        });
        mSummaryView.setText(mDetailItem.Summary);
        mKVSummaryView.setText("");

        Iterator it = mDetailItem.KVInfo.entrySet().iterator();
        int i = 0;
        while(i < 8 && it.hasNext()){
            Map.Entry e = (Map.Entry)it.next();
            mKVSummaryView.append(e.getKey() + ": " + e.getValue().toString().substring(1,e.getValue().toString().length()-1) + '\n');
            i++;
        }
        return v;
    }
    public static CardSummaryFragment newInstance(DataType.DetailItem item){
        CardSummaryFragment fragment = new CardSummaryFragment();
        fragment.setBaseItem(item);
        return fragment;
    }
}
