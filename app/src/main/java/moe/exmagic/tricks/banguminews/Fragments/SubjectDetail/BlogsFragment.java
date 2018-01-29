package moe.exmagic.tricks.banguminews.Fragments.SubjectDetail;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import moe.exmagic.tricks.banguminews.ActivityBlogList;
import moe.exmagic.tricks.banguminews.R;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType.*;

/**
 * Created by tricks on 17-2-9.
 */

public class BlogsFragment extends Fragment {
    private int mSubjectId;
    private ArrayList<BlogItem> mBlogs;
    public void setBlogs(ArrayList<BlogItem> blogs){
        mBlogs = blogs;
    }
    public void setSubjectId(int id){
        mSubjectId = id;
    }
    private void updateUI(){
        if(mBlogs != null){
            for(BlogItem item : mBlogs){
                getFragmentManager().beginTransaction()
                        .add(R.id.blog_compact_container, ItemBlogCompact.newInstance(item))
                        .commit();
            }
        }
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_blogs_compact,container,false);
        v.findViewById(R.id.blog_compact_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityBlogList.class);
                intent.putExtra(ActivityBlogList.BLOG_EXTRA_SUBJECT_ID,mSubjectId);
                startActivity(intent);
            }
        });
        updateUI();
        return v;
    }

    public static BlogsFragment newInstance(ArrayList<BlogItem> blogs, int subjectId){
        BlogsFragment fragment = new BlogsFragment();
        fragment.setBlogs(blogs);
        fragment.setSubjectId(subjectId);
        return fragment;
    }
}
