package com.qingfeng.music.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.qingfeng.music.dao.Music;
import com.qingfeng.music.receiver.MediaNotificationManager;
import com.qingfeng.music.util.MusicManager;
import com.qingfeng.music.util.Player;
import com.wgl.android.library.base.BaseService;

import java.util.List;

/**
 * Created by Ganlin.Wu on 2016/9/29.
 */
public class PlayerService extends BaseService {
    private static final String TAG = "PlayerService";
    private SystemReceiver mReceiver;
    private Player mPlayer;
    private MediaNotificationManager mMediaNotificationManager;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
        mReceiver = new SystemReceiver();
        mPlayer = Player.getPlayer();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(mReceiver, intentFilter);
        mMediaNotificationManager = new MediaNotificationManager(this);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        mPlayer.destroy();
        unregisterReceiver(mReceiver);
        mMediaNotificationManager.stopNotification();
    }

    /**
     * 播放
     *
     * @param list
     * @param position
     * @return
     */
    public Music play(List<Music> list, int position) {
        Log.d(TAG, "play: " + list);
        Log.d(TAG, "play: " + position);
        Music music = null;
        if (list == null || list.isEmpty()) {
            play();
        } else {
            music = mPlayer.play(this, list, position);
        }
        mMediaNotificationManager.startNotification();
        return music;
    }

    /**
     * 默认播放
     *
     * @return
     */
    public Music play() {
        Music music = null;
        if (MusicManager.getInstance().getLocalMusicList() == null || MusicManager.getInstance().getLocalMusicList().isEmpty()) {

        } else {
            music = mPlayer.play(this, MusicManager.getInstance().getLocalMusicList(), 0);
        }
        return music;
    }

    /**
     * 暂停
     */
    public void pause() {
        mPlayer.pause();
    }

    /**
     * 正在暂停，调用后开始继续播放
     */
    public void replay() {
        mPlayer.replay();
    }

    /**
     * 下一首
     *
     * @return
     */
    public Music next() {
        Music music = null;
        if (mPlayer.getState() == Player.STATE_STOP || mPlayer.getList().isEmpty()) {
            play();
        } else {
            music = mPlayer.next(this);
        }
        return music;
    }

    /**
     * 上一首
     *
     * @return
     */
    public Music previous() {
        Music music = null;
        if (mPlayer.getState() == Player.STATE_STOP || mPlayer.getList().isEmpty()) {
            play();
        } else {
            music = mPlayer.previous(this);
        }
        return music;
    }

    /**
     * 打电话时暂停播放
     */
    public class SystemReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 如果是打电话
            if (Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())) {
                pause();
            } else {
                // 如果是来电
                TelephonyManager tm = (TelephonyManager) context
                        .getSystemService(Service.TELEPHONY_SERVICE);
                switch (tm.getCallState()) {
                    // 响铃
                    case TelephonyManager.CALL_STATE_RINGING:
                        pause();
                        break;
                    // 摘机
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        pause();
                        break;
                    // 空闲
                    case TelephonyManager.CALL_STATE_IDLE:
                        replay();
                        break;
                }
            }
        }
    }
}
