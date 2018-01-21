package moe.exmagic.tricks.banguminews;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import moe.exmagic.tricks.banguminews.WebSpider.WebSpider;

/**
 * Created by Stern on 2018/1/20.
 */

public class ActivityPersonalInfo extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
    }
}
