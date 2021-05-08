package com.example.factoryrec.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.util.Log;

import com.example.factoryrec.R;
import com.example.factoryrec.app.Fragment_Result;
import com.example.factoryrec.app.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PdfCreator {

    private MainActivity mActivity;
    private ProductItem mItem;
    private boolean mShowLogo, mShowTitle, mShowFooter, mShowWatermark;
    private final int mScreenWidth = 720;
    private final int mScreenHeight = 1479;

    private int mBitmapWidth = 70;
    private int mBitmapHeight = 70;

    private int mPageWidth = 1600;
    private int mPageHeight = 2400;

    public PdfCreator(MainActivity activity, Fragment_Result result) {
        mActivity = activity;
        mItem = result.getItem();
//        mScreenWidth = mActivity.getWindowManager().getDefaultDisplay().getWidth();
//        mScreenHeight = mActivity.getWindowManager().getDefaultDisplay().getHeight();
//        Log.i("cc", "mScreenWidth = " + mScreenWidth + ", mScreenHeight = " + mScreenHeight);
    }

    public void generatePdf(boolean showLogo, boolean showTitle, boolean showFooter, boolean showWatermark) {
        mShowLogo = showLogo;
        mShowTitle = showTitle;
        mShowFooter = showFooter;
        mShowWatermark = showWatermark;
        PdfDocument document = new PdfDocument();//1.建立PdfDocument
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(mScreenWidth, mScreenHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);//2.建立新的page

//        drawPdfCanvas(page.getCanvas());
        drawPdf(page.getCanvas());
        document.finishPage(page);

        String pdfName = mItem.getCustomer() + " " + mItem.getMachineType() + " " + mItem.getBadPhenom2() + "不良解析报告.pdf";
        String path = mActivity.getApplicationContext().getExternalFilesDir(null) + "/" + pdfName;
//        String path = mActivity.getApplicationContext().getExternalFilesDir(null) + "/table1.pdf";
        Log.i("cc", "path = " + path);
        System.out.println(path);
        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdirs()) {
                System.out.println(1);
            } else
                System.out.println(0);
        }
        if (file.exists()) {
            file.delete();
        }
        try {
            document.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.close();
    }

    private void drawBitmap(Canvas canvas, Paint paint, Rect dst, BitmapFactory.Options options, String pathName) {
        WeakReference<Bitmap> wfb = new WeakReference<>(BitmapFactory.decodeFile(pathName, options));
        Bitmap bitmap = wfb.get();
        scaleAndDrawBitmap(canvas, paint, dst, bitmap, 0.3);
        bitmap.recycle();
        wfb.clear();
    }

    private void scaleAndDrawBitmap(Canvas canvas, Paint paint, Rect dst, Bitmap bitmap, double coe) {
        int bitmapW = (int) (bitmap.getWidth() * coe);
        int bitmapH = (int) (bitmap.getHeight() * coe);
        WeakReference<Bitmap> nwfb = new WeakReference<>(Bitmap.createScaledBitmap(bitmap, bitmapW, bitmapH, false));
        Bitmap newBM = nwfb.get();
        canvas.drawBitmap(newBM, null, dst, paint);
        newBM.recycle();
        nwfb.clear();
    }

    private void drawPdf(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(16);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        //绘制home背景格式
        drawHomeBackground(canvas, paint, options);
        drawLogoAndroidTitle(canvas, paint, options);
        drawHomeText(canvas, paint);
        drawBadPic(canvas, paint, options);
        drawWatermark(canvas, paint, mItem.getFooter());
    }

    private void drawHomeBackground(Canvas canvas, Paint paint, BitmapFactory.Options options) {
        WeakReference<Bitmap> wfb = new WeakReference<>(BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.pdf_background, options));
        Bitmap bitmap = wfb.get();
//        Log.i("cc", "bitmap.width = " + bitmap.getWidth() + ", bitmap.height = " + bitmap.getHeight());
        Rect dst = new Rect(0, 0, mScreenWidth, mScreenWidth  * bitmap.getHeight() / bitmap.getWidth());
