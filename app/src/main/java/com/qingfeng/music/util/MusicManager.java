package com.qingfeng.music.util;

import com.qingfeng.music.dao.DaoHelper;
import com.qingfeng.music.dao.Music;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ganlin.Wu on 2016/9/25.
 */
public class MusicManager {
    private List<Music> localMusicList;
    private static MusicManager instance;

    private MusicManager() {

    }

    public static MusicManager getInstance() {
        if (instance == null) {
            synchronized (MusicManager.class) {
                if (instance == null) {
                    instance = new MusicManager();
                }
            }
        }
        return instance;
    }

    public synchronized void loadLocalMusicList() {
        if (localMusicList == null) {
            localMusicList = new ArrayList<>();
        }
        localMusicList.clear();
        localMusicList = DaoHelper.getInstance().getMusicDao().loadAll();

    }

    public List<Music> getLocalMusicList() {
        return localMusicList == null ? new ArrayList<Music>() : localMusicList;
    }

    public Music getMusicById(long id) {
        for (Music music : localMusicList) {
            if (music.getId() == id) {
                return music;
            }
        }
        return null;
    }

}
