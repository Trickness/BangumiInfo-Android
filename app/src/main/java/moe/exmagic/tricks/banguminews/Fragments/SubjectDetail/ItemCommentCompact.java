package moe.exmagic.tricks.banguminews.Fragments.SubjectDetail;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType.*;

/**
 * Created by tricks on 17-2-8.
 */

public class ItemCommentCompact extends Fragment {
    private CommentItem mCommentItem;
    public void setCommentItem(CommentItem item){
        mCommentItem = item;
    }
    public static ItemCommentCompact newInstance(CommentItem item){
        ItemCommentCompact fragment = new ItemCommentCompact();
        fragment.setCommentItem(item);
        return fragment;
    }

    private ImageView mSubmitterHeader;
    private Transformation mHeaderTransform = new RoundedTransformationBuilder()
            .cornerRadiusDp(2)
            .oval(false)
            .build();

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_comment_compact,container,false);
        mSubmitterHeader = (ImageView) v.findViewById(R.id.comment_user_header);
        ((TextView)v.findViewById(R.id.comment_nickname)).setText(mCommentItem.User.UserNickname);
        ((TextView)v.findViewById(R.id.comment_submit_date)).setText(mCommentItem.SubmitDatetime);
        ((TextView)v.findViewById(R.id.comment_text)).setText(mCommentItem.Comment);
        if(mCommentItem.Score != -1)
            ((TextView)v.findViewById(R.id.comment_star_info)).setText("评分: " + mCommentItem.Score);
        Picasso.with(getActivity()).load(mCommentItem.User.UserHeaderUrl.replace("/l/","/g/"))
                .transform(mHeaderTransform)
                .into(mSubmitterHeader);
        return v;
    }
}
