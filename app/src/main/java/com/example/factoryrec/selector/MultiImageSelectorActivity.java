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
package com.example.factoryrec.selector;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.example.factoryrec.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片选择
 *
 * @author yangcheng
 */
public class MultiImageSelectorActivity extends FragmentActivity
        implements MultiImageSelectorFragment.Callback {

    /** 最大图片选择次数，int类型，默认9 */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /** 图片选择模式，默认多选 */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /** 是否显示相机，默认显示 */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /** 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合 */
    public static final String EXTRA_RESULT = "select_result";
    /** 默认选择集 */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";

    /** 单选 */
    public static final int MODE_SINGLE = 0;
    /** 多选 */
    public static final int MODE_MULTI = 1;

    private ArrayList<String> resultList = new ArrayList<String>();
    private Button mSubmitButton;
    private int mDefaultCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);

        Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, 9);
        int mode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);
        boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        if (mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }

        Bundle bundle = new Bundle();
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mode);
        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
        bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);

        getSupportFragmentManager().beginTransaction().add(R.id.image_grid, Fragment.instantiate(this,
                                                                                                 MultiImageSelectorFragment.class
                                                                                                         .getName(),
                                                                                                 bundle))
                .commit();

        // 返回按钮
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                setResult(RESULT_CANCELED);
                finish();
            }
        });

        // 完成按钮
        mSubmitButton = (Button) findViewById(R.id.commit);
        if (resultList == null || resultList.size() <= 0) {
            mSubmitButton.setText(R.string.action_done);
        } else {
            updateDoneText();
        }
        mSubmitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (resultList != null) {
                    // 返回已选择的图片数据
                    Intent data = new Intent();
                    data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });
    }

    private void updateDoneText() {

        mSubmitButton.setText(String.format("%s(%d/%d)", getString(R.string.action_done), resultList.size(),
                                            mDefaultCount));
    }

    @Override
    public void onSingleImageSelected(String path) {

        Intent data = new Intent();
        resultList.add(path);
        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onMultiImageSelected(List<String> fileFath) {

        if (fileFath != null) {
            resultList.clear();
            resultList.addAll(fileFath);
            updateDoneText();

            // 当为选择图片时候的状态
            if (resultList.size() == 0) {
                mSubmitButton.setText(R.string.action_done);
            }
        }
    }

    @Override
    public void onImageSelected(String path) {

        if (!resultList.contains(path)) {
            resultList.add(path);
        }
        // 有图片之后，改变按钮状态
        if (resultList.size() > 0) {
            updateDoneText();
        }
    }

    @Override
    public void onImageUnselected(String path) {

        if (resultList.contains(path)) {
            resultList.remove(path);
        }
        updateDoneText();

        // 当为选择图片时候的状态
        if (resultList.size() == 0) {
            mSubmitButton.setText(R.string.action_done);
        }
    }

    @Override
    public void onCameraShot(File imageFile) {

        if (imageFile != null) {

            // notify system
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));

            Intent data = new Intent();
            resultList.add(imageFile.getAbsolutePath());
            data.putStringArrayListExtra(EXTRA_RESULT, resultList);
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
