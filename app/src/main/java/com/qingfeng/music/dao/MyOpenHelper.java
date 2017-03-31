package com.qingfeng.music.dao;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

/**
 * Created by Ganlin.Wu on 2016/10/18.
 */
public class MyOpenHelper extends DaoMaster.DevOpenHelper {
    public MyOpenHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {

    }
}
