package com.qingfeng.music.ui.billboard.model;

import com.qingfeng.music.api.song.SongApi;
import com.qingfeng.music.api.song.SongApiService;
import com.qingfeng.music.bean.PlayBean;
import com.qingfeng.music.bean.SongRankBean;
import com.qingfeng.music.ui.billboard.contract.BillboardContract;
import com.wgl.android.library.baserx.RxSchedulers;

import rx.Observable;

/**
 * Created by Ganlin.Wu on 2016/10/14.
 */
public class BillboardModel implements BillboardContract.Model {
    private SongApiService mSongApiService;

    public BillboardModel() {
        mSongApiService = SongApi.getInstance().getApiService();
    }


    @Override
    public Observable<SongRankBean> getBillBoardMusics(String format, String callback, String from, String method, int type, int size, int offset) {
        return mSongApiService.getSongRank(format, callback, from, method, type, size, offset).compose(RxSchedulers.<SongRankBean>io_main());
    }

    @Override
    public Observable<PlayBean> getPlay(String format, String callback, String from, String method, String songid) {
        return mSongApiService.getPlay(format, callback, from, method, songid).compose(RxSchedulers.<PlayBean>io_main());
    }
}
