package moe.exmagic.tricks.bangumiinfo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import moe.exmagic.tricks.bangumiinfo.utils.DataType;
import moe.exmagic.tricks.bangumiinfo.utils.WebSpider;
import moe.exmagic.tricks.bangumiinfo.utils.view.CardCharactersFragment;
import moe.exmagic.tricks.bangumiinfo.utils.view.CardSummaryFragment;

/**
 * Created by SternW Zhang on 17-1-9.
 * 项目细明页
 */

public class ItemDetailFragment extends Fragment {
    private DataType.DetailItem         mDetailItem;
    private DataType.SearchResultItem   mBaseItem;
    private MainActivity                mParent;
    public void setBaseItem(DataType.SearchResultItem item){
        mBaseItem = item;
    }
    public void setParent(MainActivity parent){
        mParent = parent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_item_detail,container,false);
        WebSpider.get(getActivity()).GetItemDetail(WebSpider.BASE_SITE + "subject/" + mBaseItem.ItemId, this);

        return  v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

        public static ItemDetailFragment newInstance(DataType.SearchResultItem item, MainActivity parent){
        ItemDetailFragment fragment = new ItemDetailFragment();
        fragment.setBaseItem(item);
        fragment.setParent(parent);
        return fragment;
    }

    public void updateUI(DataType.DetailItem detail){
        if(detail == null){
            Toast.makeText(getActivity(),"载入失败",Toast.LENGTH_SHORT).show();
            return;
        }
        detail.BaseItem = mBaseItem;
        mDetailItem = detail;

        Fragment SummaryFragment = CardSummaryFragment.newInstance(mDetailItem);
        if(SummaryFragment != null){
            getFragmentManager().beginTransaction()
                    .add(R.id.detail_card_container,SummaryFragment,"Summary")
                    .commit();
        }
        Fragment CharactersFragment = CardCharactersFragment.newInstance(mDetailItem.CharactersList);
        if(CharactersFragment != null && mDetailItem.CharactersList != null){
            getFragmentManager().beginTransaction()
                    .add(R.id.detail_card_container,CharactersFragment,"Characters")
                    .commit();
        }
    }
}
