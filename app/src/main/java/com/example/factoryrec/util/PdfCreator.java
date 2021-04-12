package com.example.factoryrec.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.util.Log;

import com.example.factoryrec.app.Fragment_Result;
import com.example.factoryrec.app.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class PdfCreator {

    private MainActivity mActivity;
    private ProductItem mItem;
    private int mScreenWidth, mScreenHeight;

    private int mBitmapWidth = 70;
    private int mBitmapHeight = 70;

    public PdfCreator(MainActivity activity, Fragment_Result result) {
        mActivity = activity;
        mItem = result.getItem();
        mScreenWidth = mActivity.getWindowManager().getDefaultDisplay().getWidth();
        mScreenHeight = mActivity.getWindowManager().getDefaultDisplay().getHeight();
    }

    public void generatePdf() {
        PdfDocument document = new PdfDocument();//1.建立PdfDocument
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(mScreenWidth, mScreenHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);//2.建立新的page

        drawPdfCanvas(page.getCanvas());
        document.finishPage(page);

        String path = mActivity.getFilesDir().getAbsolutePath() + "/table1.pdf";
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

    private void drawPdfCanvas(Canvas canvas) {
        //首页
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ArrayList strList = new ArrayList();
        strList.add("客户：" + mItem.getCustomer());
        strList.add("机种：" + mItem.getMachineType());
        strList.add("SN："   + mItem.getSN());
        strList.add("不良现象：" + mItem.getBadPhenom() + "——" + mItem.getBadPhenom2());
        strList.add("发生时间：" + mItem.getOccDate() + "   " + mItem.getOccTime());
        strList.add("发生站点：" + mItem.getOccSite());
        strList.add("不良位置：" + mItem.getBadPosition());
        strList.add("不良位置：");
//        String str
//                = "客户：" + mItem.getCustomer() + "\n"
//                + "机种：" + mItem.getMachineType() + "\n"
//                + "SN："   + mItem.getSN() + "\n"
//                + "不良现象：" + mItem.getBadPhenom() + "——" + mItem.getBadPhenom2() + "\n"
//                + "发生时间：" + mItem.getOccDate() + "   " + mItem.getOccTime() + "\n"
//                + "发生站点：" + mItem.getOccSite() + "\n"
//                + "不良位置：" + mItem.getBadPosition() + "\n";
        for (int i = 0; i < strList.size(); i++) {
            canvas.drawText((String) strList.get(i), 50, 50 + i * 30, paint);
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        //首页不良图片
        int newPosition = 30 + strList.size() * 30;
        ArrayList homeBitmapList = (ArrayList) mItem.getHome_BadPic();
        drawBitmap(canvas, paint, homeBitmapList, newPosition, options);

        //外观确认
        newPosition += 30 + mBitmapHeight;
        canvas.drawText("外观确认：" + mItem.getDisplayText(), 50, newPosition, paint);
        //外观确认图片
        newPosition += 10;
        ArrayList displayBitmapList = (ArrayList) mItem.getDisplay_BadPic();
        drawBitmap(canvas, paint, displayBitmapList, newPosition, options);

        //OM确认
        newPosition += 30 + mBitmapHeight;
        canvas.drawText("OM确认：" + mItem.getOMText(), 50, newPosition, paint);
        //OM确认图片
        newPosition += 10;
        ArrayList omBitmapList = (ArrayList) mItem.getOM_BadPic();
        drawBitmap(canvas, paint, omBitmapList, newPosition, options);

        //讯号量测确认
        newPosition += 30 + mBitmapHeight;
        canvas.drawText("讯号量测确认：" + mItem.getSignalText(), 50, newPosition, paint);
        //讯号量测确认图片
        newPosition += 10;
        ArrayList signalBitmapList = (ArrayList) mItem.getSignal_BadPic();
        drawBitmap(canvas, paint, signalBitmapList, newPosition, options);

        //结论
        newPosition += 30 + mBitmapHeight;
        canvas.drawText("结论：" + mItem.getConclusion(), 50, newPosition, paint);
    }

    private void drawBitmap(Canvas canvas, Paint paint, ArrayList list, int y, BitmapFactory.Options options) {
        Rect dst;
        for (int i = 0; i < list.size(); i++) {
            int x = 50 + (mBitmapWidth + 5) * i;
            dst = new Rect(x, y, x + mBitmapWidth, y + mBitmapHeight);
            WeakReference<Bitmap> wfb = new WeakReference<>(BitmapFactory.decodeFile((String)list.get(i), options));
            Bitmap bitmap = wfb.get();
            canvas.drawBitmap(bitmap, null, dst, paint);
            wfb.clear();
            bitmap.recycle();
        }
    }

}
