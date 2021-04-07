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
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.Hashtable;

/**
 * 图片处理工具类
 *
 * @author yangcheng
 */
public final class ImageUtil {

    /**
     * 图片质量压缩
     */
    public static boolean compressBmpToFile(String srcPath, File file) {

        try {
            Bitmap bitmap = getImage(srcPath, file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, 100, baos);
            int options = 100;
            while (baos.toByteArray().length / 1024 > 200) {
                baos.reset();
                bitmap.compress(CompressFormat.JPEG, options, baos);
                options -= 10;
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 图片大小压缩
     */
    private static Bitmap getImage(String srcPath, File file) {

        Options newOpts = new Options();

        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f

        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {
            // 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            // 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) {
            be = 1;
        }
        newOpts.inSampleSize = be;// 设置缩放比例

        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        return bitmap;
    }

    /**
     * Transfer drawable to bitmap
     *
     * @param drawable
     *
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {

        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Bitmap to drawable
     *
     * @param bitmap
     *
     * @return
     */
    public static Drawable bitmapToDrawable(Bitmap bitmap) {

        return new BitmapDrawable(bitmap);
    }

    /**
     * Input stream to bitmap
     *
     * @param inputStream
     *
     * @return
     *
     * @throws Exception
     */
    public static Bitmap inputStreamToBitmap(InputStream inputStream) throws Exception {

        return BitmapFactory.decodeStream(inputStream);
    }

    /**
     * Byte transfer to bitmap
     *
     * @param byteArray
     *
     * @return
     */
    public static Bitmap byteToBitmap(byte[] byteArray) {

        if (byteArray.length != 0) {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        } else {
            return null;
        }
    }

    /**
     * Byte transfer to drawable
     *
     * @param byteArray
     *
     * @return
     */
    public static Drawable byteToDrawable(byte[] byteArray) {

        ByteArrayInputStream ins = null;
        if (byteArray != null) {
            ins = new ByteArrayInputStream(byteArray);
        }
        return Drawable.createFromStream(ins, null);
    }

    /**
     * @param bm
     *
     * @return
     */
    public static byte[] bitmapToBytes(Bitmap bm) {

        byte[] bytes = null;
        if (bm != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(CompressFormat.PNG, 100, baos);
            bytes = baos.toByteArray();
        }
        return bytes;
    }

    /**
     * Drawable transfer to bytes
     *
     * @param drawable
     *
     * @return
     */
    public static byte[] drawableToBytes(Drawable drawable) {

        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        byte[] bytes = bitmapToBytes(bitmap);
        ;
        return bytes;
    }

    /**
     * Base64 to byte[] //
     */
    // public static byte[] base64ToBytes(String base64) throws IOException {
    // byte[] bytes = Base64.decode(base64);
    // return bytes;
    // }
    //
    // /**
    // * Byte[] to base64
    // */
    // public static String bytesTobase64(byte[] bytes) {
    // String base64 = Base64.encode(bytes);
    // return base64;
    // }

    /**
     * Create reflection images
     *
     * @param bitmap
     *
     * @return
     */
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {

        final int reflectionGap = 4;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w, h / 2, matrix, false);

        Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2), Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);

        canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
                                                   bitmapWithReflection.getHeight() + reflectionGap,
                                                   0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, h, w, bitmapWithReflection.getHeight() + reflectionGap, paint);

