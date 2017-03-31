package com.qingfeng.music.util;

import com.qingfeng.music.dao.Music;
import com.qingfeng.music.widget.lrc.DefaultLrcBuilder;
import com.qingfeng.music.widget.lrc.LrcRow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by Ganlin.Wu on 2016/10/12.
 */
public class LrcUtil {

    public static List<LrcRow> resolve(Music music) {
        StringBuilder lrc = scanLrc(music);
        List<LrcRow> rows = null;
        if (lrc != null) {
            // 解析歌词
            DefaultLrcBuilder builder = new DefaultLrcBuilder();
            rows = builder.getLrcRows(lrc.toString());
        }
        return rows;
    }

    /**
     * 从SD卡中查找是否有歌词，如果有就读取
     */
    private static StringBuilder scanLrc(Music music) {
        StringBuilder lrc = null;
        FileUtils fileUtils = new FileUtils();
        File file = new File(fileUtils.getSavePath() + "/" + music.getTitle()
                + ".lrc");
        if (file.exists()) {
            lrc = readLrc(music, file);
        }
        return lrc;
    }

    /**
     * 从文件读取
     */
    private static StringBuilder readLrc(Music music, File file) {
        BufferedReader br = null;
        StringBuilder lrcContent = new StringBuilder();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(
                    file), "utf-8"));
            String str = null;
            while ((str = br.readLine()) != null) {
                lrcContent.append(str).append("\r\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return lrcContent;
    }
}
