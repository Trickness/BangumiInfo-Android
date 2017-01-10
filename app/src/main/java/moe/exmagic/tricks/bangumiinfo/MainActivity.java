package moe.exmagic.tricks.bangumiinfo;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import moe.exmagic.tricks.bangumiinfo.utils.WebSpider;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,View.OnClickListener {
    private String mCurrentSearchKeyWord = "";
    private ViewPager mPager;
    private PagerSlidingTabStrip mTabs;
    private myFragmentPagerAdapter mPagerAdapter;
    private Fragment mFragmentSearchAll;
    private Fragment mFragmentSearchBangumi;
    private Fragment mFragmentSearchGame;
    private Fragment mFragmentSearchMusic;
    private Fragment mFragmentSearchBook;
    private LinearLayout mSlideUpContainer;

    @Override
    public void onClick(View v) {
        addFragment();
    }

    private class myFragmentPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mViewList;
        public myFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        public void setViewList(List<Fragment> list) {
            mViewList = list;
        }
        @Override
        public Fragment getItem(int position) {
            return mViewList.get(position);
        }
        @Override
        public int getCount() {
            return mViewList.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return ((SearchResultFragment) mViewList.get(position)).getTitle();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPager = (ViewPager) findViewById(R.id.pager);
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //mSlideUpContainer = (LinearLayout) findViewById(R.id.slideup_container);

        mFragmentSearchAll = new SearchResultFragment().setSearchType(WebSpider.SEARCH_ALL);
        mFragmentSearchBangumi = new SearchResultFragment().setSearchType(WebSpider.SEARCH_BANGUMI);
        mFragmentSearchGame = new SearchResultFragment().setSearchType(WebSpider.SEARCH_GAME);
        mFragmentSearchMusic = new SearchResultFragment().setSearchType(WebSpider.SEARCH_MUSIC);
        mFragmentSearchBook = new SearchResultFragment().setSearchType(WebSpider.SEARCH_BOOK);

        ((SearchResultFragment)mFragmentSearchAll).setParent(this);
        ((SearchResultFragment)mFragmentSearchBangumi).setParent(this);
        ((SearchResultFragment)mFragmentSearchGame).setParent(this);
        ((SearchResultFragment)mFragmentSearchMusic).setParent(this);
        ((SearchResultFragment)mFragmentSearchBook).setParent(this);


        final List<Fragment> viewList = new ArrayList<>();
        viewList.add(mFragmentSearchAll);
        viewList.add(mFragmentSearchBangumi);
        viewList.add(mFragmentSearchGame);
        viewList.add(mFragmentSearchBook);
        viewList.add(mFragmentSearchMusic);

        // PagerAdapter
        mPagerAdapter = new myFragmentPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.setViewList(viewList);
        mPager.setAdapter(mPagerAdapter);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        mPager.setPageMargin(pageMargin);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int mLastState;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if (mLastState == 2) {
                    SearchResultFragment fm = (SearchResultFragment) mPagerAdapter.getItem(position);
                    if (!fm.getKeyWord().equals(mCurrentSearchKeyWord)) {
                        fm.search(mCurrentSearchKeyWord);
                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                mLastState = state;
            }
        });
        mTabs.setViewPager(mPager);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query.equals("")) {
            return false;
        }
        SearchResultFragment fm = (SearchResultFragment) mPagerAdapter.getItem(mPager.getCurrentItem());
        mCurrentSearchKeyWord = query;
        fm.search(query);
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(this);
        return true;

    }

    public void addFragment() {
    }
}
