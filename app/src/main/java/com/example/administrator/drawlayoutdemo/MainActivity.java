package com.example.administrator.drawlayoutdemo;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private DrawerLayout mDrawerLayout;
    private ListView mListView;
    private ActionBar actionBar;
    private Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;

    private List<String> datas;
    private ArrayAdapter<String> mAdapter;

    private String title;
    private String[] cities = {"发现", "广州", "北京", "周口", "深圳",};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_drawer);
        initView();

        initDatas();

    }

    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.draw_layout);
        mListView = (ListView) findViewById(R.id.list_view);


    }

    private void initDatas() {
        actionBar = getSupportActionBar();
        title = actionBar.getTitle().toString();


        mDrawerToggle = new ActionBarDrawerToggle
                (MainActivity.this, mDrawerLayout, R.drawable.toggle2,R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
//                actionBar.setTitle(title);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

//                actionBar.setTitle("菜单");
                invalidateOptionsMenu();


            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        datas = new ArrayList<>();

        datas.addAll(Arrays.asList(cities));

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datas);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        setSelectedItem(0);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setSelectedItem(position);

    }
    public void setSelectedItem(int position) {
        if(position == 0)
        {
            FindFragment mFindFregment = FindFragment.newInstance(datas.get(position), datas.get(position));
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, mFindFregment).commit();

            mDrawerLayout.closeDrawer(mListView);
            title = "发现";
            actionBar.setTitle(title);
            return;
        }
        ContentFragment mContentFragment = ContentFragment.newInstance
                (datas.get(position), datas.get(position));

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, mContentFragment).commit();

        mDrawerLayout.closeDrawer(mListView);
        title = cities[position];
        actionBar.setTitle(title);
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isDrawOpen = mDrawerLayout.isDrawerOpen(mListView);
        menu.findItem(R.id.menu_search_web).setVisible(!isDrawOpen);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //屏蔽DrawToggle的点击事件
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == R.id.menu_search_web) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri uri = Uri.parse("http://www.baidu.com");
            intent.setData(uri);

            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        //将drawToggle与drawlayout状态同步
        mDrawerToggle.syncState();


    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);

    }


}
