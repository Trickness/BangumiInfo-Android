package moe.exmagic.tricks.banguminews.Views;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import moe.exmagic.tricks.banguminews.R;

/**
 * Created by tricks on 17-2-9.
 */

// TODO: KVDetail More Info

public class KVSummaryFragment extends Fragment {
    private Map<String,ArrayList<String>> mKVSummary;
    public void setSummary(Map<String,ArrayList<String>> s){
        mKVSummary = s;
    }
    public void updateUI(Map<String,ArrayList<String>> s){
        if(s == null)
            return;
        setSummary(s);
        Iterator it = mKVSummary.entrySet().iterator();
        int i = 0;
        while(i < 8 && it.hasNext()){
            Map.Entry e = (Map.Entry)it.next();
            mSummaryView.append(e.getKey() + ": " + e.getValue().toString().substring(1,e.getValue().toString().length()-1) + '\n');
            i++;
        }
    }

    private TextView mSummaryView;
    private View.OnClickListener expand = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Iterator it = mKVSummary.entrySet().iterator();
            int i = 0;
            mSummaryView.setText("");
            while(it.hasNext()){
                Map.Entry e = (Map.Entry)it.next();
                mSummaryView.append(e.getKey() + ": " + e.getValue().toString().substring(1,e.getValue().toString().length()-1) + '\n');
                i++;
            }
            v.setOnClickListener(collapse);
            ((Button)v).setText("收起");
        }
    };
    private View.OnClickListener collapse  = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Iterator it = mKVSummary.entrySet().iterator();
            int i = 0;
            mSummaryView.setText("");
            while(i < 8 && it.hasNext()){
                Map.Entry e = (Map.Entry)it.next();
                mSummaryView.append(e.getKey() + ": " + e.getValue().toString().substring(1,e.getValue().toString().length()-1) + '\n');
                i++;
            }
            v.setOnClickListener(expand);
            ((Button)v).setText("展开");
        }
    };
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_kvsummary,container,false);
        mSummaryView = (TextView) v.findViewById(R.id.textDetailKVSummary);
        v.findViewById(R.id.kv_summary_more).setOnClickListener(expand);
        updateUI(mKVSummary);
        return v;
    }
    public static KVSummaryFragment newInstance(){
        return new KVSummaryFragment();
    }
}
