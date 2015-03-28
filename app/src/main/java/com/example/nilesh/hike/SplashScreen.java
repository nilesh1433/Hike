package com.example.nilesh.hike;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.nilesh.util.Constants;
import com.example.nilesh.util.SharedPrefInstance;


public class SplashScreen extends HikeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();

        new CountDownTimer(2000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                if(SharedPrefInstance.getBoolean(SplashScreen.this, Constants.SharedPref.SHARED_PREF,
                        Constants.SharedPref.IS_REGISTERED))
                    startActivity(new Intent(SplashScreen.this, UserListActivity.class));
                else
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));

                SplashScreen.this.finish();

            }
        }.start();

    }
}
