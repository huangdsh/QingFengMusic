package com.qingfeng.music.ui.main.contract;

import com.qingfeng.music.dao.Music;
import com.wgl.android.library.base.BaseModel;
import com.wgl.android.library.base.BasePresenter;
import com.wgl.android.library.base.BaseView;

import java.util.List;

import rx.Observable;

/**
 * Created by Ganlin.Wu on 2016/10/13.
 */
public interface LocalMainContract {
    interface Model extends BaseModel {
        Observable<List<Music>> getLocalMusics();
    }

    interface View extends BaseView {
        void showMusicList(List<Music> musics);
    }

    abstract class Presenter extends BasePresenter<View, Model> {
        public abstract void loadLocalMusics();
    }
}
