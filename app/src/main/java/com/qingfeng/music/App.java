package com.qingfeng.music;


import com.wgl.android.library.baseapp.BaseApplication;
import com.wgl.android.library.commonutils.LogUtils;

/**
 * Created by Ganlin.Wu on 2016/9/20.
 */
public class App extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.logInit(true);
    }

}
