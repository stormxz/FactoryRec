package com.example.factoryrec.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.factoryrec.R;
import com.example.factoryrec.adapter.LogoViewListAdapter;
import com.example.factoryrec.excel.ExcelCreator;
import com.example.factoryrec.selector.MultiImageSelectorActivity;
import com.example.factoryrec.ui.ListLogoDialog;
import com.example.factoryrec.ui.SpinnerEditDialog;
import com.example.factoryrec.util.PdfCreator;
import com.example.factoryrec.util.ProductItem;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Fragment_Result extends MainFragment {

    private EditText mResultEditText = null;

    private CheckBox mExcel = null;
    private CheckBox mPDF = null;

    private CheckBox mShowLogo = null;
    private ImageView mSelect_Logo = null;
    private ImageView mEdit_Logo = null;

    private SharedPreferences mSpinner_Value;
    private SharedPreferences.Editor mSpinner_Edit;

    //标题
    private CheckBox mShowTitle;
    private LinearLayout mTitle_Layout;
    private TextView mTitle_Spinner;
    private ImageView mTitle_Edit_Image;
    private String[] mTitle_Array;
    private int mTitle_which = -1;

    //页脚
    private CheckBox mShowFooter;
    private LinearLayout mFooter_Layout;
    private TextView mFooter_Spinner;
    private ImageView mFooter_Edit_Image;
    private String[] mFooter_Array;
    private int mFooter_which = -1;

    //水印
    private CheckBox mShowWatermark;

    private Button btnSubmit = null;

    // 记录当天提交个数
    private SharedPreferences mSubmitCount_Value;
    private SharedPreferences.Editor mSubmitCount_Edit;

    //logo image ListView
    private List<String> mLogoUriList;
    private LogoViewListAdapter mLogoAdapter;
    private int mSelectPosition = -1;

    private static final String REGULAR_EXPRESSION = "#";
    private SharedPreferences mLogo_Uri;
    private SharedPreferences.Editor mLogo_Uri_Edit;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_result, container, false);
        mExcel = view.findViewById(R.id.addexcel);
        mPDF = view.findViewById(R.id.addpdf);
        mShowLogo = view.findViewById(R.id.showlogo);
        mSelect_Logo = view.findViewById(R.id.image_select_logo);
