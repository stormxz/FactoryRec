package com.example.factoryrec.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.factoryrec.R;
import com.example.factoryrec.excel.ExcelCreator;
import com.example.factoryrec.util.PdfCreator;
import com.example.factoryrec.util.ProductItem;


public class Fragment_Result extends MainFragment {

    private CheckBox mExcel = null;

    private CheckBox mPDF = null;

    private CheckBox mShowLogo = null;

    private EditText mResultEditText = null;

    private Button btnSubmit = null;

    // 记录当天提交个数
    private SharedPreferences mSubmitCount_Value;
    private SharedPreferences.Editor mSubmitCount_Edit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_result, container, false);
        mExcel = view.findViewById(R.id.addexcel);
        mPDF = view.findViewById(R.id.addpdf);
        mShowLogo = view.findViewById(R.id.showlogo);
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
                } else {
                    mShowLogo.setChecked(false);
                    mShowLogo.setVisibility(View.GONE);
                }
            }
        });

        // 提交按钮
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //各种条件判断后，进行创建excel pdf 并且提交
                Log.e("stormxz", " display text = " + mItem.getDisplayText() + "  om text = " + mItem.getOMText() + "  signal text = " + mItem.getSignalText() + "  result text = " + mItem.getConclusion());
                Toast.makeText(getContext(), "正在保存...", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mPDF.isChecked()) {
                            PdfCreator pc = new PdfCreator(mActivity, Fragment_Result.this);
                            pc.generatePdf();
                        }
                        if (mExcel.isChecked()) {
                            ExcelCreator excelCreator = new ExcelCreator(mActivity, Fragment_Result.this, mSubmitCount_Value, mSubmitCount_Edit);
                            excelCreator.generateExcel();
                        }
                    }
                }).start();
            }
        });

        return view;
    }

    public ProductItem getItem() {
        return mItem;
    }
}
