package moe.exmagic.tricks.banguminews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class ActivityMain extends AppCompatActivity {
    private MaterialSearchView mSearchView;
    private MenuItem mSearchViewMenuItem;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSearchView = (MaterialSearchView) findViewById(R.id.search_view);
        mToolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(mToolbar);
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
}
