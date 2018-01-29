package moe.exmagic.tricks.banguminews;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.idlestar.ratingstar.RatingStarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import moe.exmagic.tricks.banguminews.Fragments.SubjectDetail.FragmentCollectionInfo;
import moe.exmagic.tricks.banguminews.Fragments.SubjectDetail.FragmentItemDetail;
import moe.exmagic.tricks.banguminews.Fragments.SubjectDetail.FragmentComments;
import moe.exmagic.tricks.banguminews.Fragments.SubjectDetail.ItemTagMain;
import moe.exmagic.tricks.banguminews.Fragments.SubjectDetail.ItemVotesChartFragment;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType.*;
import moe.exmagic.tricks.banguminews.Utils.MyPagerViewAdapter;
import moe.exmagic.tricks.banguminews.WebSpider.OnApiResponseListener;
import moe.exmagic.tricks.banguminews.WebSpider.WebSpider;

/**
 * Created by tricks on 17-2-8.
 */

public class ActivityItemDetail extends FragmentActivity {
    public static String EXTRA_ITEM_DETAIL_BASEITEM = "detailItemBaseItem";
    public static String EXTRA_DETAIL_ITEM_ID       = "detailItemId";
    public static String EXTRA_WEBSPIDER_COOKIES = "detailWebSpiderCookies";
    public static String EXTRA_ITEM_DETAIL = "detailExtra";
    private FragmentItemDetail mDetailFragment;
    private FragmentComments mCommentsFragment;
    private FragmentCollectionInfo mCollectionFragment;
    private TabLayout mDetailTabs;
    private ViewPager mDetailPager;
    private MyPagerViewAdapter mAdapter;
    private String mItemId;
    private ActionBar   mActionBar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ImageView mMainImageView;
    private BgmDataType.DetailItem mDetailItem;
    private Toolbar     mToolbar;
    private TextView    mTitleView;
    private TextView    mCnTitleView;
    private RatingStarView mStarsView;
    private TextView    mStarsNumView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail_new);

        Bundle bundle = getIntent().getExtras();
        mItemId = bundle.getString(EXTRA_DETAIL_ITEM_ID,"");
        if(mItemId.equals(""))      // 没有传入值的话
            this.finish();              // 退出


        final Toolbar toolbar = (Toolbar) findViewById(R.id.item_detail_toolbar);
        setActionBar(toolbar);
        mToolbar = toolbar;
        mActionBar = getActionBar();

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        mDetailTabs = (TabLayout) findViewById(R.id.detail_tabs);
        mDetailPager = (ViewPager) findViewById(R.id.detail_pager);
        mMainImageView = (ImageView) findViewById(R.id.detail_main_image_view);
        mTitleView  = (TextView) findViewById(R.id.detail_title_view);
        mCnTitleView = (TextView) findViewById(R.id.detail_cn_title_view);
        mStarsView = (RatingStarView)findViewById(R.id.detail_main_stars_view);
        mStarsNumView = (TextView) findViewById(R.id.detail_main_stars_num_view);
        setupViewPager(mDetailPager);
        mDetailTabs.setupWithViewPager(mDetailPager);
        mDetailTabs.setTabGravity(TabLayout.GRAVITY_FILL);
        WebSpider.get(getApplicationContext()).APIFetchSubjectDetail(mItemId, new OnApiResponseListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onSuccess(JSONObject object) {
                if(object.getInteger("type") != 2){
                    Toast toast = Toast.makeText(getApplicationContext(),"暂不支持该种类（请打死开发者）",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    return;
                }
                mDetailItem = new DetailItem();
                mDetailItem.BaseItem = new BgmDataType.SearchResultItem();
                mDetailItem.BaseItem.ItemId = object.getInteger("id");
                mDetailItem.BaseItem.DetailUrl = object.getString("url");
                mDetailItem.BaseItem.OriginalTitle = object.getString("name");
                mDetailItem.BaseItem.Title = object.getString("name_cn");
                mDetailItem.Summary = object.getString("summary");
                mDetailItem.Eps = new TreeMap<>();
                mDetailItem.BaseItem.CoverUrl = object.getJSONObject("images").getString("large");
                JSONArray array = object.getJSONArray("eps");
                mDetailItem.Eps = new TreeMap<>();
                for(Object e : array){
                    JSONObject E    = (JSONObject) e;
                    EpItem epItem   = new EpItem();
                    epItem.Status   = E.getString("status");
                    epItem.EpID     = E.getString("id");
                    epItem.Summary  = E.getString("desc");
                    epItem.AirDate  = E.getString("aitdate");
                    epItem.Title    = E.getString("name");
                    epItem.Translation = E.getString("name_cn");
                    epItem.CommentsNumber = Integer.parseInt(E.getString("comment"));
                    epItem.Duration = E.getString("duration");
                    epItem.Index    = Integer.parseInt(E.getString("sort"));
                    epItem.EpType   = Integer.parseInt(E.getString("type"));
                    mDetailItem.Eps.put(epItem.Index,epItem);
                }

                mDetailItem.BaseItem.Rank   = object.getShort("rank");
                mDetailItem.BaseItem.Score  = Float.parseFloat(object.getJSONObject("rating").getString("score"));
                mDetailItem.ScoreDetail = new ArrayList<>();
                mDetailItem.ScoreDetail.add(0); // 0 of 0
                JSONObject countObject = object.getJSONObject("rating").getJSONObject("count");
                for(int i = 1; i <= 10; ++i)
                    mDetailItem.ScoreDetail.add(countObject.getInteger("" + i));

                mDetailItem.CharactersList = new ArrayList<>();
                array = object.getJSONArray("crt");
                if(array != null){
                    for(Object e : array){
                        JSONObject E = (JSONObject) e;
                        CharacterItem item = new CharacterItem();
                        item.CharacterHeaderUrl = E.getJSONObject("images").getString("large");
                        item.CharacterRoleName  = E.getString("role_name");
                        item.CharacterID        = E.getShort("id");
                        item.CharacterName      = E.getString("name");
                        item.CharacterTranslation = E.getString("name_cn");
                        item.CommentNumber      = E.getString("comment");
                        item.CollectsNumber     = E.getString("collects");
                        // CV Info
                    /*item.CVInfo             = new ArrayList<>();
                    for(Object ce : E.getJSONArray("actors")){
                        JSONObject jE = (JSONObject) ce;
                        PersonItem pItem = new PersonItem();
                        if(jE.getJSONObject("images") != null)
                            pItem.HeaderUrl = jE.getJSONObject("images").getString("large");
                        pItem.PersonID  = jE.getString("id");
                        pItem.Name      = jE.getString("name");
                        item.CVInfo.add(pItem);
                    }*/
                        mDetailItem.CharactersList.add(item);
                    }
                }


                mDetailItem.StaffInfo = new HashMap<>();
                array = object.getJSONArray("staff");
                if(array != null){
                    for(Object e :array) {
                        JSONObject E = (JSONObject) e;
                        PersonItem item = new PersonItem();
                        if(E.getJSONObject("images") != null)
                            item.HeaderUrl = E.getJSONObject("images").getString("large");
                        item.PersonID   = E.getString("id");
                        item.Name       = E.getString("name");
                        item.Translation= E.getString("name_cn");
                        for(Object ce : E.getJSONArray("jobs")){
                            String job = (String) ce;
                            if(mDetailItem.StaffInfo.containsKey(job)){
                                mDetailItem.StaffInfo.get(job).add(item);
                            }else{
                                mDetailItem.StaffInfo.put(job,new ArrayList<PersonItem>());
                                mDetailItem.StaffInfo.get(job).add(item);
                            }

                        }
                    }
                }

                mDetailItem.AirDate = object.getString("air_date");
                mDetailItem.AirWeekday = object.getString("air_weekday");
                mDetailItem.Blogs = new ArrayList<>();
                if(object.getJSONArray("blog") != null){
                    for(Object e : object.getJSONArray("blog")){
                        JSONObject jE = (JSONObject) e;
                        BlogItem item = new BlogItem();
                        item.Title = jE.getString("title");
                        item.BlogID = jE.getString("id");
                        item.BlogPreview = jE.getString("summary");
                        item.SubmitDatetime = jE.getString("dateline");
                        item.BlogReplyNumber = jE.getString("replies");

                        // user
                        UserItem uItem = new UserItem();
                        JSONObject jUser = jE.getJSONObject("user");
                        uItem.Signature = jUser.getString("sign");
                        uItem.UserID    = jUser.getString("id");
                        uItem.UserNickname=jUser.getString("nickname");
                        uItem.UserHeaderUrl=jUser.getJSONObject("avatar").getString("large");
                        item.Submitter = uItem;


                        mDetailItem.Blogs.add(item);
                    }
                }


                mDetailItem.Topics = new ArrayList<>();
                if(object.getJSONArray("topic") != null){
                    for(Object e : object.getJSONArray("topic")){
                        JSONObject jE = (JSONObject) e;
                        SubjectTopicItem item = new SubjectTopicItem();
                        item.Title = jE.getString("title");
                        item.TopicID = jE.getString("id");
                        item.RepliesNumber = jE.getShort("replies");
                        item.SubjectID = jE.getString("main_id");

                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            item.SubmitDate = df.parse(jE.getString("timestamp")).toString();
                            item.LastReplyDate = df.parse(jE.getString("lastpost")).toString();
                        }catch (Exception ex){
                            Log.d("DEBUG",ex.toString());
                        }

                        // user
                        UserItem uItem = new UserItem();
                        JSONObject jUser = jE.getJSONObject("user");
                        uItem.Signature = jUser.getString("sign");
                        uItem.UserID    = jUser.getString("id");
                        uItem.UserNickname=jUser.getString("nickname");
                        uItem.UserHeaderUrl=jUser.getJSONObject("avatar").getString("large");
                        item.Submitter = uItem;

                        mDetailItem.Topics.add(item);
                    }
                }

                updateUI(mDetailItem);
                Log.d("DEBUG","DEBUG");
            }

            @Override
            public void onFailed(JSONObject object) {

            }
        });
        WebSpider.get(getApplicationContext()).GetItemDetail(WebSpider.PROTOCOL + WebSpider.BASE_SITE + "/subject/" + mItemId,this);
    }

    private void setupViewPager(ViewPager viewPager) {
        mAdapter = new MyPagerViewAdapter(getFragmentManager());
        mDetailFragment = FragmentItemDetail.newInstance();
        mCommentsFragment = FragmentComments.newInstance(mItemId);
        mCollectionFragment = FragmentCollectionInfo.newInstance(mDetailItem);
        mAdapter.addFragment(mDetailFragment,"作品信息");
        mAdapter.addFragment(mCommentsFragment,"吐槽箱");
        mAdapter.addFragment(mCollectionFragment,"收藏盒");
        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(3);
    }
    private void loadBackdrop() {
        Glide.with(this).load(mDetailItem.BaseItem.CoverUrl).centerCrop().into(mMainImageView);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_result_menu, menu);
        return true;
    }
    public void updateUI(DetailItem item){
        if(item == null){
            Toast.makeText(this,"载入失败", Toast.LENGTH_SHORT).show();
            return;
        }
        if(item.KVInfo != null){        // 这是WebSpider返回的结果
            mDetailFragment.updateUI(item);
            // main tag
            if(item.Tags != null && item.Tags.size() > 2){
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.detail_main_tag_container, ItemTagMain.newInstance(item.Tags.get(0).Tag));
                fragmentTransaction.add(R.id.detail_main_tag_container, ItemTagMain.newInstance(item.Tags.get(1).Tag));
                fragmentTransaction.commit();
            }
            // main tag end
            return;
        }
        mTitleView.setText(item.BaseItem.OriginalTitle);
        mCnTitleView.setText(item.BaseItem.Title);
        mDetailFragment.updateUI(item);//  API 返回的
        mDetailItem = item;
        loadBackdrop();
        //mActionBar.setTitle(item.BaseItem.Title);
        //mToolbar.setTitle(item.BaseItem.Title);
        mCollapsingToolbarLayout.setTitle(item.BaseItem.Title);
        mCollectionFragment.updateUI(item);
        mStarsView.setRating(item.BaseItem.Score/2);
        mStarsNumView.setText(String.valueOf(item.BaseItem.Score));

        //FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        //fragmentTransaction.add(R.id.detail_top_container, ItemVotesChartFragment.newInstance(mDetailItem)).commit();


//        mCommentsFragment.updateUI(mDetailItem);
//        mDetailFragment.updateUI(mDetailItem);
    }
    public void  replace(SearchResultItem replaceItem){
        // TODO: Replace Activity
    }
}