//        mEdit_Logo = view.findViewById(R.id.edit_image_logo);
        mShowTitle = view.findViewById(R.id.show_title);
        mTitle_Layout = view.findViewById(R.id.title_edit_layout);
        mTitle_Spinner = view.findViewById(R.id.title_spinner);
        mTitle_Edit_Image = view.findViewById(R.id.edit_image_title);

        mShowFooter = view.findViewById(R.id.show_footer);
        mFooter_Layout = view.findViewById(R.id.footer_edit_layout);
        mFooter_Spinner = view.findViewById(R.id.footer_spinner);
        mFooter_Edit_Image = view.findViewById(R.id.edit_image_footer);

        mShowWatermark = view.findViewById(R.id.show_watermark);

        mResultEditText = view.findViewById(R.id.result_et);

        mSubmitCount_Value = mActivity.getSharedPreferences("submit_count", Context.MODE_PRIVATE);
        mSubmitCount_Edit = mSubmitCount_Value.edit();

        mResultEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mItem.setConclusion(s + "");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSubmit = (Button) view.findViewById(R.id.btnSubmit).findViewById(R.id.btn);
        btnSubmit.setEnabled(true);

        // 添加PDF 监听，用来控制logo 选项是否显示
        mPDF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mShowLogo.setVisibility(View.VISIBLE);
                    mShowTitle.setVisibility(View.VISIBLE);
                    mShowFooter.setVisibility(View.VISIBLE);
                    mShowWatermark.setVisibility(View.VISIBLE);
                } else {
                    mShowLogo.setChecked(false);
                    mShowLogo.setVisibility(View.GONE);
                    mShowTitle.setVisibility(View.GONE);
                    mShowFooter.setVisibility(View.GONE);
                    mShowWatermark.setVisibility(View.GONE);
                }
            }
        });

        mShowLogo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSelect_Logo.setVisibility(View.VISIBLE);
                } else {
                    mSelect_Logo.setVisibility(View.GONE);
                }
            }
        });

        //选择LOGO按钮
        mSelect_Logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initLogoListDialog(getContext());
            }
        });

        mShowTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTitle_Layout.setVisibility(View.VISIBLE);
                } else {
                    mTitle_Layout.setVisibility(View.GONE);
                }
            }
        });

        //Title选择
        mTitle_Spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder title_Builder = new AlertDialog.Builder(new ContextThemeWrapper(mActivity, R.style.AlertDialogCustom));
                int title_select = mTitle_which;
                title_Builder.setSingleChoiceItems(mTitle_Array, title_select,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mTitle_which = which;
                                mTitle_Spinner.setText(mTitle_Array[which]);
                                mItem.setTitle(mTitle_Array[which]);
                                mTitle_Spinner.setTextColor(mActivity.getColor(R.color.primary_light));
                                dialog.dismiss();
                            }
                        });
                title_Builder.setTitle("标题");
                title_Builder.create().show();
            }
        });

        //Title编辑
        mTitle_Edit_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildSpinnerEditLayout("标题", mTitle_Array);
            }
        });

        mShowFooter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFooter_Layout.setVisibility(View.VISIBLE);
                } else {
                    mFooter_Layout.setVisibility(View.GONE);
                }
            }
        });

        //Footer选择
        mFooter_Spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder footer_Builder = new AlertDialog.Builder(mActivity);
                int footer_select = mFooter_which;
                footer_Builder.setSingleChoiceItems(mFooter_Array, footer_select,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mFooter_which = which;
                                mFooter_Spinner.setText(mFooter_Array[which]);
                                mItem.setFooter(mFooter_Array[which]);
                                mFooter_Spinner.setTextColor(mActivity.getColor(R.color.primary_light));
                                dialog.dismiss();
                            }
                        });
                footer_Builder.setTitle("页脚/水印");
                footer_Builder.create().show();
            }
        });

        //Footer编辑
        mFooter_Edit_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildSpinnerEditLayout("页脚", mFooter_Array);
            }
        });

        // 提交按钮
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //各种条件判断后，进行创建excel pdf 并且提交
                Log.e("stormxz", " display text = " + mItem.getDisplayText() + "  om text = " + mItem.getOMText() + "  signal text = " + mItem.getSignalText() + "  result text = " + mItem.getConclusion());
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setMessage("是否确认提交？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog1, int which) {
                                Toast.makeText(getContext(), "正在保存...按钮变蓝之前不要更改内容", Toast.LENGTH_SHORT).show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        btnSubmit.setEnabled(false);
                                        if (mPDF.isChecked()) {
                                            PdfCreator pc = new PdfCreator(mActivity, Fragment_Result.this);
                                            pc.generatePdf(mShowLogo.isChecked(), mShowTitle.isChecked(), mShowFooter.isChecked(), mShowWatermark.isChecked());
                                        }
                                        if (mExcel.isChecked()) {
                                            ExcelCreator excelCreator = new ExcelCreator(mActivity, Fragment_Result.this, mSubmitCount_Value, mSubmitCount_Edit);
                                            excelCreator.generateExcel();
                                        }
                                    }
                                }).start();
                            }
                        });
                builder.create().show();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSharedPreferenceData();
        initLogoUriList();
