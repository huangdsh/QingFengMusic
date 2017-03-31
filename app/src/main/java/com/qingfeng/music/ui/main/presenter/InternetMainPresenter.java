package com.qingfeng.music.ui.main.presenter;

import com.qingfeng.music.ui.main.contract.InternetMainContract;
import com.wgl.android.library.baserx.RxSubscriber;

import java.util.List;

/**
 * Created by Ganlin.Wu on 2016/10/14.
 */
public class InternetMainPresenter extends InternetMainContract.Presenter {
    @Override
    public void loadMusicCategories() {
        getRxManage().add(getModel().getMusicCategories().subscribe(new RxSubscriber<List<String>>(getContext(), false) {
            @Override
            protected void _onNext(List<String> strings) {
                getView().showMusicCategories(strings);
            }

            @Override
            protected void _onError(String message) {

            }
        }));
    }

    @Override
    public void loadMusicType(final String title) {
        getRxManage().add(getModel().getMusicType(title).subscribe(new RxSubscriber<Integer>(getContext(), false) {
            @Override
            protected void _onNext(Integer type) {
                getView().startBillBoardAction(title, type);
            }

            @Override
            protected void _onError(String message) {

            }
        }));
    }
}
