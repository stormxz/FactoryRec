package com.example.factoryrec.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.factoryrec.R;
import com.example.factoryrec.selector.MultiImageSelectorActivity;
import com.example.factoryrec.util.FileUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Fragment_Home extends MainFragment implements View.OnClickListener {

    private static final String TAG = "Fragment_Home";

    public static final int SCAN_REQUEST_CODE = 49374;

    private Spinner mCustomSpinner;
    private Spinner mMachineSpinner;
    private EditText mSNText;
    private ImageView mScanImage;

    private List mData;

    private String[] mCustoms = {"张三", "李四", "王二", "盖伦", "德莱厄斯"};
    private ArrayAdapter mCustomAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_home, container, false);
        mCustomSpinner = (Spinner) view.findViewById(R.id.custom_spinner);
        mMachineSpinner = (Spinner) view.findViewById(R.id.machine_spinner);
        mSNText = view.findViewById(R.id.sn_editText);
        mScanImage = view.findViewById(R.id.scan_image);
        mScanImage.setOnClickListener(this);
        gViewPostPicture = view.findViewById(R.id.gViewPostPicture_home);
        super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initHomeData();
    }

    private void initHomeData() {
        mData = new ArrayList();
        for (int i = 0; i < mCustoms.length; i++) {
            mData.add(mCustoms[i]);
        }
        mCustomAdapter = new ArrayAdapter(mActivity, android.R.layout.simple_spinner_item, mData);
        mCustomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCustomSpinner.setAdapter(mCustomAdapter);
        mCustomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "select : " + mCustomSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mMachineSpinner.setAdapter(mCustomAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_image:
                //已导入Google zxing二维码功能及第三方已实现的二维码扫描功能及界面，通过以下代码即可便捷启动扫码
                IntentIntegrator integrator = new IntentIntegrator(mActivity);
                integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan a barcode");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.initiateScan();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("cc", "requestCode = " + requestCode + ", resultCode = " + resultCode);
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
                case SCAN_REQUEST_CODE: IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    if (result != null) {
                        if (result.getContents() == null) {
                        } else {
                            if (mSNText != null) {
                                mSNText.setText(result.getContents());
                                mItem.setSN(result.getContents());
                            }
                        }
                    } else {
                        super.onActivityResult(requestCode, resultCode, data);
                    }
                    break;
            }
        }
    }
}
