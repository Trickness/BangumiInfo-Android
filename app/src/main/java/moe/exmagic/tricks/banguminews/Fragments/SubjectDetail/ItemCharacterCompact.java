package moe.exmagic.tricks.banguminews.Fragments.SubjectDetail;

import android.app.Fragment;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType.*;

/**
 * Created by tricks on 17-2-8.
 */

public class ItemCharacterCompact extends Fragment {
    private ImageView mCharacterHeader;
    private TextView mCharacterName;
    private TextView mCharacterRole;
    private TextView mCharacterCVName;
    private TextView mCharacterCommet;
    private TextView mCharacterCNName;
    private LinearLayout mTitleContainer;
    private int         mTagsWidth;

    private static Transformation mHeaderTransform = new RoundedTransformationBuilder()
            .cornerRadiusDp(3)
            .oval(false)
            .build();

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
        mCharacterRole              = (TextView) v.findViewById(R.id.character_compact_role);
        mCharacterCommet            = (TextView) v.findViewById(R.id.character_compact_commet);
        mCharacterCNName            = (TextView) v.findViewById(R.id.character_compact_name_cn);
        mTitleContainer             = (LinearLayout) v.findViewById(R.id.character_compact_title_container);

        mCharacterName.setText(mCharacterItem.CharacterName);
        if(mCharacterItem.CVInfo != null && mCharacterItem.CVInfo.size() != 0)
            mCharacterCVName.setText(mCharacterItem.CVInfo.get(0).Name);
        if(mCharacterItem.CharacterRoleName != null)
            mCharacterRole.setText(mCharacterItem.CharacterRoleName);
        if(mCharacterItem.CharacterTranslation != null)
            mCharacterCNName.setText(mCharacterItem.CharacterTranslation);
        String commentStr = "+" + mCharacterItem.CommentNumber + "讨论";
        mCharacterCommet.setText(commentStr);

        Rect bounds = new Rect();
        TextPaint paint = mCharacterCommet.getPaint();
        paint.getTextBounds(commentStr,0,commentStr.length(),bounds);
        mTagsWidth += bounds.width();
        if(mCharacterItem.CharacterRoleName != null){
            paint.getTextBounds(mCharacterItem.CharacterRoleName,0,mCharacterItem.CharacterRoleName.length(),bounds);
            mTagsWidth += bounds.width();
        }

        Picasso.with(getActivity()).load(mCharacterItem.CharacterHeaderUrl.replace("/l/","/g/"))
                .transform(mHeaderTransform)
                .into(mCharacterHeader);

        v.post(new Runnable() {
            @Override
            public void run() {
                int mParentWidth = mTitleContainer.getMeasuredWidth();
                int margin = (16+16)*getResources().getDisplayMetrics().densityDpi/160;      // 别问我16怎么来的，我随手写的
                if(mCharacterName.getWidth() > mParentWidth -  (mTagsWidth + margin)){
                    mCharacterName.setWidth(mParentWidth -  (mTagsWidth + margin));
                }
            }
        });
        return v;
    }

    public static ItemCharacterCompact newInstance(CharacterItem item){
        ItemCharacterCompact fragment = new ItemCharacterCompact();
        fragment.setDetailItem(item);
        return fragment;
    }
}
