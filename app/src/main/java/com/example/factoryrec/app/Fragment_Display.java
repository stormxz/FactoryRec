package com.example.factoryrec.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.factoryrec.R;
import com.example.factoryrec.selector.MultiImageSelectorActivity;
import com.example.factoryrec.util.FileUtil;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class Fragment_Display extends MainFragment {

    // 图片附件数据
    private List<Bitmap> postPictureData;

    private EditText mDisplayEditText = null;
    private EditText mDisplayConfirmEditText = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_display, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_display);

        mDisplayConfirmEditText = view.findViewById(R.id.display_confirm_et);
        mDisplayConfirmEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mItem.setConfirm_1(s + "");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDisplayEditText = view.findViewById(R.id.display_et);
                        // 添加显示文本编辑监听
        mDisplayEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

                    @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mItem.setDisplayText(s + "");
            }

                    @Override
            public void afterTextChanged(Editable s) {

            }
        });
        super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {// 图片选择结果回调

                List<String> newImageUris = new ArrayList<String>();
                if (selectList != null) {
                    for (int i = 0; i < selectList.size(); i++) {
                        newImageUris.add(selectList.get(i).getCompressPath());
                    }
                    mItem.setDisplay_BadPic(newImageUris);
                }
            }
        }
    }

    public void updateDeleteImages(List<LocalMedia> list) {

        List<String> newImageUris = new ArrayList<String>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                newImageUris.add(list.get(i).getCompressPath());
            }
            mItem.setDisplay_BadPic(newImageUris);
        }
    }

}
