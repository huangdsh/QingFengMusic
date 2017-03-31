package com.qingfeng.music.ui.billboard.presenter;

import android.text.TextUtils;

import com.qingfeng.music.Constant;
import com.qingfeng.music.bean.PlayBean;
import com.qingfeng.music.bean.SongRankBean;
import com.qingfeng.music.dao.Music;
import com.qingfeng.music.ui.billboard.contract.BillboardContract;
import com.wgl.android.library.baserx.RxSubscriber;
import com.wgl.android.library.commonutils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by Ganlin.Wu on 2016/10/14.
 */
public class BillboardPresenter extends BillboardContract.Presenter {
    @Override
    public void loadBillboardMusics(final String format, String callback, String from, String method, int type, int size, int offset) {
        getRxManage().add(getModel().getBillBoardMusics(format, callback, from, method, type, size, offset)
                .map(new Func1<SongRankBean, List<Music>>() {
                    @Override
                    public List<Music> call(final SongRankBean bean) {
                        Observable.create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(Subscriber<? super String> subscriber) {
                                subscriber.onNext(bean.getBillboard().getPic_s444());
                            }
                        }).subscribe(new RxSubscriber<String>(getContext(), true) {
                            @Override
                            protected void _onNext(String url) {
                                getView().showBackdrop(url);
                            }

                            @Override
                            protected void _onError(String message) {

                            }
                        });
                        List<SongRankBean.SongListBean> songList = bean.getSong_list();
                        List<Music> musics = new ArrayList<>();
                        for (SongRankBean.SongListBean song : songList) {
                            final Music music = new Music();
                            music.setId(Long.parseLong(song.getSong_id()));
                            music.setTitle(song.getTitle());
                            music.setArtist(song.getArtist_name());
                            music.setAlbum(song.getAlbum_title());
                            music.setAlbumId(Long.parseLong(song.getAlbum_id()));
                            music.setImageUrl(song.getPic_small());

                            getRxManage().add(getModel().getPlay(Constant.API_FORMAT, Constant.API_CALLBACK, Constant.API_FROM, Constant.API_METHOD_PLAY, song.getSong_id()).subscribe(new RxSubscriber<PlayBean>(getContext(), false) {
                                @Override
                                protected void _onNext(PlayBean playBean) {
                                    String url = playBean.getBitrate().getShow_link();
                                    if (TextUtils.isEmpty(url)) {
                                        url = playBean.getBitrate().getFile_link();
                                    }
                                    music.setPath(url);
                                }

                                @Override
                                protected void _onError(String message) {
                                    LogUtils.loge(message);
                                }
                            }));
                            music.setLrcUrl(song.getLrclink());
                            musics.add(music);
                        }
                        return musics;
                    }
                }).subscribe(new RxSubscriber<List<Music>>(getContext(), false) {
                    @Override
                    protected void _onNext(List<Music> musics) {
                        getView().showBillBoardMusics(musics);
                    }

                    @Override
                    protected void _onError(String message) {

                    }
                })
        );
    }
}
