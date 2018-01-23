package moe.exmagic.tricks.banguminews.Views;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import moe.exmagic.tricks.banguminews.R;

/**
 * Created by tricks on 17-2-9.
 */

public class SummaryFragment extends Fragment {
    private String mSummary;
    public void setSummary(String s){
        mSummary = s;
    }
    public void updateUI(String s){
        setSummary(s);
        if(mSummary != null && mSummary.length() != 0){
            mSummaryView.setText(mSummary);
        }
    }

    private TextView mSummaryView;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_summary,container,false);
        mSummaryView = (TextView) v.findViewById(R.id.textDetailSummary);
        updateUI(mSummary);
        return v;
    }
    public static SummaryFragment newInstance(){
        return new SummaryFragment();
    }
}
