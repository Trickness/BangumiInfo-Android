package moe.exmagic.tricks.banguminews;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.*;

import java.io.UnsupportedEncodingException;

import moe.exmagic.tricks.banguminews.WebSpider.OnCaptchaReadyListener;
import moe.exmagic.tricks.banguminews.WebSpider.OnLoginListener;
import moe.exmagic.tricks.banguminews.WebSpider.WebSpider;

/**
 * Created by Stern on 2018/1/20.
 */

public class ActivityLogin extends FragmentActivity{
    private TextInputEditText   mInputEmail;
    private TextInputEditText   mInputPassword;
    private TextInputEditText   mInputCaptcha;
    private Button              mSubmitButton;
    private ImageView           mCaptchaView;
    private int                 mAuthStatus = 0;
    private int                 mAuthCount = 0;
    private boolean             mInputCaptchaClicked = false;

    private String mB64Key;

    public static String KEY_LOGIN_RESULT = "login_result";
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mInputEmail = (TextInputEditText) findViewById(R.id.login_input_email);
        mInputPassword = (TextInputEditText) findViewById(R.id.login_input_password);
        mInputCaptcha = (TextInputEditText) findViewById(R.id.login_input_captcha);
        mSubmitButton = (Button) findViewById(R.id.login_input_submit);
        mCaptchaView = (ImageView) findViewById(R.id.login_captcha_view);
        mInputCaptcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mInputCaptchaClicked)
                    return;
                mInputCaptchaClicked = true;
                RefreshCaptcha();
            }
        });
        mCaptchaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefreshCaptcha();
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInputEmail.getText().length() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "请输入Email", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if (mInputPassword.getText().length() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if (mInputCaptcha.getText().length() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "请输入验证码", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                mSubmitButton.setClickable(false);

                String mAuthEmail = mInputEmail.getText().toString();
                String mAuthPass = mInputPassword.getText().toString();
                String mAuthChptcha = mInputCaptcha.getText().toString();

                try {
                    mB64Key = "Basic ".concat(new String(Base64.encode((mAuthEmail.concat(":").concat(mAuthPass)).getBytes("utf-8"), Base64.NO_WRAP), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    Log.d("DEBUG", e.toString());
                }

                // Web认证
                WebSpider.get(getApplicationContext()).WebAuth(mAuthEmail, mAuthPass, mAuthChptcha, new OnLoginListener() {
                    @Override
                    public void onSuccess(JSONObject data) {
                        WebSpider.get(getApplicationContext()).SaveStatus(getApplicationContext());
                        Toast toast = Toast.makeText(getApplicationContext(), "Web认证成功", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        AuthReturned(true);
                    }

                    @Override
                    public void onBusy() {

                    }

                    @Override
                    public void onFailed(JSONObject data) {
                        AuthReturned(false);
                        Toast toast = Toast.makeText(getApplicationContext(), "Web认证失败", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });
                // API认证
                WebSpider.get(getApplicationContext()).APIAuth(mAuthEmail, mAuthPass, null, new OnLoginListener() {
                    @Override
                    public void onSuccess(JSONObject data) {
                        WebSpider.get(getApplicationContext()).SaveStatus(getApplicationContext());
                        Toast toast = Toast.makeText(getApplicationContext(), "API认证成功", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        AuthReturned(true);
                    }

                    @Override
                    public void onBusy() {
                    }

                    @Override
                    public void onFailed(JSONObject data) {
                        Log.d("DEBUG", data.toString());
                        try {
                            Toast toast = Toast.makeText(getApplicationContext(), "API认证失败(".concat(data.getString("error")).concat(")"), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            AuthReturned(false);
                        } catch (JSONException e) {
                            Log.d("DEBUG", e.toString());
                        }
                    }
                });
            }
        });
    }
    @Override
    public void onBackPressed(){
        if(!WebSpider.get(getApplicationContext()).getAuthStatus()){
            // cannot pressed back while login
            return;
        }else{
            this.back();
        }
    }
    private void AuthReturned(boolean status){
        mAuthCount ++;
        if(status)
            mAuthStatus ++;
        if(mAuthCount == 2){
            if(mAuthStatus == 2)
                this.back();
            else
                mAuthStatus = 0;
            mSubmitButton.setEnabled(true);
        }
    }
    public void RefreshCaptcha(){
        WebSpider.get(getApplicationContext()).WebAuthFetchCaptcha(new OnCaptchaReadyListener() {
            @Override
            public void onSuccess(Bitmap image) {
                mCaptchaView.setImageBitmap(image);
            }
            @Override
            public void onFailed() {
            }

            @Override
            public void onBusy() {
                Toast toast = Toast.makeText(getApplicationContext(), "验证码正在载入中....", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }
    void back(){
        Intent intent = new Intent();
        intent.putExtra(ActivityLogin.KEY_LOGIN_RESULT,WebSpider.get(getApplicationContext()).getAuthStatus());
        setResult(RESULT_OK,intent);
        this.finish();
    }
}
