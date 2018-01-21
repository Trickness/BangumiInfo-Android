package moe.exmagic.tricks.banguminews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import com.alibaba.fastjson.*;

import java.util.Map;

import moe.exmagic.tricks.banguminews.WebSpider.OnLoginListener;
import moe.exmagic.tricks.banguminews.WebSpider.WebSpider;


public class ActivityMain extends AppCompatActivity {
    private MaterialSearchView mSearchView;
    private MenuItem mSearchViewMenuItem;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If not login
        if(WebSpider.get(getApplicationContext()).AuthStatus() == 0){
            WebSpider.get(getApplicationContext()).CheckAuthStatus(new OnLoginListener() {
                @Override
                public void onSuccess(JSONObject data) {
                    Log.d("DEBUG",data.toString());
                    Toast toast = Toast.makeText(getApplicationContext(),"已成功登陆",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }

                @Override
                public void onBusy() {
                }

                @Override
                public void onFailed(JSONObject data) {
                    try{
                        Toast toast = Toast.makeText(getApplicationContext(),"自动登陆失败(".concat(data.getString("error")).concat(")"),Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }catch (JSONException e){
                        Log.d("DEBUG",e.toString());
                    }
                }
            });
        }
        setContentView(R.layout.activity_main);
        mSearchView = (MaterialSearchView) findViewById(R.id.search_view);
        mToolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(mToolbar);
        WebSpider.get(getApplicationContext());
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
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        mSearchViewMenuItem = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(mSearchViewMenuItem);
        return true;
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.d("DEBUG","SAVE INSTANCE STATE");
        Log.d("DEBUG",WebSpider.get(getApplicationContext()).WebCookies.map.toString());
    }
}
