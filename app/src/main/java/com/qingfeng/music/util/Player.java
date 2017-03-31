package com.qingfeng.music.util;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.qingfeng.music.Constant;
import com.qingfeng.music.dao.Music;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ganlin.Wu on 2016/9/29.
 */
public class Player {
    private static final String TAG = "Player";
    public static final int STATE_STOP = 0;
    public static final int STATE_PAUSE = 1;
    public static final int STATE_PLAY = 2;

    public static final int MODE_REPEAT_SINGLE = 0;
    public static final int MODE_REPEAT_ALL = 1;
    public static final int MODE_SEQUENCE = 2;
    public static final int MODE_RANDOM = 3;

    private static Player player;
    private List<Music> mMusicList = new ArrayList<>();
    private MediaPlayer mMediaPlayer;

    private int mState = STATE_STOP;
    private int mPosition;
    private Context mContext;
    private int mMode = MODE_SEQUENCE;

    private Player() {

    }

    public static Player getPlayer() {
        if (player == null) {
            synchronized (Player.class) {
                if (player == null) {
                    player = new Player();
                }
            }
        }
        return player;
    }

    public List<Music> getList() {
        return mMusicList;
    }

    public int getPosition() {
        return mPosition;
    }

    public Music getMusic() {
        Music music = null;
        if (mPosition >= mMusicList.size()) {
            music = new Music();
            music.setTitle(Constant.DEFAULT_MUSIC_TITLE);
            music.setArtist(Constant.DEFAULT_MUSIC_ARTIST);
        } else {
            music = mMusicList.get(mPosition);
        }
        return music;
    }


    public int getState() {
        return mState;
    }

    public int getMode() {
        return mMode;
    }

    // 获取播放的音乐文件总时间长度
    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }


    // 获取当前播放音乐时间点
    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    // 将音乐播放跳转到某一时间点,以毫秒为单位
    public void seekTo(int msec) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(msec);
        }
    }

    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mContext.sendBroadcast(new Intent(Constant.RECEIVER_MUSIC_CHANGE));
                Player.this.onCompletion();
            }
        });
        mMediaPlayer.reset();
    }

    public Music play(Context context, List<Music> list, int position) {


        if (mMediaPlayer == null) {
            initMediaPlayer();
        }

        try {
            Log.d(TAG, "play: "+list.get(position).getPath());
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(list.get(position).getPath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mMusicList = list;
            mPosition = position;
            mContext = context;
            mState = STATE_PLAY;
            mContext.sendBroadcast(new Intent(Constant.RECEIVER_MUSIC_CHANGE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mMusicList.get(mPosition);
    }

    private Music onCompletion() {
        Music music = null;
        switch (mMode) {
            case MODE_REPEAT_SINGLE:
                //单曲播放
                stop();
                break;
            case MODE_REPEAT_ALL:
                //单曲循环
                music = play(mContext, mMusicList, mPosition);
                break;
            case MODE_SEQUENCE:
                //列表循环
                music = play(mContext, mMusicList, (mPosition + 1) % mMusicList.size());
                break;
            case MODE_RANDOM:
                //随机循环
                music = play(mContext, mMusicList, (int) Math.random() * mMusicList.size());
                break;
            default:
                break;
        }
        return music;
    }

    public void stop() {
        if (mState != STATE_STOP) {
            mMediaPlayer.reset();
            mState = STATE_STOP;
            mContext.sendBroadcast(new Intent(Constant.RECEIVER_MUSIC_CHANGE));
        }
    }

    public void pause() {
        if (mState != STATE_PAUSE) {
            mMediaPlayer.pause();
            mState = STATE_PAUSE;
            mContext.sendBroadcast(new Intent(Constant.RECEIVER_MUSIC_CHANGE));
        }
    }

    public Music replay() {
        if (mState != STATE_PLAY) {
            mMediaPlayer.start();
            mState = STATE_PLAY;
            mContext.sendBroadcast(new Intent(Constant.RECEIVER_MUSIC_CHANGE));
        }
        return mMusicList.get(mPosition);
    }


    public void destroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mState = STATE_STOP;
        }
    }

    public Music next(Context context) {
        Music music = null;
        if (mMusicList.isEmpty()) {
            destroy();
        } else {
            mMediaPlayer.reset(); // 停止上一首
            mPosition = (mPosition + 1) % mMusicList.size();
            play(mContext, mMusicList, mPosition);
            music = mMusicList.get(mPosition);
        }
        return music;
    }

    public Music previous(Context context) {
        Music music = null;
        if (mMusicList.isEmpty()) {
            destroy();
        } else {
            mMediaPlayer.reset(); // 停止上一首
            mPosition = (mPosition - 1) % mMusicList.size();
            play(mContext, mMusicList, mPosition);
            music = mMusicList.get(mPosition);
        }
        return music;
    }


}
