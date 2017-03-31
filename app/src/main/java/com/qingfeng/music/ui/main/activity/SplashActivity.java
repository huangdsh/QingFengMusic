package com.qingfeng.music.ui.main.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.qingfeng.music.Constant;
import com.qingfeng.music.R;
import com.qingfeng.music.service.ScanMusicService;
import com.qingfeng.music.service.connection.BaseServiceConnection;
import com.wgl.android.library.base.BaseActivity;
import com.wgl.android.library.commonutils.EasyPermissions;

import java.util.List;

import butterknife.Bind;

public class SplashActivity extends BaseActivity implements EasyPermissions.PermissionCallback {
    private static final String TAG = "SplashActivity";
    @Bind(R.id.logo)
    ImageView mImageView;
    @Bind(R.id.name)
    TextView mTextView;
    private BaseServiceConnection<ScanMusicService> mScanMusicServiceConnection;
    private String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int PERMS_REQUEST_CODE = 100;
    private BroadcastReceiver mMusicScanReceiver;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initView() {
        translucentStatusBar();
        Intent intent = new Intent(this, ScanMusicService.class);
        mScanMusicServiceConnection = new BaseServiceConnection<>();
        bindService(intent, mScanMusicServiceConnection, Context.BIND_AUTO_CREATE);
        mMusicScanReceiver = new MusicScanReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.RECEIVER_MUSIC_SCAN_SUCCESS);
        registerReceiver(mMusicScanReceiver, intentFilter);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0.3f, 1f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0.3f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0.3f, 1f);
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofPropertyValuesHolder(mImageView, alpha, scaleX, scaleY);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofPropertyValuesHolder(mTextView, alpha, scaleX, scaleY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator1, objectAnimator2);
        animatorSet.setInterpolator(new AccelerateInterpolator());
        animatorSet.setDuration(2000);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!EasyPermissions.hasPermissions(SplashActivity.this, perms)) {
                    EasyPermissions.requestPermissions(SplashActivity.this, "扫描本地歌曲需要读取权限", PERMS_REQUEST_CODE, perms);
                } else {
                    mScanMusicServiceConnection.getService().scanMusic();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mScanMusicServiceConnection);
        unregisterReceiver(mMusicScanReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(PERMS_REQUEST_CODE, permissions, grantResults, SplashActivity.this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        mScanMusicServiceConnection.getService().scanMusic();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        MainActivity.startAction(SplashActivity.this);
        finish();
    }

    private class MusicScanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.RECEIVER_MUSIC_SCAN_SUCCESS.equals(intent.getAction())) {
                MainActivity.startAction(SplashActivity.this);
                finish();
            }
        }
    }
}
