package com.qingfeng.music.service;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.qingfeng.music.Constant;
import com.qingfeng.music.api.song.SongApi;
import com.qingfeng.music.bean.SongDownloadBean;
import com.qingfeng.music.dao.Music;
import com.qingfeng.music.dao.DaoHelper;
import com.qingfeng.music.util.FileUtils;
import com.qingfeng.music.util.MusicManager;
import com.wgl.android.library.base.BaseService;
import com.wgl.android.library.baserx.RxSchedulers;
import com.wgl.android.library.baserx.RxSubscriber;
import com.wgl.android.library.commonutils.LogUtils;

import java.io.File;

/**
 * Created by Ganlin.Wu on 2016/9/26.
 */
public class DownloadSongService extends BaseService {
    private static final String TAG = "DownloadSongService";
    private static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "qfmusic";

    public void downloadSong(long songId) {
        SongApi.getInstance().getApiService().getDownloadSong(Constant.API_FORMAT, Constant.API_CALLBACK, Constant.API_FROM, Constant.API_METHOD_DOWNLOAD, songId, 64, 1393123213)
                .compose(RxSchedulers.<SongDownloadBean>io_main())
                .subscribe(new RxSubscriber<SongDownloadBean>(this) {
                    @Override
                    protected void _onNext(SongDownloadBean bean) {
                        downloadSong(bean.getBitrate().get(0), bean.getSonginfo());
                    }

                    @Override
                    protected void _onError(String message) {
                        LogUtils.loge(message);
                    }
                });
    }

    public void downloadSong(final SongDownloadBean.BitrateBean bitrate, final SongDownloadBean.SonginfoBean info) {
        File dir = new File(DOWNLOAD_PATH);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }
        String songName = info.getTitle() + "." + bitrate.getFile_extension();
        File file = new File(DOWNLOAD_PATH, songName);
        if (file.exists()) {
            file.delete();
        }
        String songPath = bitrate.getShow_link();
        if (TextUtils.isEmpty(songPath)) {
            songPath = bitrate.getFile_link();
        }
        FileUtils.downLoadFile(songPath, file, new FileUtils.DownloadCallback() {
            @Override
            public void onResult(File file) {
                //插入数据库并通知更新
                Music music = new Music();
                music.setId(Long.parseLong(info.getSong_id()));
                music.setPath(file.getAbsolutePath());
                music.setImageUrl(info.getPic_small());
                music.setTitle(info.getTitle());
                music.setArtist(info.getAuthor());
                music.setAlbum(info.getAlbum_title());
                music.setAlbumId(Long.parseLong(info.getAlbum_id()));
                music.setLrcUrl(info.getLrclink());
                DaoHelper.getInstance().getMusicDao().insert(music);
                MusicManager.getInstance().loadLocalMusicList();

                Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                scanIntent.setData(Uri.fromFile(file));
                sendBroadcast(scanIntent);

                Intent updateIntent = new Intent(Constant.RECEIVER_UPDATE_MUSIC_LIST);
                sendBroadcast(updateIntent);
            }
        });
    }

}
