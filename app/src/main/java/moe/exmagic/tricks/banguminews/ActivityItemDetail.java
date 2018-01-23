package moe.exmagic.tricks.banguminews;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;

import moe.exmagic.tricks.banguminews.Fragments.ItemDetailFragment;
import moe.exmagic.tricks.banguminews.Fragments.ItemEvaluationsFragment;
import moe.exmagic.tricks.banguminews.Fragments.ItemMyProcessFragment;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType.*;
import moe.exmagic.tricks.banguminews.Utils.MyPagerViewAdapter;
import moe.exmagic.tricks.banguminews.WebSpider.WebSpider;

/**
 * Created by tricks on 17-2-8.
 */

public class ActivityItemDetail extends FragmentActivity {
    public static String EXTRA_ITEM_DETAIL_BASEITEM = "detailItemBaseItem";
    public static String EXTRA_WEBSPIDER_COOKIES = "detailWebSpiderCookies";
    public static String EXTRA_ITEM_DETAIL = "detailExtra";
    private ItemDetailFragment mDetailFragment;
    private ItemEvaluationsFragment mCommentsFragment;
    private ItemMyProcessFragment mMyProcessFragment;
    private SearchResultItem mBaseItem;
    private TabLayout mDetailTabs;
    private ViewPager mDetailPager;
    private DetailItem mDetailItem;
    private MyPagerViewAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mBaseItem = (SearchResultItem)getIntent().getExtras().getBundle(EXTRA_ITEM_DETAIL).getSerializable(EXTRA_ITEM_DETAIL_BASEITEM);
        setContentView(R.layout.activity_item_detail);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.item_detail_toolbar);
        setActionBar(toolbar);
        getActionBar().setTitle(mBaseItem.OriginalTitle);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(mBaseItem.Title);
        loadBackdrop();

        mDetailTabs = (TabLayout) findViewById(R.id.detail_tabs);
        mDetailPager = (ViewPager) findViewById(R.id.detail_pager);
        setupViewPager(mDetailPager);
        mDetailTabs.setupWithViewPager(mDetailPager);
        mDetailTabs.setTabGravity(TabLayout.GRAVITY_FILL);
        WebSpider.get(this).GetItemDetail(mBaseItem.DetailUrl,this);

    }

    private void setupViewPager(ViewPager viewPager) {
        mAdapter = new MyPagerViewAdapter(getFragmentManager());
        mDetailFragment = ItemDetailFragment.newInstance();
        mCommentsFragment = ItemEvaluationsFragment.newInstance();
        mMyProcessFragment = ItemMyProcessFragment.newInstance();
        mAdapter.addFragment(mDetailFragment,"作品信息");
        mAdapter.addFragment(mCommentsFragment,"相关评价");
        mAdapter.addFragment(mMyProcessFragment,"我的信息");
        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(3);
        return;
    }
    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(mBaseItem.CoverUrl).centerCrop().into(imageView);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_result_menu, menu);
        return true;
    }
    public void updateUI(DetailItem item){
        mDetailItem = item;
        if(item == null){
            Toast.makeText(this,"载入失败", Toast.LENGTH_SHORT).show();
            return;
        }
        mDetailItem.BaseItem = mBaseItem;
        mCommentsFragment.updateUI(mDetailItem);
        mDetailFragment.updateUI(mDetailItem);
        mMyProcessFragment.updateUI(mDetailItem);
    }
    public void  replace(SearchResultItem replaceItem){
        // TODO: Replace Activity
    }
}
