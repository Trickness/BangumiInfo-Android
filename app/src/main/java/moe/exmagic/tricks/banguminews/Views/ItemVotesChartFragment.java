package moe.exmagic.tricks.banguminews.Views;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;
import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType.*;
import moe.exmagic.tricks.banguminews.WebSpider.WebSpider;

/**
 * Created by tricks on 17-2-10.
 */

public class ItemVotesChartFragment extends Fragment {
    private DetailItem mDetailItem;
    public void setDetail(DetailItem item){
        mDetailItem = item;
    }
    public void updateUI(){
        if(mDetailItem != null){
            mScoreView.setText(""+mDetailItem.BaseItem.Score);
            if(mDetailItem.BaseItem.Score >= 8.5){
                mRecommendLevel.setText("神作");
            }else if(mDetailItem.BaseItem.Score >= 7.5){
                mRecommendLevel.setText("力荐");
            }else if(mDetailItem.BaseItem.Score >= 6.5){
                mRecommendLevel.setText("推荐");
            }else if(mDetailItem.BaseItem.Score >= 5.5){
                mRecommendLevel.setText("还行");
            }
            if(mDetailItem.BaseItem.Rank != 0) {
                int nVote = 0;
                for(int i : mDetailItem.ScoreDetail){
                    nVote += i;
                }
                mRankAndVotesView.setText("Bangumi " + WebSpider.getStrType(mDetailItem.BaseItem.ItemType) + " Ranked:#" + mDetailItem.BaseItem.Rank + " with " + nVote + " votes");
            }
            generateData();
        }
    }
    public static ItemVotesChartFragment newInstance(DetailItem detail){
        ItemVotesChartFragment fragment = new ItemVotesChartFragment();
        fragment.setDetail(detail);
        return fragment;
    }
    private ColumnChartView mScoreChartView;
    private TextView mRankAndVotesView;
    private TextView mScoreView;
    private TextView mRecommendLevel;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_score_and_votes_chart,container,false);
        mScoreChartView = (ColumnChartView) v.findViewById(R.id.score_chart_view);
        mRankAndVotesView = (TextView) v.findViewById(R.id.rank_and_votes_text_view);
        mScoreView = (TextView) v.findViewById(R.id.score_view);
        mRecommendLevel = (TextView) v.findViewById(R.id.recommend_level_text_view);
        if(mDetailItem != null){
            updateUI();
        }
        return v;
    }
    private void generateData() {
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        List<AxisValue> axisValues = new ArrayList<>();
        for (int i = 10; i > 0; --i) {

            values = new ArrayList<>();
            values.add(new SubcolumnValue((float)mDetailItem.ScoreDetail.get(i), Color.GRAY));
            axisValues.add(new AxisValue(10-i, (""+(i)).toCharArray()));

            Column column = new Column(values);
            column.setHasLabels(false);
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
        }

        ColumnChartData data = new ColumnChartData(columns);

        Axis axisX = new Axis(axisValues);
        Axis axisY = new Axis().setHasLines(true);
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        mScoreChartView.setColumnChartData(data);
        mScoreChartView.setZoomEnabled(false);
    }
}
