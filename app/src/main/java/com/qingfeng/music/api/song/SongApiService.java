package com.qingfeng.music.api.song;


import com.qingfeng.music.bean.PlayBean;
import com.qingfeng.music.bean.SongDownloadBean;
import com.qingfeng.music.bean.SongRankBean;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Ganlin.Wu on 2016/9/22.
 */
public interface SongApiService {
    String BASE_URL = "http://tingapi.ting.baidu.com/";

    @GET("v1/restserver/ting")
    Observable<SongRankBean> getSongRank(@Query("format") String format, @Query("callback") String callback, @Query("from") String from,
                                         @Query("method") String method, @Query("type") int type, @Query("size") int size,
                                         @Query("offset") int offset);

    @GET("v1/restserver/ting")
    Observable<PlayBean> getPlay(@Query("format") String format, @Query("callback") String callback, @Query("from") String from,
                                 @Query("method") String method, @Query("songid") String songid);

    @GET("v1/restserver/ting")
    Observable<SongDownloadBean> getDownloadSong(@Query("format") String format, @Query("callback") String callback, @Query("from") String from,
                                                 @Query("method") String method, @Query("songid") long songid, @Query("bit") int bit, @Query("_t") long t);

}
