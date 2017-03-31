package com.qingfeng.music.ui.main.presenter;

import com.qingfeng.music.dao.Music;
import com.qingfeng.music.ui.main.contract.LocalMainContract;
import com.wgl.android.library.baserx.RxSubscriber;

import java.util.List;

/**
 * Created by Ganlin.Wu on 2016/10/13.
 */
public class LocalMainPresenter extends LocalMainContract.Presenter {
    @Override
    public void loadLocalMusics() {
        getRxManage().add(getModel().getLocalMusics().subscribe(new RxSubscriber<List<Music>>(getContext(), false) {
            @Override
            protected void _onNext(List<Music> musics) {
                getView().showMusicList(musics);
            }

            @Override
            protected void _onError(String message) {

            }
        }));
    }
}
