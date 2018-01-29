package moe.exmagic.tricks.banguminews.Fragments.SubjectDetail;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import moe.exmagic.tricks.banguminews.ActivityCharactersList;
import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType.*;

/**
 * Created by tricks on 17-2-8.
 */

public class CharactersCompactItem extends Fragment {
    private ArrayList<CharacterItem> mCharacters;
    private int mItemID;
    public void setCharacters(ArrayList<CharacterItem> items){
        mCharacters = items;
    }
    public void setItemID(int id){
        mItemID = id;
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_characters_compact,container,false);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        for(CharacterItem item : mCharacters){
            ft.add(R.id.character_compact_container, ItemCharacterCompact.newInstance(item));
        }
        ft.commit();
        v.findViewById(R.id.character_compact_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ActivityCharactersList.class);
                intent.putExtra(ActivityCharactersList.EXTRA_CHARACTER_LIST_ITEM_ID,mItemID);
                startActivity(intent);
            }
        });
        return v;
    }
    public static CharactersCompactItem newInstance(ArrayList<CharacterItem> items, int itemId){
        CharactersCompactItem fragment = new CharactersCompactItem();
        fragment.setCharacters(items);
        fragment.setItemID(itemId);
        return fragment;
    }
}
