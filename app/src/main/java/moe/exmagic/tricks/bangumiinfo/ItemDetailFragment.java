package moe.exmagic.tricks.bangumiinfo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * Created by tricks on 17-1-9.
 */

public class ItemDetailFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_item_detail,container,false);
        getActivity().setContentView(R.layout.fragment_item_detail);
        return  v;
    }
}
