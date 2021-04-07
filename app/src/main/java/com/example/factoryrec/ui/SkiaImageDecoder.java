/** ************************************************************************/
/*                                                                         */
/* Copyright (c) 2016 YULONG Company */
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
package com.example.factoryrec.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;

import com.example.factoryrec.util.FileUtil;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Default implementation of {@link com.davemorrissey.labs.subscaleview.decoder.ImageDecoder} using Android's
 * {@link BitmapFactory}, based on the Skia library. This works well in most circumstances and has
 * reasonable performance, however it has some problems with grayscale, indexed and CMYK images.
 */
public class SkiaImageDecoder implements ImageDecoder {

    private static final String FILE_PREFIX = "file://";
    private static final String ASSET_PREFIX = FILE_PREFIX + "/android_asset/";
    private static final String RESOURCE_PREFIX = ContentResolver.SCHEME_ANDROID_RESOURCE + "://";

    @Override
    public Bitmap decode(Context context, Uri uri) throws Exception {

        String uriString = uri.toString();
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = null;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        if (uriString.startsWith(FILE_PREFIX)) {
            long fileSize = FileUtil.getFileSize(new File(uriString.substring(FILE_PREFIX.length())));
            if (fileSize > 0.2 * 1048576) {
                options.inSampleSize = 1;
            }
            if (fileSize > 0.4 * 1048576) {
                options.inSampleSize = 2;
            }
            if (fileSize > 0.6 * 1048576) {
                options.inSampleSize = 4;
            }
            if (fileSize > 0.8 * 1048576) {
                options.inSampleSize = 6;
            }
        }
        if (uriString.startsWith(RESOURCE_PREFIX)) {
            Resources res;
            String packageName = uri.getAuthority();
            if (context.getPackageName().equals(packageName)) {
                res = context.getResources();
            } else {
                PackageManager pm = context.getPackageManager();
                res = pm.getResourcesForApplication(packageName);
            }

            int id = 0;
            List<String> segments = uri.getPathSegments();
            int size = segments.size();
            if (size == 2 && segments.get(0).equals("drawable")) {
                String resName = segments.get(1);
                id = res.getIdentifier(resName, "drawable", packageName);
            } else if (size == 1 && TextUtils.isDigitsOnly(segments.get(0))) {
                try {
                    id = Integer.parseInt(segments.get(0));
                } catch (NumberFormatException ignored) {
                }
            }

            bitmap = BitmapFactory.decodeResource(context.getResources(), id, options);
        } else if (uriString.startsWith(ASSET_PREFIX)) {
            String assetName = uriString.substring(ASSET_PREFIX.length());
            bitmap = BitmapFactory.decodeStream(context.getAssets().open(assetName), null, options);
        } else if (uriString.startsWith(FILE_PREFIX)) {
            try {
                bitmap = BitmapFactory.decodeFile(uriString.substring(FILE_PREFIX.length()), options);
            } catch (Exception e) {
            }
        } else {
            InputStream inputStream = null;
            try {
                ContentResolver contentResolver = context.getContentResolver();
                inputStream = contentResolver.openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
        if (bitmap == null) {
            throw new RuntimeException(
                    "Skia image region decoder returned null bitmap - image format may not be supported");
        }
        return bitmap;
    }
}
