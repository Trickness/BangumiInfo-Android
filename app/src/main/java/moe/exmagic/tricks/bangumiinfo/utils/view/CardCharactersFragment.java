package moe.exmagic.tricks.bangumiinfo.utils.view;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import moe.exmagic.tricks.bangumiinfo.R;
import moe.exmagic.tricks.bangumiinfo.utils.DataType;

/**
 * Created by tricks on 17-2-2.
 */

public class CardCharactersFragment extends Fragment {
    private ArrayList<DataType.CharacterItem> mCharacters;
    public void setCharacters(ArrayList<DataType.CharacterItem> items){
        mCharacters = items;
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.card_characters,container,false);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        for(DataType.CharacterItem item : mCharacters){
            ft.add(R.id.character_compact_container,ItemCharacterCompact.newInstance(item));
        }
        ft.commit();
        return v;
    }
    public static CardCharactersFragment newInstance(ArrayList<DataType.CharacterItem> items){
        CardCharactersFragment fragment = new CardCharactersFragment();
        fragment.setCharacters(items);
        return fragment;
    }
}
