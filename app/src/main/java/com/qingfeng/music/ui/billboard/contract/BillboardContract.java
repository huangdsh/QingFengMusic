package com.qingfeng.music.ui.billboard.contract;

import com.qingfeng.music.bean.PlayBean;
import com.qingfeng.music.bean.SongRankBean;
import com.qingfeng.music.dao.Music;
import com.wgl.android.library.base.BaseModel;
import com.wgl.android.library.base.BasePresenter;
import com.wgl.android.library.base.BaseView;

import java.util.List;

import rx.Observable;

/**
 * Created by Ganlin.Wu on 2016/10/14.
 */
public interface BillboardContract {
    interface Model extends BaseModel {
        Observable<SongRankBean> getBillBoardMusics(String format, String callback, String from, String method, int type, int size, int offset);

        Observable<PlayBean> getPlay(String format, String callback, String from, String method, String songid);
    }

    interface View extends BaseView {

        void showBackdrop(String url);

        void showBillBoardMusics(List<Music> musics);
    }

    abstract class Presenter extends BasePresenter<View, Model> {
        public abstract void loadBillboardMusics(String format, String callback, String from, String method, int type, int size, int offset);
    }
}
