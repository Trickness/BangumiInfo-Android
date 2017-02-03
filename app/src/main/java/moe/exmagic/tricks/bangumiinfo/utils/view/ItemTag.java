package moe.exmagic.tricks.bangumiinfo.utils.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import moe.exmagic.tricks.bangumiinfo.R;

/**
 * Created by tricks on 17-1-19.
 */

public class ItemTag extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.item_tag,container,false);
    }
}
