package com.ccube9.user.home;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ccube9.user.R;
import com.ccube9.user.profile.ProfileActivity;
import com.ccube9.user.registration.ChooseRegistrationRoleActivity;
import com.ccube9.user.util.PrefManager;

public class HomeActivity extends AppCompatActivity {
    Toolbar toolbar;
    LinearLayout rl_drawer;
    ImageView iv_menu;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    TextView tv_profile,tv_payment,tv_ride_history,tv_support,tv_logout;
    Button btn_become_driver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tv_profile=findViewById(R.id.tv_profile);
        tv_payment=findViewById(R.id.tv_payment);
        tv_ride_history=findViewById(R.id.tv_ride_history);
        tv_logout=findViewById(R.id.tv_logout);
        tv_support=findViewById(R.id.tv_support);
        initView();
        SetDrawer();
        onClick();

            getSupportFragmentManager().beginTransaction().replace(R.id.main, new HomeFragment()).commit();

    }

    private void onClick() {


//        <fragment android:id="@+id/autocomplete_fragment"
//        android:layout_width="match_parent"
//        android:layout_height="wrap_content"
//        android:name="com.google.android.libraries.places.widget.AutocompleteSupportFragment"
//                />

        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(rl_drawer);
            }
        });
        tv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
            }
        });

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                PrefManager.LogOut(HomeActivity.this);
                startActivity(new Intent(HomeActivity.this,ChooseRegistrationRoleActivity.class));
                finish();
            }
        });
    }

    private void SetDrawer() {

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(HomeActivity.this, drawer,
                // Navigation menu toggle icon
                R.string.navigation_drawer_open, // Navigation drawer open description
                R.string.navigation_drawer_close // Navigation drawer close description
        );
        toggle.setDrawerIndicatorEnabled(true);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        // setOnGroupClickListener listener for group heading click

    }

    public void closeDrawer() {
        drawer.closeDrawer(Gravity.LEFT);
    }
    public void initView()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv_menu = (ImageView) toolbar.findViewById(R.id.iv_menu);
        rl_drawer = findViewById(R.id.rl_drawer);

    }//initViewClose





}
