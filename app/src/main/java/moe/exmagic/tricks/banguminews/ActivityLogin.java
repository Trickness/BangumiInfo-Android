package moe.exmagic.tricks.banguminews;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.*;

import java.io.UnsupportedEncodingException;

import moe.exmagic.tricks.banguminews.WebSpider.OnLoginListener;
import moe.exmagic.tricks.banguminews.WebSpider.WebSpider;

/**
 * Created by Stern on 2018/1/20.
 */

public class ActivityLogin extends FragmentActivity{
    private TextInputEditText   mInputEmail;
    private TextInputEditText   mInputPassword;
    private Button              mSubmitButton;

    private String mB64Key;

    public static String KEY_BASIC_INFO = "auth_data";
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final ActivityLogin mSelf = this;
        mInputEmail = (TextInputEditText) findViewById(R.id.login_input_email);
        mInputPassword = (TextInputEditText) findViewById(R.id.login_input_password);
        mSubmitButton = (Button) findViewById(R.id.login_input_submit);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mInputEmail.getText().length() == 0){
                    Toast toast = Toast.makeText(getApplicationContext(),"请输入Email",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    return;
                }
                if(mInputPassword.getText().length() == 0){
                    Toast toast = Toast.makeText(getApplicationContext(),"请输入密码",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    return;
                }

                String mAuthEmail = mInputEmail.getText().toString();
                String mAuthPass = mInputPassword.getText().toString();

                try{
                    mB64Key = "Basic ".concat(new String(Base64.encode((mAuthEmail.concat(":").concat(mAuthPass)).getBytes("utf-8"),Base64.NO_WRAP),"utf-8"));
                }catch (UnsupportedEncodingException e){
                    Log.d("DEBUG",e.toString());
                }


                WebSpider.get(getApplicationContext()).Auth(mAuthEmail, mAuthPass, null,new OnLoginListener() {
                    @Override
                    public void onSuccess(JSONObject data) {
                        // Once login via username and password, save the config
                        SharedPreferences sp = getSharedPreferences(WebSpider.NAME_WS_CONFIG, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(WebSpider.CONFIG_KEY_AUTH_TOKEN, data.getString("auth"));
                        editor.putString(WebSpider.CONFIG_KEY_BASIC_INFO, data.toString());
                        editor.putString(WebSpider.CONFIG_KEY_WEB_COOKIES,JSON.toJSONString(WebSpider.get(getApplicationContext()).WebCookies.map));
                        editor.putString(WebSpider.CONFIG_KEY_API_COOKIES,JSON.toJSONString(WebSpider.get(getApplicationContext()).APICookies.map));
                        editor.putString(WebSpider.CONFIG_KEY_AUTH_BASIC, mB64Key);
                        editor.apply();

                        Toast toast = Toast.makeText(getApplicationContext(),"登陆成功",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                    @Override
                    public void onBusy() {}
                    @Override
                    public void onFailed(JSONObject data) {
                        Log.d("DEBUG",data.toString());
                        try{
                            Toast toast = Toast.makeText(getApplicationContext(),"登陆失败(".concat(data.getString("error")).concat(")"),Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }catch (JSONException e){
                            Log.d("DEBUG",e.toString());
                        }
                    }
                });
            }
        });

    }
}
