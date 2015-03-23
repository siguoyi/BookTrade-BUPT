package com.bupt.booktrade.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bupt.booktrade.MyApplication;
import com.bupt.booktrade.R;
import com.bupt.booktrade.adapter.NavDrawerListAdapter;
import com.bupt.booktrade.fragment.AboutFragment;
import com.bupt.booktrade.fragment.MessageFragment;
import com.bupt.booktrade.fragment.PostsListFragment;
import com.bupt.booktrade.fragment.SettingFragment;
import com.bupt.booktrade.fragment.model.NavDrawerItem;
import com.bupt.booktrade.utils.LogUtils;
import com.bupt.booktrade.utils.ToastUtils;

import java.util.ArrayList;


public class MainActivity extends BaseActivity {
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerLinear;
    private LinearLayout mDrawerUserHeader;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mTitle;

    // used to store app title
    private CharSequence mDrawerTitle;

    // slide menu items
    private String[] navMenuTitles;

    private static final int PERSONAL_HOME_LIST_FRAGMENT = -1;
    private static final int POSTS_LIST_FRAGMENT = 0;
    private static final int NEW_POST_FRAGMENT = 1;
    private static final int MESSAGE_FRAGMENT = 2;
    private static final int SETTING_FRAGMENT = 3;
    private static final int ABOUT_FRAGMENT = 4;

    private int drawerPosition = POSTS_LIST_FRAGMENT;//默认显示首页

    private boolean doubleBackToExitPressedOnce = false;

    private Fragment fragment = null;
    private PostsListFragment postsListFragment = new PostsListFragment();
    private MessageFragment messageFragment = new MessageFragment();
    private SettingFragment settingFragment = new SettingFragment();
    private AboutFragment aboutFragment = new AboutFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogUtils.i(TAG, "onCreate");
        mTitle = mDrawerTitle = getTitle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLinear = (LinearLayout) findViewById(R.id.drawer_linear_layout);
        mDrawerUserHeader = (LinearLayout) findViewById(R.id.drawer_user_header);
        mDrawerList = (ListView) findViewById(R.id.drawer_list_item);

        initNavDrawerItems();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        mDrawerUserHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //drawerPosition = PERSONAL_HOME_LIST_FRAGMENT;
                //displayView(drawerPosition);
                Intent intent = new Intent(MainActivity.this, PersonalHomeActivity.class);
                startActivity(intent);
                mDrawerLayout.closeDrawer(mDrawerLinear);
            }
        });

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(drawerPosition);
        }
    }


    private void initNavDrawerItems() {
        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        TypedArray navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);
        ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<>();

        // Posts List
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // New Post
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Message
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Setting
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        // About
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        // Recycle the typed array
        navMenuIcons.recycle();

        // setting the nav drawer list adapter
        NavDrawerListAdapter adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        doubleBackToExitPressedOnce = false;
        displayView(drawerPosition);
    }

    /**
     * 双击back键退出
     */
    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(mDrawerLinear)) {
            mDrawerLayout.closeDrawer(mDrawerLinear);
        } else {
            if (doubleBackToExitPressedOnce) {
                ToastUtils.clearToast();
                MyApplication.getMyApplication().exit();
                finish();
                super.onBackPressed();
                return;

            }
            this.doubleBackToExitPressedOnce = true;
            ToastUtils.showToast(this, R.string.one_more_back, Toast.LENGTH_SHORT);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
      //  switch (item.getItemId()) {
        //    case R.id.action_settings:
         //       return true;
          //  default:
                return super.onOptionsItemSelected(item);
        //}
    }

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerLinear);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Displaying fragment view for selected nav drawer list item
     */
    private void displayView(int position) {
        // update the main content by replacing fragments

        switch (position) {
            case POSTS_LIST_FRAGMENT:
                fragment = postsListFragment;
                break;
            case NEW_POST_FRAGMENT:
                Intent intent = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(intent);
                mDrawerLayout.closeDrawer(mDrawerLinear);
                break;
            case MESSAGE_FRAGMENT:
                fragment = messageFragment;
                break;
            case SETTING_FRAGMENT:
                fragment = settingFragment;
                break;
            case ABOUT_FRAGMENT:
                fragment = aboutFragment;
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_container_main, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);

            mDrawerLayout.closeDrawer(mDrawerLinear);
        } else {
            // error in creating fragment
            LogUtils.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
            drawerPosition = position;
        }
    }

}
