package moe.exmagic.tricks.banguminews;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;

import moe.exmagic.tricks.banguminews.Utils.BgmDataType.*;
import moe.exmagic.tricks.banguminews.Views.ItemCharacterCompact;
import moe.exmagic.tricks.banguminews.WebSpider.WebSpider;

/**
 * Created by tricks on 17-2-13.
 */

public class ActivityCharactersList extends FragmentActivity{
    public static String EXTRA_CHARACTER_LIST_ITEM_ID = "extra_character_list_item_id";
    private int mItemID;
    public void updateUI(ArrayList<CharacterItem> result){
        if(result == null) {
            Toast toast = Toast.makeText(this,"载入失败！", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        if(result.size() == 0){
            Toast toast = Toast.makeText(this,"没有角色", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        for(CharacterItem item : result){
            ft.add(R.id.more_character_container, ItemCharacterCompact.newInstance(item));
        }
        ft.commit();
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_character);
        Toolbar toolbar = (Toolbar) findViewById(R.id.more_character_toolbar);
        toolbar.setTitle("全部角色");
        setActionBar(toolbar);
        mItemID = getIntent().getIntExtra(EXTRA_CHARACTER_LIST_ITEM_ID,0);
        if(mItemID != 0)
            WebSpider.get(this).GetCharacterList("http://bgm.tv/subject/" + mItemID + "/characters",this);
    }
}
