package moe.exmagic.tricks.banguminews;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.hjm.bottomtabbar.BottomTabBar;
import com.makeramen.roundedimageview.RoundedImageView;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import com.alibaba.fastjson.*;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import moe.exmagic.tricks.banguminews.Fragments.FragmentDiscussion;
import moe.exmagic.tricks.banguminews.Fragments.HomePage.FragmentGroup;
import moe.exmagic.tricks.banguminews.Fragments.HomePage.FragmentHome;
import moe.exmagic.tricks.banguminews.Fragments.HomePage.FragmentRakuen;
import moe.exmagic.tricks.banguminews.Fragments.HomePage.FragmentTimeLine;
import moe.exmagic.tricks.banguminews.Utils.MyPagerViewAdapter;
import moe.exmagic.tricks.banguminews.WebSpider.OnLoginListener;
import moe.exmagic.tricks.banguminews.WebSpider.WebSpider;


public class ActivityMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private MaterialSearchView mSearchView;
    private MenuItem mSearchViewMenuItem;
    private Toolbar mToolbar;
    private BottomTabBar mBottomTabBar;
    private TextView    mNavNicknameView;
    private TextView    mNavSignView;
    private RoundedImageView mNavHeaderView;
    private CoordinatorLayout mMainLayout;

    private MyPagerViewAdapter pAdapter;

    private boolean     isLoadingWebAuth = false;
    private boolean     isLoadingApiAuth = false;
    private boolean     authTried       = false;

    private static final int REQUEST_CODE_LOGIN = 0;

    // Fragment
    FragmentHome mFragmentHome;
    FragmentDiscussion mFragmentDiscussion;
    FragmentGroup mFragmentGroup;
    FragmentRakuen mFragmentRakuen;
    FragmentTimeLine mFragmentTimeLine;

    Fragment mCurrentFragment;

    private static Transformation mHeaderTransform = new RoundedTransformationBuilder()
            .cornerRadiusDp(32)
            .oval(false)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_new);
        mSearchView = (MaterialSearchView) findViewById(R.id.search_view);
        mToolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);

        // Fragments
        mFragmentHome = FragmentHome.newInstance();
        mFragmentGroup = FragmentGroup.newInstance();
        mFragmentRakuen = FragmentRakuen.newInstance();
        mFragmentTimeLine = FragmentTimeLine.newInstance();


        ViewPager viewPager =  findViewById(R.id.main_activity_pager);
        TabLayout tabLayout =  findViewById(R.id.main_activity_toolbar_tab);
        if(viewPager != null){
            setupViewPager(viewPager);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    Log.d("DEBUG","SELECTED");
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            tabLayout.setupWithViewPager(viewPager);
        }else{
            Log.d("DEBUG","VIEW PAGER IS NULL");
        }


        setSupportActionBar(mToolbar);



        // Floating button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
