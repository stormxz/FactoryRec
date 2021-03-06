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

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.factoryrec.R;
import com.example.factoryrec.selector.adapter.FolderAdapter;
import com.example.factoryrec.selector.adapter.ImageGridAdapter;
import com.example.factoryrec.selector.bean.Folder;
import com.example.factoryrec.selector.bean.Image;
import com.example.factoryrec.selector.utils.FileUtils;
import com.example.factoryrec.selector.utils.ScreenUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片选择
 *
 * @author yangcheng
 */
public class MultiImageSelectorFragment extends Fragment {

    public static final String TAG = "me.nereo.multi_image_selector.MultiImageSelectorFragment";

    private static final String KEY_TEMP_FILE = "key_temp_file";

    /** 最大图片选择次数，int类型 */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /** 图片选择模式，int类型 */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /** 是否显示相机，boolean类型 */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /** 默认选择的数据集 */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_result";
    /** 单选 */
    public static final int MODE_SINGLE = 0;
    /** 多选 */
    public static final int MODE_MULTI = 1;
    // 不同loader定义
    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;
    // 请求加载系统照相机
    private static final int REQUEST_CAMERA = 100;

    // 结果数据
    private ArrayList<String> resultList = new ArrayList<String>();
    // 文件夹数据
    private ArrayList<Folder> mResultFolder = new ArrayList<Folder>();

    // 图片Grid
    private GridView mGridView;
    private Callback mCallback;

    private ImageGridAdapter mImageAdapter;
    private FolderAdapter mFolderAdapter;

    private ListPopupWindow mFolderPopupWindow;

    // 类别
    private TextView mCategoryText;
    // 预览按钮
    private Button mPreviewBtn;
    // 底部View
    private View mPopupAnchorView;

    private int mDesireImageCount;

    private boolean hasFolderGened = false;
    private boolean mIsShowCamera = false;

    private File mTmpFile;

    // 图片预览
    public static final int RESULT_IMAGE = 101;

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        try {
            mCallback = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    "The Activity must implement MultiImageSelectorFragment.Callback interface...");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_multi_image, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // 选择图片数量
        mDesireImageCount = getArguments().getInt(EXTRA_SELECT_COUNT);

        // 图片选择模式
        final int mode = getArguments().getInt(EXTRA_SELECT_MODE);

        // 默认选择
        if (mode == MODE_MULTI) {
            ArrayList<String> tmp = getArguments().getStringArrayList(EXTRA_DEFAULT_SELECTED_LIST);
            if (tmp != null && tmp.size() > 0) {
                resultList = tmp;
            }
        }

        // 是否显示照相机
        mIsShowCamera = getArguments().getBoolean(EXTRA_SHOW_CAMERA, true);
        mImageAdapter = new ImageGridAdapter(getActivity(), mIsShowCamera, 3);
        // 是否显示选择指示器
        mImageAdapter.showSelectIndicator(mode == MODE_MULTI);

        mPopupAnchorView = view.findViewById(R.id.footer);