//        Log.i("cc", "mScreenWidth = " + mScreenWidth + ", mScreenWidth  * bitmap.getHeight() / bitmap.getWidth() = " + mScreenWidth  * bitmap.getHeight() / bitmap.getWidth());
        canvas.drawBitmap(bitmap, null, dst, paint);
        wfb.clear();
        bitmap.recycle();
    }

    private void drawLogoAndroidTitle(Canvas canvas, Paint paint, BitmapFactory.Options options) {
        int x = 10;
        int logoWidth = 0;
        boolean logoDrawed = false;
        if (mShowLogo && mItem.getLogo_Pic() != null) {
            WeakReference<Bitmap> wfb = new WeakReference<>(BitmapFactory.decodeFile(mItem.getLogo_Pic(), options));
            Bitmap bitmap = wfb.get();
            logoWidth = 40 * bitmap.getWidth() / bitmap.getHeight();
            Rect dst = new Rect(x, x, x + logoWidth, x + 40);
            scaleAndDrawBitmap(canvas, paint, dst, bitmap, 0.3);
            logoDrawed = true;
        }
        if (mShowTitle && mItem.getTitle() != null) {
            paint.setTextSize(25);
            paint.setFakeBoldText(true);
            canvas.drawText(mItem.getTitle(), x + (logoDrawed ? logoWidth : 0) + 25, x + 27, paint);
            paint.setTextSize(16);
            paint.setFakeBoldText(false);
        }
    }

    private void drawHomeText(Canvas canvas, Paint paint) {
        drawText(mItem.getCustomer(), 170, 75, canvas, paint);                    //客户
        drawText(mItem.getSN(),170, 106, canvas, paint);                          //sn
        drawText(mItem.getBadPhenom(), 170, 137, canvas, paint);                  //不良大类
        drawText(mItem.getOccDate(), 170, 168, canvas, paint);                    //发生时间
        drawText(mItem.getMachineType(), 560, 75, canvas, paint);                 //机种
        drawText(mItem.getOccSite(), 560, 106, canvas, paint);                    //发生站点
        drawText(mItem.getBadPhenom2(), 560, 137, canvas, paint);                 //不良现象
        drawText(mItem.getBadPosition(), 560, 168, canvas, paint);                //不良位置
        drawText(mItem.getDisplayText(), 88, 628, canvas, paint);                 //外观确认
        drawText(mItem.getOMText(), 88, 854, canvas, paint);                      //OM检查
        drawText(mItem.getSignalText(), 88, 1083, canvas, paint);                 //讯号量测确认
        drawText(mItem.getConclusion(), 36, 1143, canvas, paint);                 //结论
        drawText(mItem.getFooter(), 580, 1200, canvas, paint);                    //页脚
    }

    private void drawText(String str, int x, int y, Canvas canvas, Paint paint) {
        if (str != null) {
            canvas.drawText(str, x, y, paint);
        }
    }

    private void drawDisplayText(String text, Canvas canvas, Paint paint) {
        String[] str = text.split("\n");
        for (int i = 0; i < str.length; i++) {
            canvas.drawText(str[i], 105, 280 + i * 30, paint);
        }
    }

    private void drawBadPic(Canvas canvas, Paint paint, BitmapFactory.Options options) {
        int x = 90;
        int badPicWidth = 230;
        int badPicHeight = 160;
        //首页不良图片
        ArrayList homeBitmapList = (ArrayList) mItem.getHome_BadPic();
        drawBadPic(canvas, paint, options, homeBitmapList, x, 215, badPicWidth, badPicHeight);
        //外观不良图片
        ArrayList displayBitmapList = (ArrayList) mItem.getDisplay_BadPic();
        drawBadPic(canvas, paint, options, displayBitmapList, x, 443, badPicWidth, badPicHeight);
        //OM不良图片
        ArrayList OMBitmapList = (ArrayList) mItem.getOM_BadPic();
        drawBadPic(canvas, paint, options, OMBitmapList, x, 668, badPicWidth, badPicHeight);
        //讯号量测不良图片
        ArrayList signalBitmapList = (ArrayList) mItem.getSignal_BadPic();
        drawBadPic(canvas, paint, options, signalBitmapList, x, 898, badPicWidth, badPicHeight);
    }

    private void drawBadPic(Canvas canvas, Paint paint, BitmapFactory.Options options, List list, int x, int y, int picWidth, int picHeight) {
        if (list != null && list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                Rect dst = new Rect(x + (picWidth + 30) * i, y, x + (picWidth + 30) * i + picWidth, y + picHeight);
                drawBitmap(canvas, paint, dst, options, (String)list.get(i));
            }
        }
    }

    private void drawWatermark(Canvas canvas, Paint paint, String text) {
        if (mShowWatermark && text != null) {
            paint.setARGB(0x20, 0, 0, 0);//设置水印颜色
            paint.setTextSize(30);//设置水印字体大小
            paint.setAntiAlias(true); // 抗锯齿
            canvas.rotate(-20);
            canvas.drawText(text, 0, 470, paint);
            canvas.drawText(text, 0, 960, paint);
            canvas.rotate(0);
            paint.setTextSize(16);
        }
    }

}
