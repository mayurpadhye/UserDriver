package com.ccube9.user;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ccube9.user.home.HomeActivity;
import com.ccube9.user.registration.ChooseRegistrationRoleActivity;
import com.ccube9.user.util.PrefManager;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {

                if (PrefManager.IsLogin(SplashActivity.this))
                {
                    startActivity(new Intent(SplashActivity.this,HomeActivity.class));
                    finish();
                }
                else
                {
                    startActivity(new Intent(SplashActivity.this,ChooseRegistrationRoleActivity.class));
                    finish();
                }



            }
        }, SPLASH_TIME_OUT);









    }
}
