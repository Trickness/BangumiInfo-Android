package moe.exmagic.tricks.banguminews.Utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tricks on 17-2-8.
 */

public class MyPagerViewAdapter extends FragmentPagerAdapter {
    public MyPagerViewAdapter(FragmentManager fm) {
        super(fm);
    }
    ArrayList<Fragment> mFragments = new ArrayList<>();
    ArrayList<String> mFragmentTitles = new ArrayList<>();

    public void addFragment(Fragment fragment, String title){
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }
}