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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;

/**
 * Interface for image decoding classes, allowing the default {@link android.graphics.BitmapRegionDecoder} based on the
 * Skia library to be replaced with a custom class.
 */
public interface ImageRegionDecoder {

    /**
     * Initialise the decoder. When possible, initial setup work once in this method. This method must return the
     * dimensions of the image. The URI can be in one of the following formats: File: file:///scard/picture.jpg Asset:
     * file:///android_asset/picture.png Resource: android.resource://com.example.app/drawable/picture
     *
     * @param context
     *         Application context. A reference may be held, but must be cleared on recycle.
     * @param uri
     *         URI of the image.
     *
     * @return Dimensions of the image.
     *
     * @throws Exception
     *         if initialisation fails.
     */
    Point init(Context context, Uri uri) throws Exception;

    /**
     * Decode a region of the image with the given sample size. This method is called off the UI thread so it can safely
     * load the image on the current thread. It is called from an {@link android.os.AsyncTask} running in a single
     * threaded executor, and while a synchronization lock is held on this object, so will never be called concurrently
     * even if the decoder implementation supports it.
     *
     * @param sRect
     *         Source image rectangle to decode.
     * @param sampleSize
     *         Sample size.
     *
     * @return The decoded region. It is safe to return null if decoding fails.
     */
    Bitmap decodeRegion(Rect sRect, int sampleSize);

    /**
     * Status check. Should return false before initialisation and after recycle.
     *
     * @return true if the decoder is ready to be used.
     */
    boolean isReady();

    /**
     * This method will be called when the decoder is no longer required. It should clean up any resources still in use.
     */
    void recycle();

}
