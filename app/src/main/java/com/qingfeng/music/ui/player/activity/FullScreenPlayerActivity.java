package com.qingfeng.music.ui.player.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.qingfeng.music.Constant;
import com.qingfeng.music.R;
import com.qingfeng.music.dao.Music;
import com.qingfeng.music.service.DownloadLrcService;
import com.qingfeng.music.service.PlayerService;
import com.qingfeng.music.service.connection.BaseServiceConnection;
import com.qingfeng.music.util.LrcUtil;
import com.qingfeng.music.util.Player;
import com.qingfeng.music.widget.lrc.LrcView;
import com.wgl.android.library.base.BaseActivity;
import com.wgl.android.library.base.BaseService;
import com.wgl.android.library.commonutils.LogUtils;

import butterknife.Bind;


public class FullScreenPlayerActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "FullScreen";
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.prev)
    ImageView mSkipPrev;
    @Bind(R.id.next)
    ImageView mSkipNext;
    @Bind(R.id.play_pause)
    ImageView mPlayPause;
    @Bind(R.id.startText)
    TextView mStart;
    @Bind(R.id.endText)
    TextView mEnd;
    @Bind(R.id.seekBar1)
    SeekBar mSeekbar;
    @Bind(R.id.line1)
    TextView mLine1;
    @Bind(R.id.line2)
    TextView mLine2;
    @Bind(R.id.line3)
    TextView mLine3;
    @Bind(R.id.progressBar1)
    ProgressBar mLoading;
    @Bind(R.id.controllers)
    View mControllers;
    @Bind(R.id.lrc_view)
    LrcView mLrcView;
    Drawable mPauseDrawable;
    Drawable mPlayDrawable;
    @Bind(R.id.background_image)
    ImageView mBackgroundImage;

    private BaseServiceConnection<PlayerService> mPlayServiceConnection;
    private BaseServiceConnection<DownloadLrcService> mDownloadLrcServiceConnection;

    private BroadcastReceiver mReceiver;


    @Override
    public int getLayoutId() {
        return R.layout.activity_full_player;
    }

    @Override
    public void initView() {
        translucentStatusBar();
        setToolBar(mToolbar, "");
        initLrcView();
        mPauseDrawable = ContextCompat.getDrawable(this, R.drawable.uamp_ic_pause_white_48dp);
        mPlayDrawable = ContextCompat.getDrawable(this, R.drawable.uamp_ic_play_arrow_white_48dp);
        mBackgroundImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_img_menuback_cool));
        mSeekbar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mPlayPause.setOnClickListener(this);
        mSkipPrev.setOnClickListener(this);
        mSkipNext.setOnClickListener(this);
        mPlayServiceConnection = new BaseServiceConnection<>();
        mDownloadLrcServiceConnection = new BaseServiceConnection<>();
        bindService(PlayerService.class, mPlayServiceConnection);
        bindService(DownloadLrcService.class, mDownloadLrcServiceConnection);
        mReceiver = new MusicChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.RECEIVER_MUSIC_CHANGE);
        intentFilter.addAction(Constant.RECEIVER_DOWNLOAD_LYRIC_SUCCESS);
        registerReceiver(mReceiver, intentFilter);
        updatePlaybackState();
        mSeekHandler.post(mSeekThread);
    }

    private <T extends BaseService> void bindService(Class<T> cls, BaseServiceConnection<T> connection) {
        Intent intent = new Intent(this, cls);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }


    private void initLrcView() {
        mLrcView.setOnClickListener(this);
        mLrcView.setOnLrcSeekChangeListener(new LrcView.OnLrcSeekChangeListener() {
            @Override
            public void onLrcSeekChanged(int msec) {
                Player.getPlayer().seekTo(msec);
            }
        });
        Music music = Player.getPlayer().getMusic();
        if (music != null)
            mLrcView.setLrc(LrcUtil.resolve(music));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unbindService(mPlayServiceConnection);
        unbindService(mDownloadLrcServiceConnection);
        LogUtils.logd("onDestroy");
        mSeekHandler.removeCallbacks(mSeekThread);
        mSeekHandler = null;
    }

    private void updatePlaybackState() {
        Player player = Player.getPlayer();
        if (player == null) return;
        Music music = player.getMusic();
        if (music == null) return;
        mToolbar.setTitle(music.getTitle());
        mLrcView.setLrc(LrcUtil.resolve(music));
        switch (player.getState()) {
            case Player.STATE_PLAY:
                mLoading.setVisibility(View.INVISIBLE);
                mPlayPause.setVisibility(View.VISIBLE);
                mPlayPause.setImageDrawable(mPauseDrawable);
                mControllers.setVisibility(View.VISIBLE);
                break;
            case Player.STATE_PAUSE:
                mControllers.setVisibility(View.VISIBLE);
                mLoading.setVisibility(View.INVISIBLE);
                mPlayPause.setVisibility(View.VISIBLE);
                mPlayPause.setImageDrawable(mPlayDrawable);
                break;
            case Player.STATE_STOP:
                mLoading.setVisibility(View.INVISIBLE);
                mPlayPause.setVisibility(View.VISIBLE);
                mPlayPause.setImageDrawable(mPlayDrawable);
                break;
        }
        mSkipNext.setVisibility(View.VISIBLE);
        mSkipPrev.setVisibility(View.VISIBLE);
    }


    Runnable mSeekThread = new Runnable() {
        @Override
        public void run() {
            Message msg = Message.obtain();
            msg.arg1 = Player.getPlayer().getDuration();//最大值
            msg.arg2 = Player.getPlayer().getCurrentPosition();//进度
            mSeekHandler.sendMessage(msg);
        }
    };

    Handler mSeekHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                mSeekbar.setMax(msg.arg1);
                mSeekbar.setProgress(msg.arg2);
                mLrcView.seekLrcToTime(msg.arg2);
                mStart.setText(DateUtils.formatElapsedTime(msg.arg2 / 1000));
                mEnd.setText(DateUtils.formatElapsedTime(msg.arg1 / 1000));
            }
            mSeekHandler.postDelayed(mSeekThread, 100);
        }
    };


    SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Player.getPlayer().seekTo(seekBar.getProgress());
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Player.getPlayer().seekTo(seekBar.getProgress());
        }
    };

    public static void startAction(Context context) {
        Intent intent = new Intent(context, FullScreenPlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Player player = Player.getPlayer();
        Music music = player.getMusic();
        PlayerService playerService = mPlayServiceConnection.getService();
        DownloadLrcService downloadLrcService = mDownloadLrcServiceConnection.getService();
        switch (v.getId()) {
            case R.id.play_pause:
                if (player.getState() == Player.STATE_PLAY) {
                    playerService.pause();
                } else if (player.getState() == Player.STATE_PAUSE) {
                    playerService.replay();
                } else {
                    playerService.play();
                }
                break;
            case R.id.next:
                playerService.next();
                break;
            case R.id.prev:
                playerService.previous();
                break;
            case R.id.lrc_view:
                Log.d(TAG, "onClick: " + music.getLrcUrl());
                if (!TextUtils.isEmpty(music.getLrcUrl()))
                    downloadLrcService.downloadLrc(music);
                break;
        }
    }

    private class MusicChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.RECEIVER_MUSIC_CHANGE.equals(intent.getAction())) {
                updatePlaybackState();
            } else if (Constant.RECEIVER_DOWNLOAD_LYRIC_SUCCESS.equals(intent.getAction())) {
                Player player = Player.getPlayer();
                Music music = player.getMusic();
                mLrcView.setLrc(LrcUtil.resolve(music));
            }
        }
    }
}
