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

    //δΈζθε
    private TextView mCustom_Spinner;
    private TextView mMachine_Spinner;
    private TextView mPhenom_Spinner;
    private TextView mPhenom2_Spinner;
    private TextView mSite_Spinner;
    private TextView mPosition_Spinner;

    //δΈζθεηΌθΎ
    private ImageView mCustom_Edit_Image;
    private ImageView mMachine_Edit_Image;
    private ImageView mPhenom_Edit_Image;
    private ImageView mPhenom2_Edit_Image;
    private ImageView mSite_Edit_Image;
    private ImageView mPosition_Edit_Image;

    //δΊη»΄η 
    private EditText mSNText;
    private ImageView mScanImage;

    //εηζΆι΄
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
        //δΈζθε
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

        //δΈζθεηΌθΎ
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

        //δΊη»΄η 
        mSNText = view.findViewById(R.id.sn_editText);
        mScanImage = view.findViewById(R.id.scan_image);
        mScanImage.setOnClickListener(this);

        //εηζΆι΄
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
            //δΈθ―η°θ±‘
            mPhenom_Array = res.getStringArray(R.array.bad_phenomenon);
            mPhenom_Array2_CP = res.getStringArray(R.array.CP00εεηη±»δΈθ―);
            mPhenom_Array2_CM = res.getStringArray(R.array.CM00εε³ζ§δΈθ―);
            mPhenom_Array2_CF = res.getStringArray(R.array.CF00εθ½ζ§δΈθ―);
            mPhenom_Array2_CD = res.getStringArray(R.array.CD00Dotη±»δΈθ―);
            mPhenom_Array2_CA = res.getStringArray(R.array.CA00ε€θ§η±»δΈθ―);
            mPhenom_Array2_CN = res.getStringArray(R.array.CN00Other);
            mSpinner_Edit.putString("bad_phenomenon", array2StringBuilder(mPhenom_Array).toString());
            mSpinner_Edit.putString("CP00εεηη±»δΈθ―", array2StringBuilder(mPhenom_Array2_CP).toString());
            mSpinner_Edit.putString("CM00εε³ζ§δΈθ―", array2StringBuilder(mPhenom_Array2_CM).toString());
            mSpinner_Edit.putString("CF00εθ½ζ§δΈθ―", array2StringBuilder(mPhenom_Array2_CF).toString());
            mSpinner_Edit.putString("CD00Dotη±»δΈθ―", array2StringBuilder(mPhenom_Array2_CD).toString());
            mSpinner_Edit.putString("CA00ε€θ§η±»δΈθ―", array2StringBuilder(mPhenom_Array2_CA).toString());
            mSpinner_Edit.putString("CN00Other", array2StringBuilder(mPhenom_Array2_CN).toString());
            //δΈθ―δ½η½?
            mPosition_Array = res.getStringArray(R.array.δΈθ―δ½η½?);
            mSpinner_Edit.putString("δΈθ―δ½η½?", array2StringBuilder(mPosition_Array).toString());
            mSpinner_Edit.commit();
        } else {
            mCustoms_Array = string2Array(mSpinner_Value.getString("ε?’ζ·", null));
            mMachine_Array = string2Array(mSpinner_Value.getString("ζΊη§", null));
            mSite_Array = string2Array(mSpinner_Value.getString("εηη«ηΉ", null));
            mPhenom_Array = string2Array(mSpinner_Value.getString("bad_phenomenon", null));
            mPhenom_Array2_CP = string2Array(mSpinner_Value.getString("CP00εεηη±»δΈθ―", null));
            mPhenom_Array2_CM = string2Array(mSpinner_Value.getString("CM00εε³ζ§δΈθ―", null));
            mPhenom_Array2_CF = string2Array(mSpinner_Value.getString("CF00εθ½ζ§δΈθ―", null));
            mPhenom_Array2_CD = string2Array(mSpinner_Value.getString("CD00Dotη±»δΈθ―", null));
            mPhenom_Array2_CA = string2Array(mSpinner_Value.getString("CA00ε€θ§η±»δΈθ―", null));
            mPhenom_Array2_CN = string2Array(mSpinner_Value.getString("CN00Other", null));
            mPosition_Array = string2Array(mSpinner_Value.getString("δΈθ―δ½η½?", null));
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
        // εε»ΊδΈδΈͺζ₯εεΌη¨dοΌιθΏιζζΉζ³getInstance() δ»ζε?ζΆεΊ Locale.CHINA θ·εΎδΈδΈͺζ₯ζε?δΎ
        Date myDate = new Date();
        // εε»ΊδΈδΈͺDateε?δΎ
        d.setTime(myDate);
        // θ?Ύη½?ζ₯εηζΆι΄οΌζδΈδΈͺζ°ε»ΊDateε?δΎmyDateδΌ ε₯
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
            case R.id.custom_spinner:   //ε?’ζ·δΈζθε
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
                custom_Builder.setTitle("ε?’ζ·");
                custom_Builder.create().show();
                break;
            case R.id.machine_spinner:   //ζΊη§δΈζθε
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
                machine_Builder.setTitle("ζΊη§");
                machine_Builder.create().show();
                break;
            case R.id.phenom_spinner:   //δΈθ―η°θ±‘δΈηΊ§δΈζθε
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
                phenom_Builder.setTitle("δΈθ―ε€§η±»");
                phenom_Builder.create().show();
                break;
            case R.id.phenom_spinner2:   //δΈθ―η°θ±‘δΊηΊ§δΈζθε
                if (mPhenom_which == -1) {
                    Toast.makeText(mActivity, "θ―·ειζ©δΈηΊ§θε", Toast.LENGTH_SHORT).show();
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
                phenom2_Builder.setTitle("δΈθ―η°θ±‘");
                phenom2_Builder.create().show();
                break;
            case R.id.site_spinner:   //εηη«ηΉδΈζθε
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
                site_Builder.setTitle("εηη«ηΉ");
                site_Builder.create().show();
                break;
            case R.id.position_spinner:   //δΈθ―δ½η½?δΈζθε
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
                position_Builder.setTitle("δΈθ―δ½η½?");
                position_Builder.create().show();
                break;
            case R.id.scan_image:    //δΊη»΄η ζ«ζζι?
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA}, 0);
                } else {
                    startScanCode();
                }

                break;
            case R.id.time1_text:    //ιζ©ζ₯ζ
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
            case R.id.time2_text:    //ιζ©ζΆι΄
                new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHour = hourOfDay;
                        mMinute = minute;
                        setTime();
                    }
                }, mHour, mMinute, true).show();
                break;
            case R.id.edit_image_custom:      //ε?’ζ·δΈζθεηΌθΎ
                buildSpinnerEditLayout("ε?’ζ·", mCustoms_Array);
                break;
            case R.id.edit_image_machine:     //ζΊη§δΈζθεηΌθΎ
                buildSpinnerEditLayout("ζΊη§", mMachine_Array);
                break;
            case R.id.edit_image_phenom:      //δΈθ―η°θ±‘δΈζθεηΌθΎ
                buildSpinnerEditLayout("δΈθ―η°θ±‘δΈ", mPhenom_Array);
                break;
            case R.id.edit_image_phenom_2:    //δΈθ―η°θ±‘δΊδΈζθεηΌθΎ
                buildSpinner2EditLayout("δΈθ―η°θ±‘δΊ", mPhenom_Array);
                break;
            case R.id.edit_image_site:        //εηη«ηΉδΈζθεηΌθΎ
                buildSpinnerEditLayout("εηη«ηΉ", mSite_Array);
                break;
            case R.id.edit_image_position:    //δΈθ―δ½η½?δΈζθεηΌθΎ
                buildSpinnerEditLayout("δΈθ―δ½η½?", mPosition_Array);
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
                    case "ε?’ζ·":
                        mCustoms_Array = builder.getNewArray();
                        mSpinner_Edit.putString(title, array2StringBuilder(mCustoms_Array).toString());
                        break;
                    case "ζΊη§":
                        mMachine_Array = builder.getNewArray();
                        mSpinner_Edit.putString(title, array2StringBuilder(mMachine_Array).toString());
                        break;
                    case "δΈθ―η°θ±‘δΈ":
                        mPhenom_Array = builder.getNewArray();
                        mSpinner_Edit.putString("bad_phenomenon", array2StringBuilder(mPhenom_Array).toString());
                        break;
                    case "CP00εεηη±»δΈθ―":
                        mPhenom_Array2_CP = builder.getNewArray();
                        mSpinner_Edit.putString(title, array2StringBuilder(mPhenom_Array2_CP).toString());
                        break;
                    case "CM00εε³ζ§δΈθ―":
                        mPhenom_Array2_CM = builder.getNewArray();
                        mSpinner_Edit.putString(title, array2StringBuilder(mPhenom_Array2_CM).toString());
                        break;
                    case "CF00εθ½ζ§δΈθ―":
                        mPhenom_Array2_CF = builder.getNewArray();
                        mSpinner_Edit.putString(title, array2StringBuilder(mPhenom_Array2_CF).toString());
                        break;
                    case "CA00ε€θ§η±»δΈθ―":
                        mPhenom_Array2_CA = builder.getNewArray();
                        mSpinner_Edit.putString(title, array2StringBuilder(mPhenom_Array2_CA).toString());
                        break;
                    case "CD00Dotη±»δΈθ―":
                        mPhenom_Array2_CD = builder.getNewArray();
                        mSpinner_Edit.putString(title, array2StringBuilder(mPhenom_Array2_CD).toString());
                        break;
                    case "CN00Other":
                        mPhenom_Array2_CN = builder.getNewArray();
                        mSpinner_Edit.putString(title, array2StringBuilder(mPhenom_Array2_CN).toString());
                        break;
                    case "εηη«ηΉ":
                        mSite_Array = builder.getNewArray();
                        mSpinner_Edit.putString(title, array2StringBuilder(mSite_Array).toString());
                        break;
                    case "δΈθ―δ½η½?":
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
            case "CP00εεηη±»δΈθ―":
                mPhenom_Array2_CP = array;
                mSpinner_Edit.putString(mFirstMenuName, array2StringBuilder(mPhenom_Array2_CP).toString());
                break;
            case "CM00εε³ζ§δΈθ―":
                mPhenom_Array2_CM = array;
                mSpinner_Edit.putString(mFirstMenuName, array2StringBuilder(mPhenom_Array2_CM).toString());
                break;
            case "CF00εθ½ζ§δΈθ―":
                mPhenom_Array2_CF = array;
                mSpinner_Edit.putString(mFirstMenuName, array2StringBuilder(mPhenom_Array2_CF).toString());
                break;
            case "CA00ε€θ§η±»δΈθ―":
                mPhenom_Array2_CA = array;
                mSpinner_Edit.putString(mFirstMenuName, array2StringBuilder(mPhenom_Array2_CA).toString());
                break;
            case "CD00Dotη±»δΈθ―":
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
        //ε·²ε―Όε₯Google zxingδΊη»΄η εθ½εη¬¬δΈζΉε·²ε?η°ηδΊη»΄η ζ«ζεθ½εηι’οΌιθΏδ»₯δΈδ»£η ε³ε―δΎΏζ·ε―ε¨ζ«η 
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
                    if (requestCode == PictureConfig.CHOOSE_REQUEST) {// εΎηιζ©η»ζεθ°

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
