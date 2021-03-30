package com.example.factoryrec.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.factoryrec.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class Fragment_Home extends MainFragment implements View.OnClickListener {

    private static final String TAG = "Fragment_Home";

    public static final int SCAN_REQUEST_CODE = 100;

    private EditText mSNText;
    private ImageView mScanImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_home, container, false);
        mSNText = view.findViewById(R.id.sn_editText);
        mScanImage = view.findViewById(R.id.scan_image);
        mScanImage.setOnClickListener(this);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
            } else {
                mSNText.setText(result.getContents());
                mItem.setSN(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
