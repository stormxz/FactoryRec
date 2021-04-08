package com.example.factoryrec.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.factoryrec.R;
import com.example.factoryrec.app.MainActivity;

public class SpinnerEditDialog extends Dialog {

    private static MainActivity mActivity;
    private static String[] mArray;

    private static String mDeleteString;

    public SpinnerEditDialog(@NonNull Context context) {
        super(context);
    }

    public SpinnerEditDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected SpinnerEditDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    /* Builder */
    public static class Builder implements View.OnClickListener{
        private EditText editInfo;
        private Button addButton, deleteButton;

        private View mLayout;
        private View.OnClickListener mButtonCancelClickListener;
        private View.OnClickListener mButtonConfirmClickListener;

        private SpinnerEditDialog mDialog;

        public Builder(MainActivity activity, String[] array) {
            mActivity = activity;
            mArray = array;
            mDialog = new SpinnerEditDialog(activity);
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // 加载布局文件
            mLayout = inflater.inflate(R.layout.spinner_edit, null, false);
            // 添加布局文件到 Dialog
            Resources res = activity.getResources();
            mDialog.addContentView(mLayout, new ViewGroup.LayoutParams(res.getDimensionPixelSize(R.dimen.spinner_edit_layout_width),
                    res.getDimensionPixelSize(R.dimen.spinner_edit_layout_height)));

            editInfo = (EditText) mLayout.findViewById(R.id.edit_info);
            addButton = (Button) mLayout.findViewById(R.id.add_button);
            deleteButton = (Button) mLayout.findViewById(R.id.delete_button);

            deleteButton.setOnClickListener(this);

        }

        /**
         * 设置 Dialog 标题
         */
        public Builder setTitle(String title) {
            return this;
        }

        /**
         * 设置 Warning
         */
        public Builder setWarning(String waring) {
            return this;
        }

        /**
         * 设置 Info
         */
        public Builder setInfo(String message) {
            return this;
        }

        public SpinnerEditDialog create() {

            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
            return mDialog;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.add_button:

                    break;
                case R.id.delete_button:
                    if (mArray == null || mArray.length == 0) {
                        Toast.makeText(mActivity, "列表中无有效数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setSingleChoiceItems(mArray, -1,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setDeleteString(mArray[which]);
                                    Log.i("cc", "delete String = " + mArray[which]);
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                    builder.setTitle("请选择需要删除的选项");
                    builder.create().show();
                    break;
            }
        }

        private void setDeleteString(String string) {
            mDeleteString = string;
        }
    }

}
