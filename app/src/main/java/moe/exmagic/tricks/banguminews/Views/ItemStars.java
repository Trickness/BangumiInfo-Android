package moe.exmagic.tricks.banguminews.Views;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import moe.exmagic.tricks.banguminews.R;

/**
 * Created by Stern on 2018/1/24.
 */

public class ItemStars extends Fragment {
    private TextView mStars;
    private int      mScores = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_stars, container, false);
        mStars = (TextView) v.findViewById(R.id.stars_view);
        if(mScores != 0)
            this.updateUI();
        return v;
    }
    public void setScores(int scores){
        mScores = scores;
    }
    public void updateUI(){
        String str = "";
        for(int i = 0; i < mScores/2 ; ++i)
            str = str.concat(getResources().getString(R.string.star));
        if(mScores % 2 == 1){
            str = str.concat(getResources().getString(R.string.half_star));
        }
        mStars.setText(str);
    }

    public static ItemStars newInstance(int score) {
        ItemStars fragment = new ItemStars();
        fragment.setScores(score);
        return fragment;
    }
}
