package com.example.factoryrec.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.factoryrec.R;
import com.example.factoryrec.selector.MultiImageSelectorActivity;
import com.example.factoryrec.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Fragment_Signal extends MainFragment {

    // 图片附件数据
    private List<Bitmap> postPictureData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_signal, container, false);
        gViewPostPicture = view.findViewById(R.id.gViewPostPicture_single);
        super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PHOTOS:// 图片
                    if (data != null) {
                        List<String> imageUris = data.getStringArrayListExtra(
                                MultiImageSelectorActivity.EXTRA_RESULT);
                        Log.e("stormxz", "onActivityResult imageUris: " + imageUris);
                        List<String> newImageUris = new ArrayList<String>();

                        // 过滤超过图片限制的图片
                        if (imageUris != null) {
                            photoList.clear();
                            for (String file : imageUris) {
                                long fileSize = FileUtil.getFileSize(new File(file));
                                if (fileSize < MAX_PHOTO_SIZE) {
                                    newImageUris.add(file);
                                    photoList.add(new File(file));
                                }
                            }
                        }
                        // 刷新图片列表
                        Log.e("stormxz", "onActivityResult newImageUris: " + newImageUris);
                        setImageList(newImageUris);
                    }
                    break;
            }
        }
    }

}
