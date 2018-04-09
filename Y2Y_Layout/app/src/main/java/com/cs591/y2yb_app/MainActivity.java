package com.cs591.y2yb_app;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create the toolbar and replace the original action bar with our own toolbar
        mToolbar = (Toolbar) findViewById(R.id.nav_action);
        setSupportActionBar(mToolbar);

        // set up our drawer containing the options
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_opened, R.string.drawer_closed);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        // set the hamburger to open up the drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // display the home screen as a default
        displaySelectedScreen(R.id.nav_home);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        displaySelectedScreen(id);
                        return true;
                    }
                }
        );
    }

    private void displaySelectedScreen(int id) {
        Fragment fragment = null;

        // set the initialized fragment to the fragment with the corresponding id
        switch(id) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_lottery:
                fragment = new LotteryFragment();
        }

        // set the current screen to the fragment inputted as an argument
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        // close drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            Toast.makeText(this, "woah", Toast.LENGTH_SHORT).show();
            return true;
        }

        else {
            Toast.makeText(this, "haha", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

}
