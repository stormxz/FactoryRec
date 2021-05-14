package com.example.factoryrec.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.factoryrec.R;
import com.example.factoryrec.selector.MultiImageSelectorActivity;
import com.example.factoryrec.ui.SpinnerEditDialog;
import com.example.factoryrec.util.FileUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


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

    //发生时间
    private TextView mTime1_Text;
    private TextView mTime2_Text;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;

    private SharedPreferences mSpinner_Value;
    private SharedPreferences.Editor mSpinner_Edit;

    private List mData;

    private Resources res;
    private static final String REGULAR_EXPRESSION = "#";
    private String[] mCustoms_Array;
    private String[] mMachine_Array, mSite_Array, mPosition_Array;
    private String[] mPhenom_Array, mPhenom_Array2_CP, mPhenom_Array2_CM, mPhenom_Array2_CF, mPhenom_Array2_CD, mPhenom_Array2_CA, mPhenom_Array2_CN;
    private String mFirstMenuName;
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
        mRecyclerView = view.findViewById(R.id.recycler_home);
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

        mPhenom_Spinner.addTextChangedListener(new EditextOnChangeListenerData());

        //二维码
        mSNText = view.findViewById(R.id.sn_editText);
        mScanImage = view.findViewById(R.id.scan_image);
        mScanImage.setOnClickListener(this);

        //发生时间
        mTime1_Text = view.findViewById(R.id.time1_text);
        mTime2_Text = view.findViewById(R.id.time2_text);
        mTime1_Text.setOnClickListener(this);
        mTime2_Text.setOnClickListener(this);

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
        initTimeData();
        setTime();
    }

    private void initSharedPreferenceData() {
        mSpinner_Value = mActivity.getSharedPreferences("spinner_value", Context.MODE_PRIVATE);
        mSpinner_Edit = mSpinner_Value.edit();
        boolean inited = mSpinner_Value.contains("bad_phenomenon");
        Log.i("cc", "inited = " + inited);
        if (!inited) {
            //不良现象
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
            //不良位置
            mPosition_Array = res.getStringArray(R.array.不良位置);
            mSpinner_Edit.putString("不良位置", array2StringBuilder(mPosition_Array).toString());
            mSpinner_Edit.commit();
        } else {
            mCustoms_Array = string2Array(mSpinner_Value.getString("客户", null));
            mMachine_Array = string2Array(mSpinner_Value.getString("机种", null));
            mSite_Array = string2Array(mSpinner_Value.getString("发生站点", null));
            mPhenom_Array = string2Array(mSpinner_Value.getString("bad_phenomenon", null));
            mPhenom_Array2_CP = string2Array(mSpinner_Value.getString("CP00偏光版类不良", null));
            mPhenom_Array2_CM = string2Array(mSpinner_Value.getString("CM00品味性不良", null));
            mPhenom_Array2_CF = string2Array(mSpinner_Value.getString("CF00功能性不良", null));
            mPhenom_Array2_CD = string2Array(mSpinner_Value.getString("CD00Dot类不良", null));
            mPhenom_Array2_CA = string2Array(mSpinner_Value.getString("CA00外观类不良", null));
            mPhenom_Array2_CN = string2Array(mSpinner_Value.getString("CN00Other", null));
            mPosition_Array = string2Array(mSpinner_Value.getString("不良位置", null));
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
        return stringBuilder;
    }

    private String[] string2Array(String string) {
        return string != null ? string.split("#") : null;
    }

    private void initTimeData() {
        Calendar d = Calendar.getInstance(Locale.CHINA);
        // 创建一个日历引用d，通过静态方法getInstance() 从指定时区 Locale.CHINA 获得一个日期实例
        Date myDate = new Date();
        // 创建一个Date实例
        d.setTime(myDate);
        // 设置日历的时间，把一个新建Date实例myDate传入
        mYear = d.get(Calendar.YEAR);
        mMonth = d.get(Calendar.MONTH);
        mDay = d.get(Calendar.DAY_OF_MONTH);
        mHour = d.get(Calendar.HOUR_OF_DAY); // 0-23
        mMinute = d.get(Calendar.MINUTE);
    }

    protected void setTime() {
        int m = mMonth + 1;
        mTime1_Text.setText(mYear + "/" + m + "/" + mDay);
        mTime2_Text.setText(FormatTime(mHour) + ":" + FormatTime(mMinute));
        mItem.setOccDate(mYear + "/" + m + "/" + mDay);
        mItem.setOccTime(FormatTime(mHour) + ":" + FormatTime(mMinute));
    }

    private String FormatTime(int time) {
        String str;
        if (time < 10) {
            str = "0" + time;
        } else {
            str = "" + time;
        }
        return str;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.custom_spinner:   //客户下拉菜单
                AlertDialog.Builder custom_Builder = new AlertDialog.Builder(mActivity);
                int custom_select = mCustom_which;
                custom_Builder.setSingleChoiceItems(mCustoms_Array, custom_select,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCustom_which = which;
                                mCustom_Spinner.setText(mCustoms_Array[which]);
                                mItem.setCustomer(mCustoms_Array[which]);
                                mCustom_Spinner.setTextColor(mActivity.getColor(R.color.primary_light));
                                dialog.dismiss();
                            }
                        });
                custom_Builder.setTitle("客户");
                custom_Builder.create().show();
                break;
            case R.id.machine_spinner:   //机种下拉菜单
                AlertDialog.Builder machine_Builder = new AlertDialog.Builder(mActivity);
                int machine_select = mMachine_which;
                machine_Builder.setSingleChoiceItems(mMachine_Array, machine_select,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mMachine_which = which;
                                mMachine_Spinner.setText(mMachine_Array[which]);
                                mItem.setMachineType(mMachine_Array[which]);
                                mMachine_Spinner.setTextColor(mActivity.getColor(R.color.primary_light));
                                dialog.dismiss();
                            }
                        });
                machine_Builder.setTitle("机种");
                machine_Builder.create().show();
                break;
            case R.id.phenom_spinner:   //不良现象一级下拉菜单
                AlertDialog.Builder phenom_Builder = new AlertDialog.Builder(mActivity);
                int phenom_select = mPhenom_which;
                phenom_Builder.setSingleChoiceItems(mPhenom_Array, phenom_select,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPhenom_which = which;
                                mPhenom_Spinner.setText(mPhenom_Array[which]);
                                mItem.setBadPhenom(mPhenom_Array[which]);
                                mPhenom_Spinner.setTextColor(mActivity.getColor(R.color.primary_light));
                                dialog.dismiss();
                            }
                        });
                phenom_Builder.setTitle("不良大类");
                phenom_Builder.create().show();
                break;
            case R.id.phenom_spinner2:   //不良现象二级下拉菜单
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
                                    mItem.setBadPhenom2(array_select[which]);
                                    mPhenom2_Spinner.setTextColor(mActivity.getColor(R.color.primary_light));
                                }
                                dialog.dismiss();
                            }
                        });
                phenom2_Builder.setTitle("不良现象");
                phenom2_Builder.create().show();
                break;
            case R.id.site_spinner:   //发生站点下拉菜单
                AlertDialog.Builder site_Builder = new AlertDialog.Builder(mActivity);
                int site_select = mSite_which;
                site_Builder.setSingleChoiceItems(mSite_Array, site_select,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSite_which = which;
                                mSite_Spinner.setText(mSite_Array[which]);
                                mItem.setOccSite(mSite_Array[which]);
                                mSite_Spinner.setTextColor(mActivity.getColor(R.color.primary_light));
                                dialog.dismiss();
                            }
                        });
                site_Builder.setTitle("发生站点");
                site_Builder.create().show();
                break;
            case R.id.position_spinner:   //不良位置下拉菜单
                AlertDialog.Builder position_Builder = new AlertDialog.Builder(mActivity);
                int position_select = mPosition_which;
                position_Builder.setSingleChoiceItems(mPosition_Array, position_select,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPosition_which = which;
                                mPosition_Spinner.setText(mPosition_Array[which]);
                                mItem.setBadPosition(mPosition_Array[which]);
                                mPosition_Spinner.setTextColor(mActivity.getColor(R.color.primary_light));
                                dialog.dismiss();
                            }
                        });
                position_Builder.setTitle("不良位置");
                position_Builder.create().show();
                break;
            case R.id.scan_image:    //二维码扫描按键
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA}, 0);
                } else {
                    startScanCode();
                }

                break;
            case R.id.time1_text:    //选择日期
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mYear = year;
                        mMonth = month;
                        mDay = dayOfMonth;
                        setTime();
                    }
                }, mYear, mMonth, mDay).show();
                break;
            case R.id.time2_text:    //选择时间
                new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHour = hourOfDay;
                        mMinute = minute;
                        setTime();
                    }
                }, mHour, mMinute, true).show();
                break;
            case R.id.edit_image_custom:      //客户下拉菜单编辑
                buildSpinnerEditLayout("客户", mCustoms_Array);
                break;
            case R.id.edit_image_machine:     //机种下拉菜单编辑
                buildSpinnerEditLayout("机种", mMachine_Array);
                break;
            case R.id.edit_image_phenom:      //不良现象下拉菜单编辑
                buildSpinnerEditLayout("不良现象一", mPhenom_Array);
                break;
            case R.id.edit_image_phenom_2:    //不良现象二下拉菜单编辑
                buildSpinner2EditLayout("不良现象二", mPhenom_Array);
                break;
            case R.id.edit_image_site:        //发生站点下拉菜单编辑
                buildSpinnerEditLayout("发生站点", mSite_Array);
                break;
            case R.id.edit_image_position:    //不良位置下拉菜单编辑
                buildSpinnerEditLayout("不良位置", mPosition_Array);
                break;
        }
    }

    public String[] phenomIndex(int index) {
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

    private void buildSpinnerEditLayout(final String title, String[] array) {
        final SpinnerEditDialog.Builder builder = new SpinnerEditDialog.Builder(mActivity, array);
        builder.setTitle(title);
        builder.setButtonConfirm(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (title) {
                    case "客户":
                        mCustoms_Array = builder.getNewArray();
                        mSpinner_Edit.putString(title, array2StringBuilder(mCustoms_Array).toString());
                        break;
                    case "机种":
                        mMachine_Array = builder.getNewArray();
                        mSpinner_Edit.putString(title, array2StringBuilder(mMachine_Array).toString());
                        break;
                    case "不良现象一":
                        mPhenom_Array = builder.getNewArray();
                        mSpinner_Edit.putString("bad_phenomenon", array2StringBuilder(mPhenom_Array).toString());
                        break;
                    case "CP00偏光版类不良":
                        mPhenom_Array2_CP = builder.getNewArray();
                        mSpinner_Edit.putString(title, array2StringBuilder(mPhenom_Array2_CP).toString());
                        break;
                    case "CM00品味性不良":
                        mPhenom_Array2_CM = builder.getNewArray();
                        mSpinner_Edit.putString(title, array2StringBuilder(mPhenom_Array2_CM).toString());
                        break;
                    case "CF00功能性不良":
                        mPhenom_Array2_CF = builder.getNewArray();
                        mSpinner_Edit.putString(title, array2StringBuilder(mPhenom_Array2_CF).toString());
                        break;
                    case "CA00外观类不良":
                        mPhenom_Array2_CA = builder.getNewArray();
                        mSpinner_Edit.putString(title, array2StringBuilder(mPhenom_Array2_CA).toString());
                        break;
                    case "CD00Dot类不良":
                        mPhenom_Array2_CD = builder.getNewArray();
                        mSpinner_Edit.putString(title, array2StringBuilder(mPhenom_Array2_CD).toString());
                        break;
                    case "CN00Other":
                        mPhenom_Array2_CN = builder.getNewArray();
                        mSpinner_Edit.putString(title, array2StringBuilder(mPhenom_Array2_CN).toString());
                        break;
                    case "发生站点":
                        mSite_Array = builder.getNewArray();
                        mSpinner_Edit.putString(title, array2StringBuilder(mSite_Array).toString());
                        break;
                    case "不良位置":
                        mPosition_Array = builder.getNewArray();
                        mSpinner_Edit.putString(title, array2StringBuilder(mPosition_Array).toString());
                        break;
                }
                mSpinner_Edit.commit();
            }
        });
        builder.create().show();
    }

    public void setSelectFirstMenuName(String name) {
        Log.i("cc", "setSelectFirstMenuName = " + mFirstMenuName);
        mFirstMenuName = name;
    }

    private void buildSpinner2EditLayout(final String title, String[] array) {
        final SpinnerEditDialog.Builder builder = new SpinnerEditDialog.Builder(mActivity, array, this);
        builder.setTitle(title);
        builder.setButtonConfirm(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSecondaryMenu(builder.getNewArray());
            }
        });
        builder.create().show();
    }

    public void saveSecondaryMenu(String[] array) {
        switch (mFirstMenuName) {
            case "CP00偏光版类不良":
                mPhenom_Array2_CP = array;
                mSpinner_Edit.putString(mFirstMenuName, array2StringBuilder(mPhenom_Array2_CP).toString());
                break;
            case "CM00品味性不良":
                mPhenom_Array2_CM = array;
                mSpinner_Edit.putString(mFirstMenuName, array2StringBuilder(mPhenom_Array2_CM).toString());
                break;
            case "CF00功能性不良":
                mPhenom_Array2_CF = array;
                mSpinner_Edit.putString(mFirstMenuName, array2StringBuilder(mPhenom_Array2_CF).toString());
                break;
            case "CA00外观类不良":
                mPhenom_Array2_CA = array;
                mSpinner_Edit.putString(mFirstMenuName, array2StringBuilder(mPhenom_Array2_CA).toString());
                break;
            case "CD00Dot类不良":
                mPhenom_Array2_CD = array;
                mSpinner_Edit.putString(mFirstMenuName, array2StringBuilder(mPhenom_Array2_CD).toString());
                break;
            case "CN00Other":
                mPhenom_Array2_CN = array;
                mSpinner_Edit.putString(mFirstMenuName, array2StringBuilder(mPhenom_Array2_CN).toString());
                break;
        }
        mSpinner_Edit.commit();
    }


    private class EditextOnChangeListenerData implements TextWatcher {
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

    private void startScanCode() {
        //已导入Google zxing二维码功能及第三方已实现的二维码扫描功能及界面，通过以下代码即可便捷启动扫码
        IntentIntegrator integrator = new IntentIntegrator(mActivity);
        integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    if (requestCode == PictureConfig.CHOOSE_REQUEST) {// 图片选择结果回调

                        List<String> newImageUris = new ArrayList<String>();
                        if (selectList != null) {
                            for (int i = 0; i < selectList.size(); i++) {
                                newImageUris.add(selectList.get(i).getCompressPath());
                            }
                            mItem.setHome_BadPic(newImageUris);
                        }
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

    public void updateDeleteImages(List<LocalMedia> list) {

        List<String> newImageUris = new ArrayList<String>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                newImageUris.add(list.get(i).getCompressPath());
            }
            mItem.setHome_BadPic(newImageUris);
        }
    }
}
