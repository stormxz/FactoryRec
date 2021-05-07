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
        // 初始化数据
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
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(getActivity()).externalPicturePreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(getActivity()).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
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
            //获取写的权限
            RxPermissions rxPermission = new RxPermissions(getActivity());
            rxPermission.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Permission>() {
                        @Override
                        public void accept(Permission permission) {
                            if (permission.granted) {// 用户已经同意该权限
                                //第一种方式，弹出选择和拍照的dialog
                                showPop(false);

                                //第二种方式，直接进入相册，但是 是有拍照得按钮的
//                                showAlbum();
                            } else {
                                Toast.makeText(getContext(), "拒绝", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        @Override
        public void delete(List<LocalMedia> list) {
            // 删除图片，重新加载
            updateDeleteImages(list);
        }
    };


    public void updateDeleteImages(List<LocalMedia> list) {

    }

    protected void getPremission() {

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 没有权限。
            //            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //                // 用户拒绝过这个权限了，应该提示用户，为什么需要这个权限。
            //                Log.d("weiwei","No No No ");
            //            } else {
            //申请授权。
            Log.e("stormxz", "shenqing quanxian ");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            //            }
        } else {
            showPop(true);
        }

    }

    protected void showPop(boolean isSelectLogo) {
        //相册
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
                .enableCrop(needCrop)// 是否裁剪
                .compress(true)// 是否压缩
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .withAspectRatio(1, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
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
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {// 图片选择结果回调

                images = PictureSelector.obtainMultipleResult(data);
                selectList.addAll(images);

                //selectList = PictureSelector.obtainMultipleResult(data);

                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的


                adapter.setList(selectList);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
