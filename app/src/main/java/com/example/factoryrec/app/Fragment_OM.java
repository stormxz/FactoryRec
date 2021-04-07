package com.example.factoryrec.app;

import android.Manifest;
import android.app.Activity;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.factoryrec.R;
import com.example.factoryrec.selector.MultiImageSelectorActivity;
import com.example.factoryrec.ui.ScrollGridView;
import com.example.factoryrec.util.DensityUtil;
import com.example.factoryrec.util.FileUtil;
import com.example.factoryrec.util.ImageUtil;
import com.example.factoryrec.util.ScreenUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Fragment_OM extends MainFragment {

    // 图片附件
    private ScrollGridView gViewPostPicture = null;

    // 图片附件数据
    private List<Bitmap> postPictureData;

    // 图片文件路径
    private ArrayList<String> filePaths;

    // 图片
    public static final int PHOTOS = 2;

    // 图片附件适配器
    private GridViewAdapter postPictureAdapter;

    private Bitmap bitmap;

    // 图片尺寸大小
    private int PHOTO_SIZE = 60;

    // 图片大小上限
    private static final long MAX_PHOTO_SIZE = 5 * 1048576;

    // 图片上限个数
    private static final int IMAGE_COUNT_MAX = 8;

    // 子线程
    private Thread mThread;

    private List<File> photoList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_om, container, false);
        gViewPostPicture = view.findViewById(R.id.gViewPostPicture_om);

        // 初始化数据
        initData();
        postPictureAdapter = new GridViewAdapter();
        gViewPostPicture.setAdapter(postPictureAdapter);
        // 设置视图组件监听器
        setViewListener();
        return view;
    }

    private void setViewListener() {
        // 设置图片附件点击监听器
        gViewPostPicture.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (Build.VERSION.SDK_INT < 23) {
                    showPictureSelector();
                } else {
                    getPremission();
                }

            }
        });
    }

    /**
     * 打开图片选择
     */
    private void showPictureSelector() {

        Intent intent = new Intent(getContext(), MultiImageSelectorActivity.class);

        // 是否显示拍摄图片
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);

        // 最大可选择图片数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, IMAGE_COUNT_MAX);

        // 选择模式
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);

        // 默认选择
        if (filePaths != null && filePaths.size() > 0) {
            intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, filePaths);
        }
        getActivity().startActivityForResult(intent, PHOTOS);
    }

    private void getPremission() {

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
            showPictureSelector();
        }

    }


    private void initData() {
        filePaths = new ArrayList<String>();
        PHOTO_SIZE = (ScreenUtil.getScreenWidth(getContext()) - DensityUtil.dp2px(getContext(), 50)) / 5;
        postPictureData = new ArrayList<Bitmap>();
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.btn_image_add);
        postPictureData.add(bitmap);

        photoList = new ArrayList<File>();
    }

    /**
     * 图片附件适配器
     */
    private class GridViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            return postPictureData.size();
        }

        @Override
        public Object getItem(int position) {

            return postPictureData.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            GridViewAdapter.ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.item_forum_picture, null);
                holder = new GridViewAdapter.ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.imageDelete = (ImageView) convertView.findViewById(R.id.imageDelete);
                //holder.imageView.setLayoutParams(new FrameLayout.LayoutParams(PHOTO_SIZE, PHOTO_SIZE));
                convertView.setTag(holder);
            } else {
                holder = (GridViewAdapter.ViewHolder) convertView.getTag();
            }
            try {
                final Bitmap bitMap = postPictureData.get(position);
                if (bitMap != null) {
                    Log.d("stormxz", "bitmap != null ");
                    holder.imageView.setBackgroundDrawable(new BitmapDrawable(bitMap));
                } else {
                    Log.d("stormxz", "bitmap == null ");
                    holder.imageView.setBackgroundDrawable(
                            getResources().getDrawable(R.drawable.image_default));
                }

                if (bitMap == bitmap) {
                    holder.imageDelete.setVisibility(View.GONE);
                } else {
                    holder.imageDelete.setVisibility(View.VISIBLE);
                }

                //删除图标按钮
                holder.imageDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        postPictureData.remove(position);
                        if (!postPictureData.contains(bitmap)) {
                            postPictureData.add(bitmap);
                        }
                        filePaths.remove(position);
                        notifyDataSetChanged();
                    }
                });
            } catch (Exception e) {
                Log.d("stormxz", e.getStackTrace() + "");
            }
            return convertView;
        }

        private class ViewHolder {

            private ImageView imageView;
            private ImageView imageDelete;
        }

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

    /**
     * 刷新图片列表
     */
    private void setImageList(final List<String> imageUris) {

        if (imageUris != null) {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        filePaths.clear();
                        List<Bitmap> bitmaps = new ArrayList<Bitmap>();
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        for (String path : imageUris) {
                            long fileSize = FileUtil.getFileSize(new File(path));
                            if (fileSize > 0.2 * 1048576) {
                                options.inSampleSize = 1;
                            }
                            if (fileSize > 0.4 * 1048576) {
                                options.inSampleSize = 2;
                            }
                            if (fileSize > 0.6 * 1048576) {
                                options.inSampleSize = 4;
                            }
                            if (fileSize > 0.8 * 1048576) {
                                options.inSampleSize = 6;
                            }
                            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                            if (bitmap != null) {
                                Bitmap zoomBitmap = ImageUtil.zoomBitmap(bitmap, PHOTO_SIZE, PHOTO_SIZE);
                                bitmaps.add(zoomBitmap);
                                bitmap.recycle();
                                bitmap = null;
                                filePaths.add(path);
                            }
                        }
                        postPictureData.clear();
                        postPictureData.addAll(bitmaps);
                        if (bitmaps.size() < 3) {
                            postPictureData.add(bitmap);
                        }

                        // 通知刷新附件列表
                        mHandler.sendEmptyMessage(1);
                    } catch (Exception e) {
                        mHandler.sendEmptyMessage(1);
                    }
                }
            });
            mThread.start();
        }
    }

    // Handler
    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (postPictureAdapter != null) {
                    postPictureAdapter.notifyDataSetChanged();
                }
            }
        }
    };
}

