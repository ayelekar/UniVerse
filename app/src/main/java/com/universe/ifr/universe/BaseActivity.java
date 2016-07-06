package com.universe.ifr.universe;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class BaseActivity extends AppCompatActivity {

    FragmentManager mFragmentManager;

    private NavigationView drawerContainer;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        //Manages fragments
        mFragmentManager = getSupportFragmentManager();

        drawerContainer = (NavigationView) findViewById(R.id.drawer_container);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        // Set the list's click listener
        drawerContainer.setNavigationItemSelectedListener(new NavigationItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                myToolbar,  /* toolbar */
                R.string.app_name,  /* "open drawer" description */
                R.string.app_name  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(mDrawerToggle);

        Fragment f = null;
        Class fClass = CourseList.class;

        try {
            f = (Fragment) fClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        mFragmentManager.beginTransaction().replace(R.id.flContent, f).commit();
        setTitle("Course List");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        if (item.getItemId() == R.id.settings_button) {
            Intent i = new Intent(this, AccountSettings.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_actionbar, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }


    private class NavigationItemClickListener implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {

            // Create a new fragment and specify the fragment to show based on nav item clicked
            Fragment fragment = null;
            Class fragmentClass = null;
            switch(menuItem.getItemId()) {
                case R.id.nav_course_list:
                    fragmentClass = CourseList.class;
                    break;
                case R.id.nav_view_term:
                //    fragmentClass = SecondFragment.class;
                    break;
                case R.id.nav_manage_courses:
                 //   fragmentClass = ThirdFragment.class;
                    break;
                case R.id.nav_browse_courses:
                    fragmentClass = BrowseCourses.class;
                    break;
                default:
                    fragmentClass = CourseList.class;
            }

            if (fragmentClass != null) {
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Insert the fragment by replacing any existing fragment
                mFragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

                // Set action bar title
                setTitle(menuItem.getTitle());
                // Close the navigation drawer
                drawerLayout.closeDrawers();
            }
            return true;
        }
    }
}
