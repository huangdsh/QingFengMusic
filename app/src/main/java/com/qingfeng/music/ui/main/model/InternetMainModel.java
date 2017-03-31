package com.qingfeng.music.ui.main.model;

import com.qingfeng.music.ui.main.contract.InternetMainContract;
import com.wgl.android.library.baserx.RxSchedulers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Ganlin.Wu on 2016/10/14.
 */
public class InternetMainModel implements InternetMainContract.Model {
    private Map<String, Integer> mDataMap = new LinkedHashMap<String, Integer>() {{
        put("新歌榜", 1);
        put("热歌榜", 2);
        put("欧美金曲榜", 21);
        put("经典老歌榜", 22);
        put("情歌对唱榜", 23);
        put("网络歌曲榜", 25);
    }};

    @Override
    public Observable<List<String>> getMusicCategories() {

        return Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                List<String> titles = new ArrayList<String>();
                titles.addAll(mDataMap.keySet());
                subscriber.onNext(titles);
                subscriber.onCompleted();
            }
        }).compose(RxSchedulers.<List<String>>io_main());
    }

    @Override
    public Observable<Integer> getMusicType(final String title) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(mDataMap.get(title));
                subscriber.onCompleted();
            }
        }).compose(RxSchedulers.<Integer>io_main());
    }

}