//        initLogoListDialog(getContext());
        mLogoAdapter = new LogoViewListAdapter(getContext(), mLogoUriList);
    }

    private void initLogoUriList() {
        mLogo_Uri = mActivity.getSharedPreferences("logo_uri", Context.MODE_PRIVATE);
        mLogo_Uri_Edit = mLogo_Uri.edit();
        mLogoUriList = string2List(mLogo_Uri.getString("uri", null));
    }

    private StringBuilder list2String(List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(list.get(i));
            if (i != list.size() - 1) {
                stringBuilder.append(REGULAR_EXPRESSION);
            }
        }
        return stringBuilder;
    }

    private List<String> string2List(String string) {
        List<String> list = new ArrayList<String>();
        String[] strArr = string != null ? string.split(REGULAR_EXPRESSION) : null;
        if (strArr != null && strArr.length != 0) {
            list.addAll(Arrays.asList(strArr));
        }
        return list;
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

    private void initLogoListDialog(Context context) {
        final ListLogoDialog dialog = new ListLogoDialog(context);
        dialog.setCancelable(true);
        dialog.setTitle("Logo选择");
        //取消
        dialog.getCancelText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mSelectPosition = -1;
            }
        });
        //确定
        dialog.getConfirmText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mSelectPosition != -1) {
                    mSelect_Logo.setImageBitmap(BitmapFactory.decodeFile(mLogoUriList.get(mSelectPosition)));
                }
            }
        });
        //删除
        dialog.getDeleteText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectPosition == -1) {
                    Toast.makeText(mActivity, "请选择删除项", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setMessage("是否确定删除该项？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog1, int which) {
                                    mLogoUriList.remove(mSelectPosition);
//                                    mLogoAdapter.notifyDataSetChanged();
                                    mLogoAdapter.notifyDataSetInvalidated();
                                    dialog.getLogoListView().setSelection(-1);
                                    mLogo_Uri_Edit.putString("uri", list2String(mLogoUriList).toString());
                                    mLogo_Uri_Edit.commit();
                                }
                            });
                    builder.create().show();
                }
            }
        });
        //添加
        dialog.getAddText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < 23) {
                    showPop(true);
                } else {
                    getPremission();
                }
            }
        });
        dialog.getLogoListView().setAdapter(mLogoAdapter);
        dialog.getLogoListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("cc", "onItemClick : position = " + position);
                mSelectPosition = position;
                mItem.setLogo_Pic(mLogoUriList.get(mSelectPosition));
            }
        });
        dialog.show();
    }

    private void initSharedPreferenceData() {
        mSpinner_Value = mActivity.getSharedPreferences("spinner_value_result", Context.MODE_PRIVATE);
        mSpinner_Edit = mSpinner_Value.edit();

        mTitle_Array = string2Array(mSpinner_Value.getString("标题", null));
        mFooter_Array = string2Array(mSpinner_Value.getString("页脚", null));
    }

    private void buildSpinnerEditLayout(final String title, String[] array) {
        final SpinnerEditDialog.Builder builder = new SpinnerEditDialog.Builder(mActivity, array);
        builder.setTitle(title);
        builder.setButtonConfirm(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (title) {
                    case "标题":
                        mTitle_Array = builder.getNewArray();
                        mSpinner_Edit.putString(title, array2StringBuilder(mTitle_Array).toString());
                        break;
                    case "页脚":
                        mFooter_Array = builder.getNewArray();
                        mSpinner_Edit.putString(title, array2StringBuilder(mFooter_Array).toString());
                        break;
                }
                mSpinner_Edit.commit();
            }
        });
        builder.create().show();
    }

    public ProductItem getItem() {
        return mItem;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:// 图片
                    if (data != null) {
                        List<LocalMedia> images;
                        images = PictureSelector.obtainMultipleResult(data);
                        List<String> newImageUris = new ArrayList<String>();
                        if (images != null) {
                            for (int i = 0; i < images.size(); i++) {
                                newImageUris.add(images.get(i).getCompressPath());
                            }
                        }
                        Log.e("cc", "onActivityResult imageUris: " + newImageUris);
                        if (newImageUris.size() != 0) {
                            mLogoUriList.addAll(newImageUris);
                        }
                        mLogo_Uri_Edit.putString("uri", list2String(mLogoUriList).toString());
                        mLogo_Uri_Edit.commit();
                    }
                    mLogoAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

//    public void setEnable() {
//        mActivity.runOnUiThread(new Runnable() {
//          @Override
//            public void run() {
//              btnSubmit.setEnabled(true);
//          }
//        });
//    }
}
