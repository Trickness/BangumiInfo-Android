package moe.exmagic.tricks.bangumiinfo.utils.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import moe.exmagic.tricks.bangumiinfo.R;
import moe.exmagic.tricks.bangumiinfo.utils.DataType;

/**
 * Created by tricks on 17-2-2.
 */

public class ItemCharacterCompact extends Fragment {
    private ImageView   mCharacterHeader;
    private TextView    mCharacterName;
    private TextView    mCharacterType;
    private TextView    mCharacterCVName;
    private TextView    mCharacterCommet;

    private DataType.CharacterItem mCharacterItem;
    public void setDetailItem(DataType.CharacterItem item){
        mCharacterItem = item;
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_character_compact,container,false);
        mCharacterCVName            = (TextView) v.findViewById(R.id.character_compact_cv_info);
        mCharacterHeader            = (ImageView) v.findViewById(R.id.character_compact_header);
        mCharacterName              = (TextView) v.findViewById(R.id.character_compact_name);
        mCharacterType              = (TextView) v.findViewById(R.id.character_compact_type_view);
        mCharacterCommet            = (TextView) v.findViewById(R.id.character_compact_commet);

        mCharacterName.setText(mCharacterItem.CharacterName);
        mCharacterCVName.setText(mCharacterItem.CVName);
        mCharacterType.setText(mCharacterItem.CharacterType);
        mCharacterCommet.setText("\n讨论 " + mCharacterItem.CommentNumber);
        return v;
    }

    public static ItemCharacterCompact newInstance(DataType.CharacterItem item){
        ItemCharacterCompact fragment = new ItemCharacterCompact();
        fragment.setDetailItem(item);
        return fragment;
    }
}