        mCategoryText = (TextView) view.findViewById(R.id.category_btn);
        // 初始化，加载所有图片
        mCategoryText.setText(R.string.folder_all);
        mCategoryText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (mFolderPopupWindow == null) {
                    createPopupFolderList();
                }

                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    mFolderPopupWindow.show();
                    int index = mFolderAdapter.getSelectIndex();
                    index = index == 0 ? index : index - 1;
                    mFolderPopupWindow.getListView().setSelection(index);
                }
            }
        });

        mPreviewBtn = (Button) view.findViewById(R.id.preview);

        // 初始化，按钮状态初始化
        if (resultList == null || resultList.size() <= 0) {
            mPreviewBtn.setText(R.string.preview);
            mPreviewBtn.setEnabled(false);
            mPreviewBtn.setVisibility(View.GONE);
        } else {
            mPreviewBtn.setText(getString(R.string.preview) + "(" + resultList.size() + ")");
            mPreviewBtn.setEnabled(true);
            mPreviewBtn.setVisibility(View.VISIBLE);
        }
        mPreviewBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // 图片预览
                Intent intent = new Intent(getActivity(), ImageViewPagerActivity.class);
                intent.putExtra("imageUris", (Serializable) resultList);
                intent.putExtra("position", 0);
                startActivityForResult(intent, RESULT_IMAGE);
            }
        });

        mGridView = (GridView) view.findViewById(R.id.grid);
        mGridView.setAdapter(mImageAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (mImageAdapter.isShowCamera()) {
                    // 如果显示照相机，则第一个Grid显示为照相机，处理特殊逻辑
                    if (i == 0) {
                        if (Build.VERSION.SDK_INT < 23) {
                            showCameraAction();
                        } else {
                            getPremission();
                        }

                    } else {
                        // 正常操作
                        Image image = (Image) adapterView.getAdapter().getItem(i);
                        selectImageFromGrid(image, mode);
                    }
                } else {
                    // 正常操作
                    Image image = (Image) adapterView.getAdapter().getItem(i);
                    selectImageFromGrid(image, mode);
                }
            }
        });
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (scrollState == SCROLL_STATE_FLING) {
                    Picasso.with(view.getContext()).pauseTag(TAG);
                } else {
                    Picasso.with(view.getContext()).resumeTag(TAG);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {

            }
        });

        mFolderAdapter = new FolderAdapter(getActivity());
    }

    /**
     * 创建弹出的ListView
     */
    private void createPopupFolderList() {

        Point point = ScreenUtils.getScreenSize(getActivity());
        int width = point.x;
        int height = (int) (point.y * (4.5f / 8.0f));
        mFolderPopupWindow = new ListPopupWindow(getActivity());
        mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mFolderPopupWindow.setAdapter(mFolderAdapter);
        mFolderPopupWindow.setContentWidth(width);
        mFolderPopupWindow.setWidth(width);
        mFolderPopupWindow.setHeight(height);
        mFolderPopupWindow.setAnchorView(mPopupAnchorView);
        mFolderPopupWindow.setModal(true);
        mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                mFolderAdapter.setSelectIndex(i);

                final int index = i;
                final AdapterView v = adapterView;

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        mFolderPopupWindow.dismiss();

                        if (index == 0) {
                            getActivity().getSupportLoaderManager().restartLoader(LOADER_ALL, null,
                                                                                  mLoaderCallback);
                            mCategoryText.setText(R.string.folder_all);
                            if (mIsShowCamera) {
                                mImageAdapter.setShowCamera(true);
                            } else {
                                mImageAdapter.setShowCamera(false);
                            }
                        } else {
                            Folder folder = (Folder) v.getAdapter().getItem(index);
                            if (null != folder) {
                                mImageAdapter.setData(folder.images);
                                mCategoryText.setText(folder.name);
                                // 设定默认选择
                                if (resultList != null && resultList.size() > 0) {
                                    mImageAdapter.setDefaultSelected(resultList);
                                }
                            }
                            mImageAdapter.setShowCamera(false);
                        }

                        // 滑动到最初始位置
                        mGridView.smoothScrollToPosition(0);
                    }
                }, 100);

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_TEMP_FILE, mTmpFile);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {

        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mTmpFile = (File) savedInstanceState.getSerializable(KEY_TEMP_FILE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        // 首次加载所有图片
        // new LoadImageTask().execute();
        getActivity().getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // 相机拍照完成后，返回图片路径
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (mTmpFile != null) {
                    if (mCallback != null) {
                        mCallback.onCameraShot(mTmpFile);
                    }
                }
            } else {
                while (mTmpFile != null && mTmpFile.exists()) {
                    boolean success = mTmpFile.delete();
                    if (success) {
                        mTmpFile = null;
                    }
                }
            }
        }

        // 图片预览完成
        if (requestCode == RESULT_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            resultList = (ArrayList<String>) data.getSerializableExtra("imagePath");
            if (resultList != null) {
                if (mCallback != null) {
                    mCallback.onMultiImageSelected(resultList);
                }
                mImageAdapter.clearSelect();
                mImageAdapter.setDefaultSelected(resultList);
                if (resultList == null || resultList.size() <= 0) {
                    mPreviewBtn.setText(R.string.preview);
                    mPreviewBtn.setEnabled(false);
                    mPreviewBtn.setVisibility(View.GONE);
                } else {
                    mPreviewBtn.setText(getString(R.string.preview) + "(" + resultList.size() + ")");
                    mPreviewBtn.setEnabled(true);
                    mPreviewBtn.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        if (mFolderPopupWindow != null) {
            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            }
        }
        super.onConfigurationChanged(newConfig);
    }


    private void getPremission() {

        Log.d("weiwei", "getPremission");
        if (ContextCompat.checkSelfPermission(MultiImageSelectorFragment.this.getActivity(),
                                              Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // 没有权限。
            //            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //                // 用户拒绝过这个权限了，应该提示用户，为什么需要这个权限。
            //                Log.d("weiwei","No No No ");
            //            } else {
            //申请授权。
            Log.d("weiwei", "shenqing quanxian ");
            ActivityCompat.requestPermissions(MultiImageSelectorFragment.this.getActivity(),
                                              new String[]{Manifest.permission.CAMERA}, 0);
            //            }
        } else {
            showCameraAction();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意，可以去放肆了。
                    showCameraAction();
                } else {
                    // 权限被用户拒绝了，洗洗睡吧。
                }
                break;

        }
    }


    /**
     * 选择相机
     */
    private void showCameraAction() {

        // 判断选择数量问题
        if (mDesireImageCount <= resultList.size()) {
            Toast.makeText(getActivity(), R.string.msg_amount_limit, Toast.LENGTH_SHORT).show();
            return;
        }

        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            try {
                mTmpFile = FileUtils.createTmpFile(getActivity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mTmpFile != null && mTmpFile.exists()) {
                Uri uri = Uri.fromFile(mTmpFile);
                try {
                    ContentValues values = new ContentValues(1);
                    // values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                    values.put(MediaStore.Images.Media.DATA, mTmpFile.getAbsolutePath());
                    uri = getActivity().getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    // 文件共享问题
                    cameraIntent.addFlags(
                            Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(cameraIntent, REQUEST_CAMERA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "图片错误", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 选择图片操作
     *
     * @param image
     */
    private void selectImageFromGrid(Image image, int mode) {

        if (image != null) {
            // 多选模式
            if (mode == MODE_MULTI) {
                if (resultList.contains(image.path)) {
                    resultList.remove(image.path);
                    if (resultList.size() != 0) {
                        mPreviewBtn.setEnabled(true);
                        mPreviewBtn.setText(
                                getResources().getString(R.string.preview) + "(" + resultList.size() + ")");
                        mPreviewBtn.setVisibility(View.VISIBLE);
                    } else {
                        mPreviewBtn.setEnabled(false);
                        mPreviewBtn.setText(R.string.preview);
                        mPreviewBtn.setVisibility(View.GONE);
                    }
                    if (mCallback != null) {
                        mCallback.onImageUnselected(image.path);
                    }
                } else {
                    // 判断选择数量问题
                    if (mDesireImageCount <= resultList.size()) {
                        Toast.makeText(getActivity(), R.string.msg_amount_limit, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    resultList.add(image.path);
                    mPreviewBtn.setEnabled(true);
                    mPreviewBtn.setText(
                            getResources().getString(R.string.preview) + "(" + resultList.size() + ")");
                    mPreviewBtn.setVisibility(View.VISIBLE);
                    if (mCallback != null) {
                        mCallback.onImageSelected(image.path);
                    }
                }
                mImageAdapter.select(image);
            } else if (mode == MODE_SINGLE) {
                // 单选模式
                if (mCallback != null) {
                    mCallback.onSingleImageSelected(image.path);
                }
            }
        }
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback =
            new LoaderManager.LoaderCallbacks<Cursor>() {

                private final String[] IMAGE_PROJECTION =
                        {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME,
                                MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.MIME_TYPE,
                                MediaStore.Images.Media.SIZE, MediaStore.Images.Media._ID};

                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {

                    if (id == LOADER_ALL) {
                        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                                                                     MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                                     IMAGE_PROJECTION,
                                                                     IMAGE_PROJECTION[4] + ">0 AND "
                                                                             + IMAGE_PROJECTION[3] + "=? OR "
                                                                             + IMAGE_PROJECTION[3] + "=? ",
                                                                     new String[]{"image/jpeg", "image/png"},
                                                                     IMAGE_PROJECTION[2] + " DESC");
                        return cursorLoader;
                    } else if (id == LOADER_CATEGORY) {
                        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                                                                     MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                                     IMAGE_PROJECTION,
                                                                     IMAGE_PROJECTION[4] + ">0 AND "
                                                                             + IMAGE_PROJECTION[0]
                                                                             + " like '%" + args
                                                                             .getString("path") + "%'", null,
                                                                     IMAGE_PROJECTION[2] + " DESC");
                        return cursorLoader;
                    }

                    return null;
                }

                private boolean fileExist(String path) {

                    if (!TextUtils.isEmpty(path)) {
                        return new File(path).exists();
                    }
                    return false;
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

                    if (data != null) {
                        if (data.getCount() > 0) {
                            List<Image> images = new ArrayList<Image>();
                            data.moveToFirst();
                            do {
                                String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                                String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                                long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                                Image image = null;
                                if (fileExist(path)) {
                                    image = new Image(path, name, dateTime);
                                    images.add(image);
                                }
                                if (!hasFolderGened) {
                                    // 获取文件夹名称
                                    File folderFile = new File(path).getParentFile();
                                    if (folderFile != null && folderFile.exists()) {
                                        String fp = folderFile.getAbsolutePath();
                                        Folder f = getFolderByPath(fp);
                                        if (f == null) {
                                            Folder folder = new Folder();
                                            folder.name = folderFile.getName();
                                            folder.path = fp;
                                            folder.cover = image;
                                            List<Image> imageList = new ArrayList<Image>();
                                            imageList.add(image);
                                            folder.images = imageList;
                                            mResultFolder.add(folder);
                                        } else {
                                            f.images.add(image);
                                        }
                                    }
                                }

                            } while (data.moveToNext());

                            mImageAdapter.setData(images);
                            // 设定默认选择
                            if (resultList != null && resultList.size() > 0) {
                                mImageAdapter.setDefaultSelected(resultList);
                            }

                            if (!hasFolderGened) {
                                mFolderAdapter.setData(mResultFolder);
                                hasFolderGened = true;
                            }

                        }
                    }
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {

                }
            };

    private Folder getFolderByPath(String path) {

        if (mResultFolder != null) {
            for (Folder folder : mResultFolder) {
                if (TextUtils.equals(folder.path, path)) {
                    return folder;
                }
            }
        }
        return null;
    }

    /**
     * 回调接口
     */
    public interface Callback {

        void onSingleImageSelected(String path);

        void onMultiImageSelected(List<String> fileFath);

        void onImageSelected(String path);

        void onImageUnselected(String path);

        void onCameraShot(File imageFile);
    }
}
