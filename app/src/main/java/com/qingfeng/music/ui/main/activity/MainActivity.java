package com.qingfeng.music.ui.main.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;

import com.qingfeng.music.R;
import com.qingfeng.music.ui.main.fragment.InternetMusicFragment;
import com.qingfeng.music.ui.main.fragment.LocalMusicFragment;
import com.wgl.android.library.base.BaseActivity;
import com.wgl.android.library.baseadapter.BaseFragmentAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import cn.hugeterry.updatefun.UpdateFunGO;
import cn.hugeterry.updatefun.config.UpdateKey;

/**
 * Created by Ganlin.Wu on 2016/10/13.
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private static final String[] sTabTitles = new String[]{"本地音乐", "网络曲库"};
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    BaseFragmentAdapter mFragmentPagerAdapter;
    LocalMusicFragment mLocalMusicFragment;
    InternetMusicFragment mInternetMusicFragment;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        UpdateKey.API_TOKEN = "3a82dc592cab2914c70f65a91f624436";
        UpdateKey.APP_ID = "57fc9e6a959d69792c0000c0";
        UpdateFunGO.init(this);
        setToolBar(mToolbar, getString(R.string.app_name));
        for (String title : sTabTitles) {
            mTabLayout.addTab(mTabLayout.newTab().setText(title));
        }
        mLocalMusicFragment = new LocalMusicFragment();
        mInternetMusicFragment = new InternetMusicFragment();
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(mLocalMusicFragment);
        fragments.add(mInternetMusicFragment);
        mFragmentPagerAdapter = new BaseFragmentAdapter(getSupportFragmentManager(),
                fragments, Arrays.asList(sTabTitles));
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void setToolBar(Toolbar toolBar, CharSequence title) {
        super.setToolBar(toolBar, title);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolBar, R.string.open_content_drawer, R.string.close_content_drawer);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public static void startAction(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    /**
     * 监听返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateFunGO.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UpdateFunGO.onStop(this);
    }
}
