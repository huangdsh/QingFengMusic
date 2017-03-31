package com.qingfeng.music.service;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.qingfeng.music.Constant;
import com.qingfeng.music.dao.Music;
import com.qingfeng.music.dao.DaoHelper;
import com.qingfeng.music.util.MusicManager;
import com.qingfeng.music.util.SharedPreferencesUtil;
import com.wgl.android.library.base.BaseService;

/**
 * Created by Ganlin.Wu on 2016/9/29.
 */
public class ScanMusicService extends BaseService {
    private static final String TAG = "ScanMusicService";

    public void scanMusic() {
        boolean result = true;
        if (isFirst()) {
            ContentResolver resolver = getContentResolver();
            Cursor cursor = resolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                    null);
            if (cursor == null) {
                result = false;
            } else {
                DaoHelper.getInstance().getMusicDao().deleteAll();
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                    long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                    int size = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                    Music music = new Music();
                    music.setId(id);
                    music.setAlbum(album);
                    music.setAlbumId(albumId);
                    music.setArtist(artist);
                    music.setDisplayName(name);
                    music.setDuration(duration);
                    music.setTitle(title);
                    music.setPath(path);
                    music.setSize(size);
                    Log.e(TAG, "scanMusic: " + music);
                    DaoHelper.getInstance().getMusicDao().insert(music);
                }
                cursor.close();
                result = true;
            }
        }
        MusicManager.getInstance().loadLocalMusicList();
        SharedPreferencesUtil.put(this, SharedPreferencesUtil.KEY_IS_FIRST, false);
        Intent intent = new Intent();
        String action = result ? Constant.RECEIVER_MUSIC_SCAN_SUCCESS : Constant.RECEIVER_MUSIC_SCAN_FAIL;
        intent.setAction(action);
        sendBroadcast(intent);
    }

    private boolean isFirst() {
        return SharedPreferencesUtil.get(this, SharedPreferencesUtil.KEY_IS_FIRST, true);
    }


}
