/** ************************************************************************/
/*                                                                         */
/* Copyright (c) 2015 YULONG Company */
/* 宇龙计算机通信科技（深圳）有限公司 版权所有 2015 */
/*                                                                         */
/* PROPRIETARY RIGHTS of YULONG Company are involved in the */
/* subject matter of this material. All manufacturing, reproduction, use, */
/* and sales rights pertaining to this subject matter are governed by the */
/* license agreement. The recipient of this software implicitly accepts */
/* the terms of the license. */
/* 本软件文档资料是宇龙公司的资产,任何人士阅读和使用本资料必须获得 */
/* 相应的书面授权,承担保密责任和接受相应的法律约束. */
/*                                                                         */
/** ************************************************************************/
package com.example.factoryrec.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件工具类
 *
 * @author yangcheng
 */
public class FileUtil {
    private static final String TAG = "FileUtil";
    private static final Object zipObject = new Object();
    private static final byte[] zipDataBuffer = new byte[1024 * 1024];

    /**
     * 创建图片文件夹
     */
    public static File createImageFileDir(Context context) {

        File fileDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            fileDir = new File(Environment.getExternalStorageDirectory(), VocConstant.IMAGE_CACHE_DIR);
        } else {
            fileDir = new File(context.getFilesDir().getAbsolutePath(), VocConstant.IMAGE_CACHE_DIR);
        }
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        return fileDir;
    }

    /**
     * 创建视频文件夹
     */
    public static File createVedioFileDir(Context context) {

        File fileDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            fileDir = new File(Environment.getExternalStorageDirectory(), VocConstant.VEDIO_CACHE_DIR);
        } else {
            fileDir = new File(context.getFilesDir().getAbsolutePath(), VocConstant.VEDIO_CACHE_DIR);
        }
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        return fileDir;
    }

    /**
     * 创建数据文件夹
     */
    public static File createDataFileDir(Context context) {

        File fileDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            fileDir = new File(Environment.getExternalStorageDirectory(), VocConstant.DATA_CACHE_DIR);
        } else {
            fileDir = new File(context.getFilesDir().getAbsolutePath(), VocConstant.DATA_CACHE_DIR);
        }
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        return fileDir;
    }

    /**
     * 递归删除文件和文件夹
     */
    public static void recursionDeleteFile(File file) {

        try {
            if (file == null || !file.exists()) {
                return;
            }
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    recursionDeleteFile(f);
                }
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定文件大小
     */
    public static long getFileSize(File file) {

        long size = 0;
        try {
            if (file != null && file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
                fis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public static void transferFileToZip(ZipOutputStream zipOs, String reportFilePath, String entryParentName)
            throws IOException {
        synchronized (zipObject) {
            InputStream is = null;
            int length;
            try {
                if (TextUtils.isEmpty(entryParentName)) {
                    zipOs.putNextEntry(new ZipEntry(new File(reportFilePath).getName()));
                } else {
                    zipOs.putNextEntry(new ZipEntry(
                            entryParentName + File.separator + new File(reportFilePath).getName()));
                }
                is = new BufferedInputStream(new FileInputStream(reportFilePath));
                while (-1 != (length = is.read(zipDataBuffer))) {
                    zipOs.write(zipDataBuffer, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != is) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                zipOs.flush();
                zipOs.closeEntry();
            }
        }
    }

}
