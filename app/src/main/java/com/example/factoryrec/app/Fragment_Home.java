package com.example.factoryrec.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.factoryrec.R;
import com.example.factoryrec.selector.MultiImageSelectorActivity;
import com.example.factoryrec.ui.SpinnerEditDialog;
import com.example.factoryrec.util.FileUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Fragment_Home extends MainFragment implements View.OnClickListener {

    private static final String TAG = "Fragment_Home";

    public static final int SCAN_REQUEST_CODE = 49374;

    //下拉菜单
    private TextView mCustom_Spinner;
    private TextView mMachine_Spinner;
    private TextView mPhenom_Spinner;
    private TextView mPhenom2_Spinner;
    private TextView mSite_Spinner;
    private TextView mPosition_Spinner;

    //下拉菜单编辑
    private ImageView mCustom_Edit_Image;
    private ImageView mMachine_Edit_Image;
    private ImageView mPhenom_Edit_Image;
    private ImageView mPhenom2_Edit_Image;
    private ImageView mSite_Edit_Image;
    private ImageView mPosition_Edit_Image;

    //二维码
    private EditText mSNText;
    private ImageView mScanImage;

    private SharedPreferences mSpinner_Value;
    private SharedPreferences.Editor mSpinner_Edit;

    private List mData;

    private Resources res;
    private static final String REGULAR_EXPRESSION = "#";
    private String[] mCustoms_Array = {"张三", "李四", "王二", "盖伦", "德莱厄斯", "德莱厄斯", "德莱厄斯", "德莱厄斯", "德莱厄斯"};
    private String[] mMachine_Array;
    private String[] mPhenom_Array, mPhenom_Array2_CP, mPhenom_Array2_CM, mPhenom_Array2_CF, mPhenom_Array2_CD, mPhenom_Array2_CA, mPhenom_Array2_CN;
    private ArrayAdapter mCustomAdapter;

    private int mCustom_which = -1;
    private int mMachine_which = -1;
    private int mPhenom_which = -1;
    private int mPhenom2_which = -1;
    private int mSite_which = -1;
    private int mPosition_which = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_home, container, false);
        //下拉菜单
        mCustom_Spinner = (TextView) view.findViewById(R.id.custom_spinner);
        mMachine_Spinner = (TextView) view.findViewById(R.id.machine_spinner);
        mPhenom_Spinner = (TextView) view.findViewById(R.id.phenom_spinner);
        mPhenom2_Spinner = (TextView) view.findViewById(R.id.phenom_spinner2);
        mSite_Spinner = (TextView) view.findViewById(R.id.site_spinner);
        mPosition_Spinner = (TextView) view.findViewById(R.id.position_spinner);
        mCustom_Spinner.setOnClickListener(this);
        mMachine_Spinner.setOnClickListener(this);
        mPhenom_Spinner.setOnClickListener(this);
        mPhenom2_Spinner.setOnClickListener(this);
        mSite_Spinner.setOnClickListener(this);
        mPosition_Spinner.setOnClickListener(this);

        //下拉菜单编辑
        mCustom_Edit_Image = (ImageView) view.findViewById(R.id.edit_image_custom);
        mMachine_Edit_Image = (ImageView) view.findViewById(R.id.edit_image_machine);
        mPhenom_Edit_Image = (ImageView) view.findViewById(R.id.edit_image_phenom);
        mPhenom2_Edit_Image = (ImageView) view.findViewById(R.id.edit_image_phenom_2);
        mSite_Edit_Image = (ImageView) view.findViewById(R.id.edit_image_site);
        mPosition_Edit_Image = (ImageView) view.findViewById(R.id.edit_image_position);
        mCustom_Edit_Image.setOnClickListener(this);
        mMachine_Edit_Image.setOnClickListener(this);
        mPhenom_Edit_Image.setOnClickListener(this);
        mPhenom2_Edit_Image.setOnClickListener(this);
        mSite_Edit_Image.setOnClickListener(this);
        mPosition_Edit_Image.setOnClickListener(this);

        mPhenom_Spinner.addTextChangedListener(new EditextOnChangeListnerdata());

        //二维码
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
        res = mActivity.getResources();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSharedPreferenceData();
    }

    private void initSharedPreferenceData() {
        mSpinner_Value = mActivity.getSharedPreferences("spinner_value", Context.MODE_PRIVATE);
        mSpinner_Edit = mSpinner_Value.edit();
        boolean inited = mSpinner_Value.contains("bad_phenomenon");
        if (!inited) {
            mPhenom_Array = res.getStringArray(R.array.bad_phenomenon);
            mPhenom_Array2_CP = res.getStringArray(R.array.CP00偏光版类不良);
            mPhenom_Array2_CM = res.getStringArray(R.array.CM00品味性不良);
            mPhenom_Array2_CF = res.getStringArray(R.array.CF00功能性不良);
            mPhenom_Array2_CD = res.getStringArray(R.array.CD00Dot类不良);
            mPhenom_Array2_CA = res.getStringArray(R.array.CA00外观类不良);
            mPhenom_Array2_CN = res.getStringArray(R.array.CN00Other);
            mSpinner_Edit.putString("bad_phenomenon", array2StringBuilder(mPhenom_Array).toString());
            mSpinner_Edit.putString("CP00偏光版类不良", array2StringBuilder(mPhenom_Array2_CP).toString());
            mSpinner_Edit.putString("CM00品味性不良", array2StringBuilder(mPhenom_Array2_CM).toString());
            mSpinner_Edit.putString("CF00功能性不良", array2StringBuilder(mPhenom_Array2_CF).toString());
            mSpinner_Edit.putString("CD00Dot类不良", array2StringBuilder(mPhenom_Array2_CD).toString());
            mSpinner_Edit.putString("CA00外观类不良", array2StringBuilder(mPhenom_Array2_CA).toString());
            mSpinner_Edit.putString("CN00Other", array2StringBuilder(mPhenom_Array2_CN).toString());
            mSpinner_Edit.commit();
        } else {
            mPhenom_Array = string2Array(mSpinner_Value.getString("bad_phenomenon", null));
            mPhenom_Array2_CP = string2Array(mSpinner_Value.getString("CP00偏光版类不良", null));
            mPhenom_Array2_CM = string2Array(mSpinner_Value.getString("CM00品味性不良", null));
            mPhenom_Array2_CF = string2Array(mSpinner_Value.getString("CF00功能性不良", null));
            mPhenom_Array2_CD = string2Array(mSpinner_Value.getString("CD00Dot类不良", null));
            mPhenom_Array2_CA = string2Array(mSpinner_Value.getString("CA00外观类不良", null));
            mPhenom_Array2_CN = string2Array(mSpinner_Value.getString("CN00Other", null));
        }
    }

    private StringBuilder array2StringBuilder(String[] array) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            stringBuilder.append(array[i]);
            if (i != array.length - 1) {
                stringBuilder.append(REGULAR_EXPRESSION);
            }
        }
        Log.i("cc", "stringBuilder = " + stringBuilder.toString());
        return stringBuilder;
    }

    private String[] string2Array(String string) {
        return string.split("#");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.custom_spinner:
                AlertDialog.Builder custom_Builder = new AlertDialog.Builder(mActivity);
                int custom_select = mCustom_which;
                custom_Builder.setSingleChoiceItems(mCustoms_Array, custom_select,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCustom_which = which;
                                mCustom_Spinner.setText(mCustoms_Array[which]);
                                mCustom_Spinner.setTextColor(mActivity.getColor(R.color.primary_light));
                                dialog.dismiss();
                            }
                        });
                custom_Builder.setTitle("客户");
                custom_Builder.create().show();
                break;
            case R.id.machine_spinner:
                AlertDialog.Builder machine_Builder = new AlertDialog.Builder(mActivity);
                int machine_select = mMachine_which;
                machine_Builder.setSingleChoiceItems(mCustoms_Array, machine_select,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mMachine_which = which;
                                mMachine_Spinner.setText(mCustoms_Array[which]);
                                mMachine_Spinner.setTextColor(mActivity.getColor(R.color.primary_light));
                                dialog.dismiss();
                            }
                        });
                machine_Builder.setTitle("机种");
                machine_Builder.create().show();
                break;
            case R.id.phenom_spinner:
                AlertDialog.Builder phenom_Builder = new AlertDialog.Builder(mActivity);
                int phenom_select = mPhenom_which;
                phenom_Builder.setSingleChoiceItems(mPhenom_Array, phenom_select,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPhenom_which = which;
                                mPhenom_Spinner.setText(mPhenom_Array[which]);
                                mPhenom_Spinner.setTextColor(mActivity.getColor(R.color.primary_light));
                                dialog.dismiss();
                            }
                        });
                phenom_Builder.setTitle("不良现象（一）");
                phenom_Builder.create().show();
                break;
            case R.id.phenom_spinner2:
                if (mPhenom_which == -1) {
                    Toast.makeText(mActivity, "请先选择一级菜单", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder phenom2_Builder = new AlertDialog.Builder(mActivity);
                int phenom2_select = mPhenom2_which;
                final String[] array_select = phenomIndex(mPhenom_which);
                phenom2_Builder.setSingleChoiceItems(array_select, phenom2_select,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPhenom2_which = which;
                                if (array_select != null) {
                                    mPhenom2_Spinner.setText(array_select[which]);
                                    mPhenom2_Spinner.setTextColor(mActivity.getColor(R.color.primary_light));
                                }
                                dialog.dismiss();
                            }
                        });
                phenom2_Builder.setTitle("不良现象（二）");
                phenom2_Builder.create().show();
                break;
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
            case R.id.edit_image_custom:
                SpinnerEditDialog.Builder builder = new SpinnerEditDialog.Builder(mActivity, mCustoms_Array);
                builder.setTitle("客户");
                builder.create().show();
                break;
        }
    }

    private String[] phenomIndex(int index) {
        switch (index) {
            case 0:
                return mPhenom_Array2_CP;
            case 1:
                return mPhenom_Array2_CM;
            case 2:
                return mPhenom_Array2_CF;
            case 3:
                return mPhenom_Array2_CD;
            case 4:
                return mPhenom_Array2_CA;
            case 5:
                return mPhenom_Array2_CN;
            default:
                return null;
        }
    }

    private void buildSpinnerEditLayout() {

    }

    private class EditextOnChangeListnerdata implements TextWatcher {
        private CharSequence tmp;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            mPhenom2_Spinner.setText(null);
        }

        @Override
        public void afterTextChanged(Editable editable) {
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
                case SCAN_REQUEST_CODE:
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
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
