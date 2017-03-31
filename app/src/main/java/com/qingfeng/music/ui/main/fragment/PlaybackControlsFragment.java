package com.qingfeng.music.ui.main.fragment;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qingfeng.music.Constant;
import com.qingfeng.music.R;
import com.qingfeng.music.dao.Music;
import com.qingfeng.music.service.PlayerService;
import com.qingfeng.music.service.connection.BaseServiceConnection;
import com.qingfeng.music.ui.player.activity.FullScreenPlayerActivity;
import com.qingfeng.music.util.Player;
import com.wgl.android.library.base.BaseFragment;
import com.wgl.android.library.commonutils.LogUtils;

import butterknife.Bind;

/**
 * Created by Ganlin.Wu on 2016/9/28.
 */
public class PlaybackControlsFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "Playback";
    private BroadcastReceiver mReceiver;
    @Bind(R.id.play_pause)
    ImageButton mPlayPause;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.artist)
    TextView mSubtitle;
    @Bind(R.id.extra_info)
    TextView mExtraInfo;
    @Bind(R.id.album_art)
    ImageView mAlbumArt;
    private ObjectAnimator mAnimator;
    private Animation mAnimation;
    private BaseServiceConnection<PlayerService> mPlayerServiceConnection;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_playback_controls;
    }

    @Override
    protected void initView() {
       /* mAnimator = ObjectAnimator.ofFloat(mAlbumArt, "rotation", 0, 359);
        mAnimator.setDuration(10000);
        mAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());*/
        mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setFillAfter(true);
        Intent intent = new Intent(getContext(), PlayerService.class);
        mPlayerServiceConnection = new BaseServiceConnection<>();
        getContext().bindService(intent, mPlayerServiceConnection, Context.BIND_AUTO_CREATE);
        mReceiver = new MusicChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(Constant.RECEIVER_MUSIC_CHANGE);
        getContext().registerReceiver(mReceiver, intentFilter);
        rootView.setOnClickListener(this);
        mPlayPause.setOnClickListener(this);
        onPlaybackStateChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mReceiver);
        getContext().unbindService(mPlayerServiceConnection);
    }

    @Override
    public void onClick(View v) {
        if (v == rootView) {
            FullScreenPlayerActivity.startAction(getActivity());
        } else if (v.getId() == R.id.play_pause) {
            PlayerService playerService = mPlayerServiceConnection.getService();
            Player player = Player.getPlayer();
            if (player.getState() == Player.STATE_PLAY) {
                playerService.pause();
            } else if (player.getState() == Player.STATE_PAUSE) {
                playerService.replay();
            } else {
                playerService.play();
            }

        }

    }

    private class MusicChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");
            if (Constant.RECEIVER_MUSIC_CHANGE.equals(intent.getAction())) {
                onPlaybackStateChanged();
            }
        }
    }


    private void onPlaybackStateChanged() {
        Player player = Player.getPlayer();
        Music music = player.getMusic();
        if (music == null) {
            mAlbumArt.setImageResource(R.drawable.ic_launcher);
            mTitle.setText(Constant.DEFAULT_MUSIC_TITLE);
            mSubtitle.setText(Constant.DEFAULT_MUSIC_ARTIST);
            return;
        }
        mTitle.setText(music.getTitle());
        mSubtitle.setText(music.getArtist());
        Glide.with(getContext()).load(music.getImageUrl()).error(R.drawable.ic_launcher).into(mAlbumArt);
        LogUtils.logd(TAG, "onPlaybackStateChanged: " + player.getState());
        switch (player.getState()) {
            case Player.STATE_PAUSE:
                mPlayPause.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_play_arrow_black_36dp));
                //mAnimator.end();
                //mAlbumArt.clearAnimation();
                break;
            case Player.STATE_PLAY:
                mPlayPause.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_pause_black_36dp));
                // mAnimator.start();
                //mAlbumArt.startAnimation(mAnimation);
                break;
            case Player.STATE_STOP:
                mPlayPause.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_play_arrow_black_36dp));
                //mAnimator.end();
                //mAlbumArt.clearAnimation();
                break;
        }
    }

}