        return bitmapWithReflection;
    }

    /**
     * Get rounded corner images
     *
     * @param bitmap
     * @param roundPx
     *         5 10
     *
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * Resize the bitmap
     *
     * @param bitmap
     * @param width
     * @param height
     *
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    /**
     * Resize the drawable
     *
     * @param drawable
     * @param w
     * @param h
     *
     * @return
     */
    public static Drawable zoomDrawable(Drawable drawable, int w, int h) {

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);
        Matrix matrix = new Matrix();
        float sx = ((float) w / width);
        float sy = ((float) h / height);
        matrix.postScale(sx, sy);
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
        return new BitmapDrawable(newbmp);
    }

    /**
     * Get images from SD card by path and the name of image
     *
     * @param photoName
     *
     * @return
     */
    public static Bitmap getPhotoFromSDCard(String path, String photoName) {

        Bitmap photoBitmap = BitmapFactory.decodeFile(path + "/" + photoName + ".png");
        if (photoBitmap == null) {
            return null;
        } else {
            return photoBitmap;
        }
    }

    /**
     * Check the SD card
     *
     * @return
     */
    public static boolean checkSDCardAvailable() {

        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * @param path
     * @param photoName
     *
     * @return
     */
    public static boolean findPhotoFromSDCard(String path, String photoName) {

        boolean flag = false;

        if (checkSDCardAvailable()) {
            File dir = new File(path);
            if (dir.exists()) {
                File folders = new File(path);
                File photoFile[] = folders.listFiles();
                for (int i = 0; i < photoFile.length; i++) {
                    String fileName = photoFile[i].getName().split("\\.")[0];
                    if (fileName.equals(photoName)) {
                        flag = true;
                    }
                }
            } else {
                flag = false;
            }
            // File file = new File(path + "/" + photoName + ".jpg" );
            // if (file.exists()) {
            // flag = true;
            // }else {
            // flag = false;
            // }

        } else {
            flag = false;
        }
        return flag;
    }

    /**
     * Save image to the SD card
     *
     * @param photoBitmap
     * @param photoName
     * @param path
     */
    public static void savePhotoToSDCard(Bitmap photoBitmap, String path, String photoName) {

        if (checkSDCardAvailable()) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File photoFile = new File(path, photoName + ".png");
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBitmap != null) {
                    if (photoBitmap.compress(CompressFormat.PNG, 100, fileOutputStream)) {
                        fileOutputStream.flush();
                        // fileOutputStream.close();
                    }
                }
            } catch (FileNotFoundException e) {
                photoFile.delete();
                e.printStackTrace();
            } catch (IOException e) {
                photoFile.delete();
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Delete the image from SD card
     *
     * @param context
     * @param path
     *         file:///sdcard/temp.jpg
     */
    public static void deleteAllPhoto(String path) {

        if (checkSDCardAvailable()) {
            File folder = new File(path);
            File[] files = folder.listFiles();
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }
    }

    public static void deletePhotoAtPathAndName(String path, String fileName) {

        if (checkSDCardAvailable()) {
            File folder = new File(path);
            File[] files = folder.listFiles();
            for (int i = 0; i < files.length; i++) {
                System.out.println(files[i].getName());
                if (files[i].getName().equals(fileName)) {
                    files[i].delete();
                }
            }
        }
    }

    // 存储图片的集合(存在内存中)
    private static Hashtable<Integer, SoftReference<Bitmap>> mCacheBitmapTable =
            new Hashtable<Integer, SoftReference<Bitmap>>();

    /**
     * 根据id获得bitmap对象, 注意: 采用了SoftReference缓存,
     *
     * @param context
     * @param imageID
     *
     * @return 先去集合中根据imageID去取相对应的图片, 如果有, 直接返回 如果没有, 调用getInvertImage方法得到一个对象返回
     */
    public static Bitmap getBitmap(Context context, int imageID, int position) {

        SoftReference<Bitmap> softReference = mCacheBitmapTable.get(imageID);
        if (softReference != null) {
            Bitmap bitmap = softReference.get();
            if (bitmap != null) {
                Log.i("ImageUtils", "从内存中取: " + position);
                return bitmap;
            }
        }

        Log.i("ImageUtils", "重新加载: " + position);
        Bitmap invertImage = getInvertImage(context, imageID);

        // 取出来对应的图片之后, 往内存中存一份, 为了方便下次直接去内存中取
        mCacheBitmapTable.put(imageID, new SoftReference<Bitmap>(invertImage));
        return invertImage;
    }

    /**
     * 根据给定的id获取处理(倒影, 倒影渐变)过的bitmap
     *
     * @param imageID
     *
     * @return
     */
    private static Bitmap getInvertImage(Context context, int imageID) {

        // 获得原图
        Options opts = new Options();
        opts.inSampleSize = 2;
        Bitmap sourceBitmap = BitmapFactory.decodeResource(context.getResources(), imageID, opts);

        // 倒影图片
        Matrix matrix = new Matrix();
        // 设置图片的反转为, 垂直反转
        matrix.setScale(1.0f, -1.0f);
        // float[] values = {
        // 1.0f, 0f, 0f,
        // 0f, -1.0f, 0f,
        // 0f, 0f, 1.0f
        // };
        // 倒影图片
        Bitmap invertBitmap = Bitmap.createBitmap(sourceBitmap, 0, sourceBitmap.getHeight() / 2,
                                                  sourceBitmap.getWidth(), sourceBitmap.getHeight() / 2,
                                                  matrix, false);

        // 合成一张图片
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(),
                                                  (int) (sourceBitmap.getHeight() * 1.5 + 5),
                                                  Config.ARGB_8888);

        // 把原图添加到合成图片的左上角
        Canvas canvas = new Canvas(resultBitmap); // 指定画板画在合成图片上
        canvas.drawBitmap(sourceBitmap, 0, 0, null); // 把原图绘制到合成图上

        // 把倒影图片绘制到合成图片上
        canvas.drawBitmap(invertBitmap, 0, sourceBitmap.getHeight() + 5, null);

        Rect rect = new Rect(0, sourceBitmap.getHeight() + 5, resultBitmap.getWidth(),
                             resultBitmap.getHeight());
        Paint paint = new Paint();

        /**
         * TileMode.CLAMP 指定渲染边界以外的控件以最后的那个颜色继续往下渲染
         */
        LinearGradient shader = new LinearGradient(0, sourceBitmap.getHeight() + 5, 0,
                                                   resultBitmap.getHeight(), 0x70FFFFFF, 0x00FFFFFF,
                                                   TileMode.CLAMP);

        // 设置为取交集模式
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // 指定渲染器为线性渲染器
        paint.setShader(shader);
        canvas.drawRect(rect, paint);

        return resultBitmap;
    }

    /**
     * bmpToByteArray
     *
     * @param bmp
     * @param needRecycle
     *
     * @return
     */
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }
}
