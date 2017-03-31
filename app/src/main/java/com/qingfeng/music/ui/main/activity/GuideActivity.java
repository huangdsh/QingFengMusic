package com.qingfeng.music.ui.main.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qingfeng.music.R;
import com.qingfeng.music.util.SharedPreferencesUtil;
import com.wgl.android.library.base.BaseActivity;
import com.wgl.android.library.baseadapter.BaseViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class GuideActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "GuideActivity";
    @Bind(R.id.guide_viewpager)
    ViewPager mViewPager;
    @Bind(R.id.points_container)
    LinearLayout mPointsContainer;
    BaseViewPagerAdapter mSimpleViewPagerAdapter;
    private final int[] images = {R.drawable.welcome_1, R.drawable.welcome_2, R.drawable.welcome_3, R.drawable.welcome_4, R.drawable.welcome_1};

    @Override
    public int getLayoutId() {
        return R.layout.activity_guide;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        List<View> views = new ArrayList<>();
        for (int i = 0; i < images.length; i++) {
            if (i != images.length - 1) {
                View view = new View(this);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                view.setLayoutParams(params);
                view.setBackgroundResource(images[i]);
                views.add(view);
            } else {
                View view = View.inflate(this, R.layout.layout_guide_last, null);
                view.setBackgroundResource(images[i]);
                View button = view.findViewById(R.id.guide_btn);
                button.setOnClickListener(this);
                views.add(view);
            }
        }

        mSimpleViewPagerAdapter = new BaseViewPagerAdapter(views);
        mViewPager.setAdapter(mSimpleViewPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                onPageIndicatorSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        onPageIndicatorSelected(0);
        scanMusic();
    }

    private void onPageIndicatorSelected(int position) {
        for (int i = 0; i < mPointsContainer.getChildCount(); i++) {
            View view = mPointsContainer.getChildAt(i);
            view.setEnabled(false);
        }
        mPointsContainer.getChildAt(position).setEnabled(true);
    }

    public static void startAction(Context context) {
        Intent intent = new Intent(context, GuideActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.guide_btn) {
            SharedPreferencesUtil.put(GuideActivity.this, SharedPreferencesUtil.KEY_IS_FIRST, false);
            MainActivity.startAction(GuideActivity.this);
            finish();
        }
    }

    private void scanMusic() {
       /* Log.e(TAG, "scanMusic: before ScanMusicService");
        Intent intent = new Intent(this, ScanMusicService.class);
        startService(intent);*/
    }
}
