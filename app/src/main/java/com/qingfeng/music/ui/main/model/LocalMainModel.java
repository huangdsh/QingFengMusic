package com.qingfeng.music.ui.main.model;

import com.qingfeng.music.dao.Music;
import com.qingfeng.music.ui.main.contract.LocalMainContract;
import com.qingfeng.music.util.MusicManager;
import com.wgl.android.library.baserx.RxSchedulers;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Ganlin.Wu on 2016/10/13.
 */
public class LocalMainModel implements LocalMainContract.Model {

    @Override
    public Observable<List<Music>> getLocalMusics() {
        return Observable.create(new Observable.OnSubscribe<List<Music>>() {
            @Override
            public void call(Subscriber<? super List<Music>> subscriber) {
                subscriber.onNext(MusicManager.getInstance().getLocalMusicList());
                subscriber.onCompleted();
            }
        }).compose(RxSchedulers.<List<Music>>io_main());
    }
}
