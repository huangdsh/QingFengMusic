package com.qingfeng.music.api.song;


import com.qingfeng.music.api.Api;

/**
 * Created by Ganlin.Wu on 2016/9/21.
 */
public class SongApi extends Api<SongApiService> {

    private static SongApi sInstance;

    private SongApi() {
        super(SongApiService.BASE_URL);
    }

    public static SongApi getInstance() {
        if (sInstance == null) {
            synchronized (SongApi.class) {
                if (sInstance == null) {
                    sInstance = new SongApi();
                }
            }
        }
        return sInstance;
    }


}
