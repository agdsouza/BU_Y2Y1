package com.cs591.y2yb_app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

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
    }

    private void displaySelectedScreen(int id) {
        Fragment fragment = null;

        // set the initialized fragment to the fragment with the corresponding id
        switch(id) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
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

        int id = item.getItemId();
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // display fragment using id
        else {
            displaySelectedScreen(id);
        }

        return super.onOptionsItemSelected(item);
    }

}
