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
    public Bitmap commentUserHeader;
    public String commentDateTime;
    public String commentText;
    public String commentScore;

    public static ItemCommentFragment newInstance(DataType.CommentItem item){
        ItemCommentFragment fragment = new ItemCommentFragment();
        fragment.commentUserHeader = item.User.UserHeader;
        fragment.commentText = item.Comment;
        fragment.commentScore = String.valueOf(item.Score);
        fragment.commentDateTime = String.valueOf(item.SubmitDatetime);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
                           , Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.item_comment, container, false);
        ((ImageView) v.findViewById(R.id.comment_imageViewHeader)).setImageBitmap(this.commentUserHeader);
        ((TextView) v.findViewById(R.id.comment_textViewComments)).setText(this.commentText);
        ((TextView) v.findViewById(R.id.comment_textViewDatetime)).setText(this.commentDateTime);
        ((TextView) v.findViewById(R.id.textViewScore)).setText(this.commentScore);
        return v;
    }
}
