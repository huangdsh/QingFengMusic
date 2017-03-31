package com.qingfeng.music.service.connection;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.wgl.android.library.base.BaseService;
import com.wgl.android.library.commonutils.LogUtils;

/**
 * Created by Ganlin.Wu on 2016/10/14.
 */
public class BaseServiceConnection<T extends BaseService> implements ServiceConnection {

    private T mService;

    public T getService() {
        return mService;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        LogUtils.logd("onServiceConnected:" + name);
        mService = (T) ((BaseService.ServiceHolder) service).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        LogUtils.logd("onServiceDisconnected:" + name);
    }
}
