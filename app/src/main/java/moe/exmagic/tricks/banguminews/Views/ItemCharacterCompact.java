package moe.exmagic.tricks.banguminews.Views;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType.*;

/**
 * Created by tricks on 17-2-8.
 */

public class ItemCharacterCompact extends Fragment {
    private ImageView mCharacterHeader;
    private TextView mCharacterName;
    private TextView mCharacterType;
    private TextView mCharacterCVName;
    private TextView mCharacterCommet;

    private CharacterItem mCharacterItem;
    public void setDetailItem(CharacterItem item){
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
        if(mCharacterItem.CVInfo != null && mCharacterItem.CVInfo.size() != 0)
            mCharacterCVName.setText(mCharacterItem.CVInfo.get(0).Name);
        mCharacterType.setText(mCharacterItem.CharacterType);
        mCharacterCommet.setText("\n讨论 " + mCharacterItem.CommentNumber);
        Glide.with(getActivity()).load(mCharacterItem.CharacterHeaderUrl).diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_def_banner)
                .placeholder(R.drawable.ic_def_loading)
                .into(mCharacterHeader);
        return v;
    }

    public static ItemCharacterCompact newInstance(CharacterItem item){
        ItemCharacterCompact fragment = new ItemCharacterCompact();
        fragment.setDetailItem(item);
        return fragment;
    }
}
