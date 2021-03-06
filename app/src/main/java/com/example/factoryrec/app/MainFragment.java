package com.example.factoryrec.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.factoryrec.R;
import com.example.factoryrec.pictureselector.FullyGridLayoutManager;
import com.example.factoryrec.pictureselector.GridImageAdapter;
import com.example.factoryrec.selector.MultiImageSelectorActivity;
import com.example.factoryrec.ui.ScrollGridView;
import com.example.factoryrec.util.DensityUtil;
import com.example.factoryrec.util.FileUtil;
import com.example.factoryrec.util.ImageUtil;
import com.example.factoryrec.util.ProductItem;
import com.example.factoryrec.util.ScreenUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.Permission;
import com.luck.picture.lib.permissions.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment {

    protected MainActivity mActivity;
    protected ProductItem mItem;

    private int maxSelectNum = 2;
    protected List<LocalMedia> selectList = new ArrayList<>();
    protected GridImageAdapter adapter;
    protected RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // ???????????????
        initWidget();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mItem = mActivity.getItem();
    }


    private void initWidget() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(getContext(), onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // ???????????? ???????????????????????????
                            //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(getActivity()).externalPicturePreview(position, selectList);
                            break;
                        case 2:
                            // ????????????
                            PictureSelector.create(getActivity()).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // ????????????
                            PictureSelector.create(getActivity()).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {

        @SuppressLint("CheckResult")
        @Override
        public void onAddPicClick() {
            //??????????????????
            RxPermissions rxPermission = new RxPermissions(getActivity());
            rxPermission.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Permission>() {
                        @Override
                        public void accept(Permission permission) {
                            if (permission.granted) {// ???????????????????????????
                                //??????????????????????????????????????????dialog
                                Log.e("stormxz", " get current item = " + mActivity.getCurrentViewPageIndex());
                                if (mActivity.getCurrentViewPageIndex() == 5) {
                                    showVideoAndPic();
                                } else {
                                    showPop(false);
                                }

                                //????????????????????????????????????????????? ????????????????????????
//                                showAlbum();
                            } else {
                                Toast.makeText(getContext(), "??????", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        @Override
        public void delete(List<LocalMedia> list) {
            // ???????????????????????????
            updateDeleteImages(list);
        }
    };


    public void updateDeleteImages(List<LocalMedia> list) {

    }

    protected void getPremission() {

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // ???????????????
            //            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //                // ????????????????????????????????????????????????????????????????????????????????????
            //                Log.d("weiwei","No No No ");
            //            } else {
            //???????????????
            Log.e("stormxz", "shenqing quanxian ");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            //            }
        } else {
            showPop(true);
        }

    }

    protected void showPop(boolean isSelectLogo) {
        //??????
        Log.e("stormxz", " select = " + selectList.size());
        int maxRealSelectNum = 2;
        boolean needCrop = false;
        if (!isSelectLogo) {
            if (selectList != null) {
                if (selectList.size() == 0) {
                    maxRealSelectNum = 2;
                } else if (selectList.size() == 1) {
                    maxRealSelectNum = 1;
                }
            }
            needCrop = true;
        } else {
            maxRealSelectNum = 4;
            needCrop = false;
        }

        PictureSelector.create(getActivity())
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(maxRealSelectNum)
                .minSelectNum(1)
                .imageSpanCount(4)
                .enableCrop(needCrop)// ????????????
                .compress(true)// ????????????
                //.sizeMultiplier(0.5f)// glide ?????????????????? 0~1?????? ????????? .glideOverride()??????
                .glideOverride(160, 160)// glide ???????????????????????????????????????????????????????????????????????????????????????
                .withAspectRatio(4, 3)// ???????????? ???16:9 3:2 3:4 1:1 ????????????
                .selectionMode(PictureConfig.MULTIPLE)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    protected void showVideoAndPic() {
        PictureSelector.create(getActivity())
                .openGallery(PictureMimeType.ofAll())
                .maxSelectNum(3)
                .minSelectNum(1)
                .imageSpanCount(4)
                .compress(true)// ????????????
                //.sizeMultiplier(0.5f)// glide ?????????????????? 0~1?????? ????????? .glideOverride()??????
                .glideOverride(160, 160)// glide ???????????????????????????????????????????????????????????????????????????????????????
                .withAspectRatio(4, 3)// ???????????? ???16:9 3:2 3:4 1:1 ????????????
                .selectionMode(PictureConfig.MULTIPLE)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("stormxz", "onActivityResult 1111");
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("stormxz", "onActivityResult 2222");
        List<LocalMedia> images;
        if (resultCode == RESULT_OK && adapter != null) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {// ????????????????????????

                images = PictureSelector.obtainMultipleResult(data);
                selectList.addAll(images);

                //selectList = PictureSelector.obtainMultipleResult(data);

                // ?????? LocalMedia ??????????????????path
                // 1.media.getPath(); ?????????path
                // 2.media.getCutPath();????????????path????????????media.isCut();?????????true
                // 3.media.getCompressPath();????????????path????????????media.isCompressed();?????????true
                // ????????????????????????????????????????????????????????????????????????????????????


                adapter.setList(selectList);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
