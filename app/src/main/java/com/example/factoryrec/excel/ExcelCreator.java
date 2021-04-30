package com.example.factoryrec.excel;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.example.factoryrec.app.Fragment_Result;
import com.example.factoryrec.app.MainActivity;
import com.example.factoryrec.util.ProductItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;

import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.read.biff.BiffException;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WriteException;

public class ExcelCreator {

    private MainActivity mActivity;
    private ProductItem mItem;
    // 记录当天提交个数
    private SharedPreferences mSubmitCount_Value;
    private SharedPreferences.Editor mSubmitCount_Edit;
    private int defaultCountValue = 0;

    public ExcelCreator(MainActivity activity, Fragment_Result result, SharedPreferences submitCount_Value, SharedPreferences.Editor submitCount_Edit) {
        mActivity = activity;
        mItem = result.getItem();
        mSubmitCount_Value = submitCount_Value;
        mSubmitCount_Edit = submitCount_Edit;
    }

    public void generateExcel() {
        ZzExcelCreator excelCreator = ZzExcelCreator.getInstance();
        try {
            // 1. 获取当前时间，创建目录
            String submitTimer = getTime();
            File fileNameDir = mActivity.getApplicationContext().getExternalFilesDir(null);

            String filePath = "解析资料" + submitTimer;
            File fileName = new File(fileNameDir.getAbsolutePath() + File.separator + filePath + ".xls");
            if (fileName.exists()) {
                // 读取当前数量
                int countValue = mSubmitCount_Value.getInt("submit_count_value", defaultCountValue);
                // 当前数量+1，获得现在应该数量，并保存，用于本次保存和下次读取
                countValue++;
                defaultCountValue = countValue;
                mSubmitCount_Edit.putInt("submit_count_value", countValue);
                mSubmitCount_Edit.commit();

                // 文件存在 则插入数据即可
                Log.e("stormxz", " file name exists insert data countValue = " + countValue);
                excelCreator.openExcel(fileName);
                excelCreator.openSheet(0);

                // 调整单元格格式
                WritableFont wFont = new WritableFont(WritableFont.ARIAL, 11);
                wFont.setColour(Colour.BLACK);
                WritableCellFormat contextFormatForTest = new WritableCellFormat(wFont);
                contextFormatForTest.setWrap(true);
                adjustCellWidth(excelCreator, contextFormatForTest);
                adjustCellHeight(excelCreator, contextFormatForTest, countValue);

                // 插入内容
                insertData(excelCreator, countValue);
            } else {
                // 没有产生当天文件的时候, 数量为1
                mSubmitCount_Edit.putInt("submit_count_value", 1);
                mSubmitCount_Edit.commit();
                defaultCountValue = 1;
                // 文件不存在
                Log.e("stormxz", " file name do not exists create data");

                // 创建文件
                excelCreator.createExcel(fileNameDir.getAbsolutePath(), filePath);
                // 创建表格
                excelCreator.createSheet("sheet1");
                excelCreator.setColumnWidth(0, 100, 50);
                excelCreator.setRowHeightFromTo(0, 100, 1000);

                // 插入标题
                insertTitle(excelCreator);


                // 调整单元格格式
                WritableFont wFont = new WritableFont(WritableFont.ARIAL, 11);
                wFont.setColour(Colour.BLACK);
                WritableCellFormat contextFormatForTest = new WritableCellFormat(wFont);
                contextFormatForTest.setWrap(true);
                adjustCellWidth(excelCreator, contextFormatForTest);
                adjustCellHeight(excelCreator, contextFormatForTest, 1);
                // 插入内容
                insertData(excelCreator, 1);
            }


            Log.e("stormxz", "stormxz write begin");
            excelCreator.close();
            Log.e("stormxz", "stormxz write end");

        } catch (IOException | WriteException | BiffException/* | BiffException*/ e) {
            Log.e("stormxz", " excel is not check e = " + e);
            e.printStackTrace();
        }
    }

