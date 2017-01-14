package moe.exmagic.tricks.bangumiinfo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import moe.exmagic.tricks.bangumiinfo.utils.DataType;

/**
 * Created by SternW Zhang on 17-1-9.
 * 项目细明页
 */

public class ItemDetailFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_item_detail,container,false);
        return  v;
    }

    public static ItemDetailFragment newInstance(DataType.SearchResultItem item){
        ItemDetailFragment fragmet = new ItemDetailFragment();
        return fragmet;
    }
}
