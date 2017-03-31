package com.qingfeng.music.service;

import android.content.Intent;

import com.qingfeng.music.Constant;
import com.qingfeng.music.dao.Music;
import com.qingfeng.music.util.FileUtils;
import com.wgl.android.library.base.BaseService;
import com.wgl.android.library.commonutils.LogUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ganlin.Wu on 2016/10/12.
 */
public class DownloadLrcService extends BaseService {

    public void downloadLrc(Music music) {
        LogUtils.logd("downloadLrc:" + music.getLrcUrl());
        FileUtils fUtils = new FileUtils();
        File savePath = fUtils.getSavePath();
        if (music == null || music.getLrcUrl() == null
                || music.getLrcUrl().length() < 1) {
            LogUtils.logd("歌曲下载失败,music对象有问题");
        } else if (savePath == null) {
            LogUtils.logd("歌曲下载失败,没有存储卡");
        } else {
            File fileLrc = new File(savePath.toString() + "/"
                    + music.getTitle() + ".lrc");
            if (!fileLrc.exists()) {
                try {
                    fileLrc.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            File saveFile = new File(savePath.getAbsolutePath() + "/"
                    + music.getTitle() + ".lrc");

            FileUtils.downLoadFile(music.getLrcUrl(), saveFile, new FileUtils.DownloadCallback() {
                @Override
                public void onResult(File file) {
                    Intent intent = new Intent(Constant.RECEIVER_DOWNLOAD_LYRIC_SUCCESS);
                    sendBroadcast(intent);
                }
            });
        }
    }
}
