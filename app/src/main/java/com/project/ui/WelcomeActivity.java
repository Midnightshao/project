package com.project.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.project.Application.CApplication;
import com.project.R;
import com.project.util.SharedPreferencesUtil;


public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        Handler handler=new Handler();

        SharedPreferencesUtil sharedPreferencesUtil=new SharedPreferencesUtil(this);
        if(!sharedPreferencesUtil.ReaderUserNamePassword().equals(null)){
            CApplication app=(CApplication) getApplication();
            app.val();
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);

                WelcomeActivity.this.startActivity(intent);

                WelcomeActivity.this.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out
                );
                finish();
            }
        }, 3000);

    }

}
