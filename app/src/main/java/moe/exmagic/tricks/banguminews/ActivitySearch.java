package moe.exmagic.tricks.banguminews;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.widget.Toolbar;

import java.util.HashMap;
import java.util.Map;

import moe.exmagic.tricks.banguminews.Fragments.FragmentSearchResult;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType;
import moe.exmagic.tricks.banguminews.Utils.MyPagerViewAdapter;
import moe.exmagic.tricks.banguminews.WebSpider.WebSpider;

/**
 * Created by tricks on 17-2-13.
 */

public class ActivitySearch extends FragmentActivity {
    public static String EXTRA_SEARCH_KEY = "EXTRA_SEARCH_KEY";

    private String mSearchKeyWord;
    public MyPagerViewAdapter pAdapter;
    public Map<String,BgmDataType.SearchResult> pSearchResults = new HashMap<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mSearchKeyWord = getIntent().getStringExtra(EXTRA_SEARCH_KEY);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.search_activity_toolbar);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Search : " + mSearchKeyWord);

        ViewPager viewPager = (ViewPager) findViewById(R.id.result_list_container);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.search_type_tabs);
        if (viewPager != null) {
            setupViewPager(viewPager);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }
                @Override
                public void onPageSelected(int position) {
                    Log.d("DEBUG","SELECT");
                    if(pSearchResults.get(((FragmentSearchResult) pAdapter.getItem(position)).pSearchType) == null){
                        ((FragmentSearchResult) pAdapter.getItem(position)).doSearch();
                    }else{
                        // Ugly bug fix
                        ((FragmentSearchResult) pAdapter.getItem(position)).pSwipeRefreshLayout.setRefreshing(false);
                    }
                }
                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                        //正在滑动   pager处于正在拖拽中
                    } else if (state == ViewPager.SCROLL_STATE_SETTLING) {
                        //pager正在自动沉降，相当于松手后，pager恢复到一个完整pager的过程
                    } else if (state == ViewPager.SCROLL_STATE_IDLE) {
                        Log.d("DEBUG","IDLE");
                    }
                }
            });
        }
        tabLayout.setupWithViewPager(viewPager);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_result_menu, menu);
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        pAdapter = new MyPagerViewAdapter(getFragmentManager());
        pAdapter.addFragment(FragmentSearchResult.newInstance(mSearchKeyWord, WebSpider.SEARCH_ALL,this),"全部");
        pAdapter.addFragment(FragmentSearchResult.newInstance(mSearchKeyWord, WebSpider.SEARCH_BANGUMI,this),"番剧");
        pAdapter.addFragment(FragmentSearchResult.newInstance(mSearchKeyWord, WebSpider.SEARCH_GAME,this),"游戏");
        pAdapter.addFragment(FragmentSearchResult.newInstance(mSearchKeyWord, WebSpider.SEARCH_BOOK,this),"书籍");
        pAdapter.addFragment(FragmentSearchResult.newInstance(mSearchKeyWord, WebSpider.SEARCH_MUSIC,this),"音乐");
        pAdapter.addFragment(FragmentSearchResult.newInstance(mSearchKeyWord, WebSpider.SEARCH_3DIM,this),"3次元");
        viewPager.setAdapter(pAdapter);
        ((FragmentSearchResult) pAdapter.getItem(0)).setFirst();
    }

}