    private void insertData(ZzExcelCreator excelCreator, int row) {
        try {
            // 内容
            WritableFont wFont = new WritableFont(WritableFont.ARIAL, 11);
            wFont.setColour(Colour.BLACK);

            WritableCellFormat titleFormat = new WritableCellFormat(wFont);
            titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);//上下居中
            titleFormat.setWrap(true);
            titleFormat.setAlignment(Alignment.CENTRE); //
            titleFormat.setBorder(Border.ALL, BorderLineStyle.THIN,
                    Colour.BLACK);

            // 插入确认内容
            WritableCellFormat contextFormat = new WritableCellFormat(wFont);
            contextFormat.setWrap(true);
            contextFormat.setVerticalAlignment(VerticalAlignment.CENTRE);//上下居中
            contextFormat.setBorder(Border.ALL, BorderLineStyle.THIN,
                    Colour.BLACK);


            // 插入第一个客户
            // 插入NO.
            String cc = "    ";
//            for (int i = 0; i < 100; i++) {
//                excelCreator.merge(i, 1, i, 2);
//            }
            excelCreator.fillContent(0, row,  row + cc, titleFormat);
            // 插入客户
            excelCreator.fillContent(1, row,  mItem.getCustomer() + cc, titleFormat);
            // 插入机种
            excelCreator.fillContent(2, row, mItem.getMachineType() + cc, titleFormat);
            // 插入SN
            excelCreator.fillContent(3, row, mItem.getSN() + cc, titleFormat);
            // 插入不良大类
            excelCreator.fillContent(4, row, mItem.getBadPhenom() + cc, titleFormat);
            // 插入不良现象
            excelCreator.fillContent(5, row, mItem.getBadPhenom2() + cc, titleFormat);
            // 插入发生时间
            excelCreator.fillContent(6, row,mItem.getOccDate() + "  " + mItem.getOccTime() + cc, titleFormat);
            // 插入发生站点
            excelCreator.fillContent(7, row, mItem.getOccSite() + cc, titleFormat);
            // 插入不良位置
            excelCreator.fillContent(8, row, mItem.getBadPosition() + cc, titleFormat);
            // 插入不良图片
            List<String> listBad = mItem.getHome_BadPic();
            addImages(excelCreator, listBad, 9, row);
            // 插入外观确认
            excelCreator.fillContent(11, row, mItem.getDisplayText(), contextFormat);

            // 插入外观图片
            List<String> listDisplay = mItem.getDisplay_BadPic();
            addImages(excelCreator, listDisplay, 12, row);

            // 插入OM 确认
            excelCreator.fillContent(14, row, mItem.getOMText(), contextFormat);

            // 插入OM图片
            List<String> listOM = mItem.getOM_BadPic();
            addImages(excelCreator, listOM, 15, row);

            // 插入讯号量测确认
            excelCreator.fillContent(17, row, mItem.getSignalText(), contextFormat);

            // 插入讯号量测图片
            List<String> listSignal = mItem.getSignal_BadPic();
            addImages(excelCreator, listSignal, 18, row);

            // 插入结论
            excelCreator.fillContent(20, row, mItem.getConclusion() + "\n", contextFormat);
        } catch (WriteException exception) {
            Log.e("stormxz", " insert data error = " + exception);
            exception.printStackTrace();
        }
    }

    private void insertTitle(ZzExcelCreator excelCreator) {
        // 插入信息（坐标从0开始，列，行）

        // 标题
        try {
            WritableFont wFontTitle = new WritableFont(WritableFont.ARIAL, 14);
            wFontTitle.setColour(Colour.BLACK);
            WritableCellFormat titleFormatTitle = new WritableCellFormat(wFontTitle);
            titleFormatTitle.setVerticalAlignment(VerticalAlignment.CENTRE);//上下居中
            titleFormatTitle.setAlignment(Alignment.CENTRE); //
//            titleFormat1.setBackground(Colour.SKY_BLUE);

            // 自定义蓝色
            titleFormatTitle.setBackground(ColourUtil.getCustomColor1("#8DB4E2"));

            titleFormatTitle.setBorder(Border.ALL, BorderLineStyle.THIN,
                    Colour.BLACK);
            titleFormatTitle.setWrap(true);  // 自动换行

            // 插入标题
            // 设置第0行  高
            excelCreator.fillContent(0, 0, "No.", titleFormatTitle);
            excelCreator.fillContent(1, 0, "客户", titleFormatTitle);
            excelCreator.fillContent(2, 0, "机种", titleFormatTitle);
            excelCreator.fillContent(3, 0, "SN", titleFormatTitle);
            excelCreator.fillContent(4, 0, "不良大类", titleFormatTitle);
            excelCreator.fillContent(5, 0, "不良现象", titleFormatTitle);
            excelCreator.fillContent(6, 0, "发生时间", titleFormatTitle);
            excelCreator.fillContent(7, 0, "发生站点", titleFormatTitle);
            excelCreator.fillContent(8, 0, "不良位置", titleFormatTitle);
            excelCreator.fillContent(9, 0, "不良图片1", titleFormatTitle);
            excelCreator.fillContent(10, 0, "不良图片2", titleFormatTitle);
            excelCreator.fillContent(11, 0, "外观确认", titleFormatTitle);
            excelCreator.fillContent(12, 0, "Image1", titleFormatTitle);
            excelCreator.fillContent(13, 0, "Image2", titleFormatTitle);
            excelCreator.fillContent(14, 0, "OM 确认", titleFormatTitle);
            excelCreator.fillContent(15, 0, "Image1", titleFormatTitle);
            excelCreator.fillContent(16, 0, "Image2", titleFormatTitle);
            excelCreator.fillContent(17, 0, "讯号量测确认", titleFormatTitle);
            excelCreator.fillContent(18, 0, "Image1", titleFormatTitle);
            excelCreator.fillContent(19, 0, "Image2", titleFormatTitle);
            excelCreator.fillContent(20, 0, "结论", titleFormatTitle);
        } catch (WriteException e) {
            Log.e("stormxz", " insert title = " + e);
            e.printStackTrace();
        }

    }

    private String getTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return "" + year + month + day;
    }

    private void adjustCellWidth(ZzExcelCreator excelCreator, WritableCellFormat contextFormat) {

        // 调整单元格格式
        try {
            for (int i = 0; i < 21; i++) {
                excelCreator.fillContent(i, 99, "                 ", contextFormat);
            }
            // 调整图片
            excelCreator.fillContent(25, 0, "  \n  \n ", contextFormat);

        } catch (WriteException e) {
            e.printStackTrace();
        }

    }

    private void adjustCellHeight(ZzExcelCreator excelCreator, WritableCellFormat contextFormat, int row) {
        try {
            excelCreator.fillContent(25, row, "  \n  \n  \n  \n \n \n", contextFormat);
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    public void addImages(ZzExcelCreator excelCreator, List<String> list, int firstcolIndex, int row) {
        if (list != null && list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                byte[] data = getNewBitmap(list.get(i));
                excelCreator.addImage(list.get(i), firstcolIndex + i, row, 1, 1, data);
            }
        }
    }

    // 图片压缩
    private byte[] getNewBitmap(String pathName) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        options.inSampleSize = 1;
        WeakReference<Bitmap> wfb = new WeakReference<>(BitmapFactory.decodeFile(pathName, options));
        Bitmap bitmap = wfb.get();
        int bitmapW = (int) (bitmap.getWidth() * 0.15);
        int bitmapH = (int) (bitmap.getHeight() * 0.15);
        WeakReference<Bitmap> nwfb = new WeakReference<>(Bitmap.createScaledBitmap(bitmap, bitmapW, bitmapH, false));
        Bitmap newBM = nwfb.get();
        bitmap.recycle();
        wfb.clear();
        nwfb.clear();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        newBM.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        newBM.recycle();
        return baos.toByteArray();
    }
}
