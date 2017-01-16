package moe.exmagic.tricks.bangumiinfo;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import moe.exmagic.tricks.bangumiinfo.utils.DataType;
import moe.exmagic.tricks.bangumiinfo.utils.WebSpider;

public class MainActivity extends AppCompatActivity {
    private String mCurrentSearchKeyWord = "";
    private ViewPager mPager;
    private PagerSlidingTabStrip mTabs;
    private myFragmentPagerAdapter mPagerAdapter;
    private Fragment mFragmentSearchAll;
    private Fragment mFragmentSearchBangumi;
    private Fragment mFragmentSearchGame;
    private Fragment mFragmentSearchMusic;
    private Fragment mFragmentSearchBook;
    private Fragment mDetailFragment;
    private MenuItem mSearchViewMenuItem;
    private MaterialSearchView mSearchView;
    private Toolbar  mToolbar;

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
        mSearchView             = (MaterialSearchView) findViewById(R.id.search_view);
        mToolbar                = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorAppTitle));

        setSupportActionBar(mToolbar);

        mFragmentSearchAll      = SearchResultFragment.newInstance(WebSpider.SEARCH_ALL,this);
        mFragmentSearchBangumi  = SearchResultFragment.newInstance(WebSpider.SEARCH_BANGUMI,this);
        mFragmentSearchGame     = SearchResultFragment.newInstance(WebSpider.SEARCH_GAME,this);
        mFragmentSearchMusic    = SearchResultFragment.newInstance(WebSpider.SEARCH_MUSIC,this);
        mFragmentSearchBook     = SearchResultFragment.newInstance(WebSpider.SEARCH_BOOK,this);

        final List<Fragment> viewList = new ArrayList<>();
        viewList.add(mFragmentSearchAll);
        viewList.add(mFragmentSearchBangumi);
        viewList.add(mFragmentSearchGame);
        viewList.add(mFragmentSearchBook);
        viewList.add(mFragmentSearchMusic);

        // PagerAdapter
        mPagerAdapter = new myFragmentPagerAdapter(getFragmentManager());
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
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.equals("")) {
                    return false;
                }
                SearchResultFragment fm = (SearchResultFragment) mPagerAdapter.getItem(mPager.getCurrentItem());
                mCurrentSearchKeyWord = query;
                fm.search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        mSearchViewMenuItem = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(mSearchViewMenuItem);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            if (getFragmentManager().findFragmentByTag("Details") != null) {
                this.backToMain();
            }
            return false;
        }
        return super.onOptionsItemSelected(item);
    }
    public void launchDetailFragment(DataType.SearchResultItem item) {
        mDetailFragment = ItemDetailFragment.newInstance(item,this);
        getFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .add(R.id.detail_container,mDetailFragment,"Details")
                .addToBackStack(null)
                .commit();
        mToolbar.setNavigationIcon(R.drawable.ic_action_white_back);
        mSearchViewMenuItem.setVisible(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (getFragmentManager().findFragmentByTag("Details") != null) {
                this.backToMain();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    public void backToMain(){
        this.getFragmentManager()
                .popBackStack();
        mToolbar.setNavigationIcon(null);
        mSearchViewMenuItem.setVisible(true);
    }
}
