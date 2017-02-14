package moe.exmagic.tricks.banguminews.Views;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import moe.exmagic.tricks.banguminews.ActivityCommentList;
import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType.*;

/**
 * Created by tricks on 17-2-10.
 */

public class CommentsCompactFragment extends Fragment {
    private int mSubjectId;
    private ArrayList<CommentItem> mComments;
    public void setComments(ArrayList<CommentItem> items){
        mComments = items;
    }
    public void setSubjectId(int subjectId){
        mSubjectId = subjectId;
    }
    public void updateUI(){
        if(mComments != null){
            for (CommentItem item : mComments){
                getFragmentManager().beginTransaction()
                        .add(R.id.comment_compact_container,ItemCommentCompact.newInstance(item))
                        .commit();
            }
        }
    }
    Button mMoreButton;
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_comments_compact,container,false);
        mMoreButton = (Button) v.findViewById(R.id.comment_compact_more);
        mMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityCommentList.class);
                intent.putExtra(ActivityCommentList.COMMENT_EXTRA_SUBJECT_ID,mSubjectId);
                startActivity(intent);
            }
        });
        updateUI();
        return v;
    }
    public static CommentsCompactFragment newInstance(ArrayList<CommentItem> items, int subjectID){
        CommentsCompactFragment fragment = new CommentsCompactFragment();
        fragment.setComments(items);
        fragment.setSubjectId(subjectID);
        return fragment;
    }
}
