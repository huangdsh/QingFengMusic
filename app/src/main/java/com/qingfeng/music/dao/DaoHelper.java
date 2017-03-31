package com.qingfeng.music.dao;

import com.qingfeng.music.App;

/**
 * Created by Ganlin.Wu on 2016/9/29.
 */
public class DaoHelper {

    private static DaoHelper instance;
    private DaoMaster.DevOpenHelper mDevOpenHelper;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    private DaoHelper() {
        mDevOpenHelper = new MyOpenHelper(App.getAppContext(), "app.db");
        mDaoMaster = new DaoMaster(mDevOpenHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
    }

    public static DaoHelper getInstance() {
        if (instance == null) {
            synchronized (DaoHelper.class) {
                if (instance == null) {
                    instance = new DaoHelper();
                }
            }
        }
        return instance;
    }

    public MusicDao getMusicDao() {
        return mDaoSession.getMusicDao();
    }
}