/*        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,mToolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.equals("")) {
                    return false;
                }
                Intent intent = new Intent(getApplicationContext(), ActivitySearch.class);
                intent.putExtra(ActivitySearch.EXTRA_SEARCH_KEY, query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        // 默认页面
        /*FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_fragment_container,mFragmentHome).commit();
        mCurrentFragment = mFragmentHome;*/

        // 登录部分
        if(!WebSpider.get(getApplicationContext()).getAuthStatus() && !authTried){
            authTried = true;   // only automatic auth once
            isLoadingApiAuth = true;
            isLoadingWebAuth = true;
            WebSpider.get(getApplicationContext()).CheckAPIAuthStatus(new OnLoginListener() {
                @Override
                public void onSuccess(JSONObject data) {
                    isLoadingApiAuth = false;
                    Log.d("DEBUG",data.toString());
                    Toast toast = Toast.makeText(getApplicationContext(),"API 已成功登录",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    onAPISuccessLogin();
                }

                @Override
                public void onBusy() {
                    isLoadingApiAuth = false;
                }

                @Override
                public void onFailed(JSONObject data) {
                    isLoadingApiAuth = false;
                    try{
                        WebSpider.get(getApplicationContext()).ClearSavedStatus(getApplicationContext());
                        Toast toast = Toast.makeText(getApplicationContext(),"API 自动登录失败(".concat(data.getString("error")).concat(")"),Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }catch (JSONException e){
                        Log.d("DEBUG",e.toString());
                    }
                }
            });
            WebSpider.get(getApplicationContext()).CheckWebAuthStatus(new OnLoginListener() {
                @Override
                public void onSuccess(JSONObject data) {
                    isLoadingWebAuth = false;
                    onWebSuccessLogin();
                }

                @Override
                public void onBusy() {
                    isLoadingWebAuth = false;
                }

                @Override
                public void onFailed(JSONObject data) {
                    isLoadingWebAuth = false;
                    try{
                        //WebSpider.get(getApplicationContext()).ClearSavedStatus(getApplicationContext());
                        Toast toast = Toast.makeText(getApplicationContext(),"WEB 自动登录失败",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }catch (JSONException e){
                        Log.d("DEBUG",e.toString());
                    }
                }
            });
        }
    }
    private void setupViewPager(ViewPager viewPager){
        pAdapter = new MyPagerViewAdapter(getFragmentManager());
        pAdapter.addFragment(mFragmentHome,"热点话题");
        pAdapter.addFragment(mFragmentRakuen,"超展开");        // 这不是乐园么？！
        pAdapter.addFragment(mFragmentTimeLine,"时空管理局");
        pAdapter.addFragment(mFragmentGroup,"小组");
        viewPager.setAdapter(pAdapter);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        mSearchViewMenuItem = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(mSearchViewMenuItem);
        mNavNicknameView  = (TextView)findViewById(R.id.nav_nickname_view);
        mNavSignView = (TextView)findViewById(R.id.nav_sign_view);
        mNavHeaderView = (RoundedImageView) findViewById(R.id.nav_header_view);
        if(WebSpider.get(getApplicationContext()).getAuthStatus()){
            this.onAPISuccessLogin();
        }
        (findViewById(R.id.side_nav_header)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!WebSpider.get(getApplicationContext()).getAuthStatus() && (!isLoadingApiAuth && !isLoadingWebAuth)){
                    Intent intent = new Intent(ActivityMain.this,ActivityLogin.class);
                    startActivityForResult(intent,REQUEST_CODE_LOGIN);
                }
                Log.d("DEBUG","CLICKED");
            }
        });
        mMainLayout = (CoordinatorLayout) findViewById(R.id.menu_coord_layout);
        Snackbar.make(mMainLayout, "正在尝试登录中......", Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show();

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            this.switchFragment(mFragmentHome);
        } else if (id == R.id.nav_discussion) {
            this.switchFragment(mFragmentDiscussion);
        } else if (id == R.id.nav_progress) {

        } else if (id == R.id.nav_self_info) {

        }else{

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
    }

    private void switchFragment(Fragment targetFragment){
        if(mCurrentFragment == targetFragment)  return;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if(!targetFragment.isAdded()){
            fragmentTransaction
                    .hide(mCurrentFragment)
                    .add(R.id.main_fragment_container,targetFragment)
                    .commit();
        }else{
            fragmentTransaction
                    .hide(mCurrentFragment)
                    .show(targetFragment)
                    .commit();
        }
        mCurrentFragment = targetFragment;
    }

    public void onAPISuccessLogin(){
        if(isLoadingWebAuth)
            Snackbar.make(mMainLayout, "Api 登录已成功，正在登录到 Web......", Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show();
        else
            Snackbar.make(mMainLayout, "登录成功", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        JSONObject data = WebSpider.get(getApplicationContext()).getAuthInfo();
        Picasso.with(getApplicationContext()).load(data.getJSONObject("avatar").getString("large"))
                .transform(mHeaderTransform)
                .into(mNavHeaderView);
        mNavNicknameView.setText(data.getString("nickname"));
        mNavSignView.setText(data.getString("sign"));

        /*Intent intent = new Intent(getApplicationContext(), ActivityItemDetail.class);
        intent.putExtra(ActivityItemDetail.EXTRA_DETAIL_ITEM_ID,"212279");
        startActivity(intent);*/
    }

    public void onWebSuccessLogin(){
        if(isLoadingApiAuth)
            Snackbar.make(mMainLayout, "Web 登录已成功，正在登录到 API......", Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show();
        else
            Snackbar.make(mMainLayout, "登录成功", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        mFragmentHome.Refresh();
        mFragmentTimeLine.startRefresh();
        //WebSpider.get(getApplicationContext()).GetTopicItem();

        //WebSpider.get(getApplicationContext()).GetItemDetail("http://bgm.tv/subject/207195",new ActivityItemDetail());
    }

    // 处理Login Activity传回的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_CODE_LOGIN && data != null){
            if(WebSpider.get(getApplicationContext()).getAuthStatus()){
                this.onAPISuccessLogin();

            }
        }

    }
}
