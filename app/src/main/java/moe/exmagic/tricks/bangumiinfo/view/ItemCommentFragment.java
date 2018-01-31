package moe.exmagic.tricks.bangumiinfo.view;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import moe.exmagic.tricks.bangumiinfo.R;
import moe.exmagic.tricks.bangumiinfo.utils.DataType;

/**
 * Created by liu on 2017/1/14 014.
 * 吐槽条目
 */

public class ItemCommentFragment extends Fragment {
    private DataType.CommentItem oneCommentItem;

    public static ItemCommentFragment newInstance(DataType.CommentItem item){
        ItemCommentFragment fragment = new ItemCommentFragment();
        fragment.setItem(item);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
                           , Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.item_comment, container, false);
        ((ImageView) v.findViewById(R.id.comment_imageViewHeader))
                .setImageBitmap(oneCommentItem.User.UserHeader);
        ((TextView) v.findViewById(R.id.comment_textViewComments))
                .setText(oneCommentItem.Comment);
        ((TextView) v.findViewById(R.id.comment_textViewDatetime))
                .setText(String.valueOf(oneCommentItem.SubmitDatetime));
        ((TextView) v.findViewById(R.id.textViewScore))
                .setText(String.valueOf(oneCommentItem.Score));
        return v;
    }

    public void setItem(DataType.CommentItem item){
        oneCommentItem = item;
    }
}
