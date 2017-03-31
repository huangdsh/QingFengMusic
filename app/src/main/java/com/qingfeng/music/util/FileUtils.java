package com.qingfeng.music.util;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 获得文件存储路径
 */
public class FileUtils {
    private static final int NULL = 0;
    private static final int READ_ONLY = 1;
    private static final int READ_WRITE = 2;
    private String state = Environment.getExternalStorageState();
    private int permission;

    public File getSavePath() {
        File path = null;
        // 检查外部存储是否可用
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            permission = READ_WRITE;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            permission = READ_ONLY;
        } else {
            permission = NULL;
        }
        // 没有读写权限，直接退出
        if (permission >= READ_WRITE) {
            File pathFile = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            pathFile.mkdirs();
            path = new File(pathFile.toString() + "/lyric");
            path.mkdirs();
        }
        return path;
    }

    public boolean copy(File src, File dest) {
        if (src == null || dest == null || !src.exists()) return false;
        if (!dest.exists()) {
            dest.mkdirs();
            try {
                dest.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileInputStream is = new FileInputStream(src);
            FileOutputStream os = new FileOutputStream(dest);
            byte[] buff = new byte[1024];
            int data;
            while ((data = is.read(buff)) != -1) {
                os.write(buff, 0, data);
            }
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static void downLoadFile(final String src, final File dest, final DownloadCallback callback) {
        new AsyncTask<String, Void, File>() {
            @Override
            protected File doInBackground(String... params) {
                try {
                    URL url = new URL(src);
                    URLConnection conn = url.openConnection();
                    InputStream is = conn.getInputStream();
                    byte[] bytes = new byte[1024];
                    int len;
                    int sum = 0;
                    OutputStream os = new FileOutputStream(dest);
                    while ((len = is.read(bytes)) != -1) {
                        sum = sum + len;
                        os.write(bytes, 0, len);
                    }
                    os.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return dest;
            }

            @Override
            protected void onPostExecute(File file) {
                if (callback != null) {
                    callback.onResult(file);
                }
            }
        }.execute();
    }

    public interface DownloadCallback {
        void onResult(File file);
    }

}
