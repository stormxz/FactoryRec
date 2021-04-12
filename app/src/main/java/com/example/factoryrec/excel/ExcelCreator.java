package com.example.factoryrec.excel;

import android.util.Log;

import com.example.factoryrec.app.Fragment_Result;
import com.example.factoryrec.app.MainActivity;
import com.example.factoryrec.util.ProductItem;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WriteException;

public class ExcelCreator {

    private MainActivity mActivity;
    private ProductItem mItem;

    public ExcelCreator(MainActivity activity, Fragment_Result result) {
        mActivity = activity;
        mItem = result.getItem();
    }

    public void generateExcel() {
        ZzExcelCreator excelCreator = ZzExcelCreator.getInstance();
        try {
            File fileName = mActivity.getApplicationContext().getExternalFilesDir(null);
            // 创建文件
            excelCreator.createExcel(fileName.getAbsolutePath(), mItem.getCustomer() + "_" + mItem.getMachineType() + "_" + mItem.getBadPhenom() + "_" + "解析报告");

            // 创建表格
            excelCreator.createSheet("sheet1");

            // 插入信息（坐标从0开始，列，行）

            // 设置格式，比如居中等：
            WritableFont wFont = new WritableFont(WritableFont.ARIAL, 15);
            wFont.setColour(Colour.BLACK);
            WritableCellFormat titleFormat = new WritableCellFormat(wFont);
            titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);//上下居中
            titleFormat.setWrap(true);

            excelCreator.setColumnWidth(0, 100, 20);
            excelCreator.setRowHeightFromTo(0, 100, 500);

            // 插入客户，机种
            excelCreator.fillContent(1, 1, "客户", titleFormat);
            excelCreator.fillContent(2, 1, mItem.getCustomer(), titleFormat);

            excelCreator.fillContent(4, 1, "机种", titleFormat);
            excelCreator.fillContent(5, 1, mItem.getMachineType(), titleFormat);

            // 插入SN，不良现象
            excelCreator.fillContent(1, 3, "SN", titleFormat);
            excelCreator.fillContent(2, 3, mItem.getSN(), titleFormat);

            excelCreator.fillContent(4, 3, "不良现象", titleFormat);
            excelCreator.fillContent(5, 3, mItem.getBadPhenom(), titleFormat);

            // 插入发生时间，发生站点
            excelCreator.fillContent(1, 5, "发生时间", titleFormat);
            excelCreator.fillContent(2, 5, mItem.getOccTime(), titleFormat);

            excelCreator.fillContent(4, 5, "发生站点", titleFormat);
            excelCreator.fillContent(5, 5, mItem.getOccSite(), titleFormat);

            // 插入不良位置，不良图片
            excelCreator.fillContent(1, 7, "不良位置", titleFormat);
            excelCreator.fillContent(2, 7, mItem.getBadPosition(), titleFormat);

            excelCreator.fillContent(1, 9, "不良图片", titleFormat);
            excelCreator.merge(1, 9, 1, 11);
            // 插入不良图片
            List<String> listBad = mItem.getHome_BadPic();
            addImages(excelCreator, listBad, 4, 9);

            excelCreator.fillContent(1, 13, "步骤分析", titleFormat);
            // 插入外观确认
            excelCreator.fillContent(1, 13, "1. 外观确认", titleFormat);
            excelCreator.merge(1, 14, 3, 16);
            excelCreator.fillContent(1, 14, mItem.getDisplayText(), titleFormat);

            // 插入外观图片
            List<String> listDisplay = mItem.getDisplay_BadPic();
            addImages(excelCreator, listDisplay, 4, 14);

            // 插入OM确认
            excelCreator.fillContent(1, 18, "2. OM确认", titleFormat);
            excelCreator.merge(1, 19, 3, 21);
            excelCreator.fillContent(1, 19, mItem.getOMText(), titleFormat);

            // 插入OM图片
            List<String> listOM = mItem.getOM_BadPic();
            addImages(excelCreator, listOM, 4, 19);

            // 插入讯号量测确认
            excelCreator.fillContent(1, 23, "3. 讯号量测确认", titleFormat);
            excelCreator.merge(1, 24, 3, 26);
            excelCreator.fillContent(1, 24, mItem.getSignalText(), titleFormat);

            // 插入讯号量测图片
            List<String> listSignal = mItem.getSignal_BadPic();
            addImages(excelCreator, listSignal, 4, 24);

            // 插入结论
            excelCreator.fillContent(1, 28, "4. 结论", titleFormat);
            excelCreator.merge(1, 29, 3, 31);
            excelCreator.fillContent(1, 29, mItem.getConclusion(), titleFormat);

            excelCreator.close();
        } catch (IOException | WriteException e) {
            Log.e("stormxz", " excel is not check e = " + e);
            e.printStackTrace();
        }
    }

    public void addImages(ZzExcelCreator excelCreator, List<String> list, int firstcolIndex, int row) {
        if (list != null && list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                excelCreator.addImage(list.get(i), firstcolIndex + i, row, 1, 3);
            }
        }
    }
}
