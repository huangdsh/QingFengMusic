package com.qingfeng.music.ui.main.contract;

import com.wgl.android.library.base.BaseModel;
import com.wgl.android.library.base.BasePresenter;
import com.wgl.android.library.base.BaseView;

import java.util.List;

import rx.Observable;

/**
 * Created by Ganlin.Wu on 2016/10/14.
 */
public interface InternetMainContract {
    interface Model extends BaseModel {
        Observable<List<String>> getMusicCategories();

        Observable<Integer> getMusicType(String title);
    }

    interface View extends BaseView {
        void showMusicCategories(List<String> musics);

        void startBillBoardAction(String title, int type);
    }

    abstract class Presenter extends BasePresenter<View, Model> {
        public abstract void loadMusicCategories();

        public abstract void loadMusicType(String title);
    }
}
