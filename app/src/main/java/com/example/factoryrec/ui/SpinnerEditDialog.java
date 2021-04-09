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
import android.widget.TextView;
import android.widget.Toast;

import com.example.factoryrec.R;
import com.example.factoryrec.app.Fragment_Home;
import com.example.factoryrec.app.MainActivity;

import java.util.ArrayList;

public class SpinnerEditDialog extends Dialog {

    private static MainActivity mActivity;
    private static String[] mArray;

    private static String mDeleteString;

    private static boolean mSecondaryMenu;
    private static String[] mArray2;
    private static Fragment_Home mHome;
    private static int mSelect_which = -1;

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
    public static class Builder implements View.OnClickListener {
        private EditText editInfo;
        private Button addButton, deleteButton;
        private TextView confirmText, cancelText;

        private View mLayout;
        private View.OnClickListener mButtonCancelClickListener;
        private View.OnClickListener mButtonConfirmClickListener;

        private SpinnerEditDialog mDialog;

        public Builder(MainActivity activity, String[] array) {
            mActivity = activity;
            mArray = array;
            mSecondaryMenu = false;
            initView();
        }

        public Builder(MainActivity activity, String[] array, Fragment_Home home) {
            mActivity = activity;
            mArray = array;
            mHome = home;
            mSecondaryMenu = true;
            initView();
        }

        public void initView() {
            mDialog = new SpinnerEditDialog(mActivity);
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // 加载布局文件
            mLayout = inflater.inflate(R.layout.spinner_edit, null, false);
            // 添加布局文件到 Dialog
            Resources res = mActivity.getResources();
            mDialog.addContentView(mLayout, new ViewGroup.LayoutParams(res.getDimensionPixelSize(R.dimen.spinner_edit_layout_width),
                    res.getDimensionPixelSize(R.dimen.spinner_edit_layout_height)));

            editInfo = (EditText) mLayout.findViewById(R.id.edit_info);
            addButton = (Button) mLayout.findViewById(R.id.add_button);
            deleteButton = (Button) mLayout.findViewById(R.id.delete_button);
            confirmText = (TextView) mLayout.findViewById(R.id.confirm_text);
            cancelText = (TextView) mLayout.findViewById(R.id.cancel_text);

            addButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
        }

        /**
         * 设置确认按钮文字和监听
         */
        public Builder setButtonConfirm(View.OnClickListener listener) {
            mButtonConfirmClickListener = listener;
            return this;
        }

        /**
         * 设置 Dialog 标题
         */
        public Builder setTitle(String title) {
            return this;
        }

        public String[] getNewArray() {
            if (!mSecondaryMenu) {
                return mArray;
            } else {
                return mArray2;
            }
        }

        public SpinnerEditDialog create() {
            confirmText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                    mButtonConfirmClickListener.onClick(view);
                }
            });

            cancelText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                }
            });

            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
            return mDialog;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.add_button:
                    if (editInfo == null || "".equals(editInfo.getText().toString())) {
                        Toast.makeText(mActivity, "请输入有效数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!mSecondaryMenu) {
                        if (mArray != null) {
                            synchronized (mArray) {
                                mArray = addElement(editInfo.getText().toString(), mArray);
                            }
                        } else {
                            mArray = addElement(editInfo.getText().toString(), mArray);
                        }
                        Toast.makeText(mActivity, "数据添加成功", Toast.LENGTH_SHORT).show();
                        confirmText.setVisibility(View.VISIBLE);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setSingleChoiceItems(mArray, -1,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.i("cc", "setSingleChoiceItems ; which = " + which);
                                        mSelect_which = which;
                                        mHome.setSelectFirstMenuName(mArray[which]);
                                    }
                                })
                                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.i("cc", "setPositiveButton : which = " + which);
                                        mArray2 = mHome.phenomIndex(mSelect_which);
                                        if (mArray2 != null) {
                                            synchronized (mArray2) {
                                                mArray2 = addElement(editInfo.getText().toString(), mArray2);
                                            }
                                        } else {
                                            mArray2 = addElement(editInfo.getText().toString(), mArray2);
                                        }
                                        Toast.makeText(mActivity, "数据添加成功", Toast.LENGTH_SHORT).show();
                                        mHome.saveSecondaryMenu(mArray2);
                                        confirmText.setText("确定");
                                        confirmText.setVisibility(View.VISIBLE);
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                        builder.setTitle("请选择需要加入的一级菜单");
                        builder.create().show();
                    }
                    break;
                case R.id.delete_button:
                    if (mArray == null || mArray.length == 0) {
                        Toast.makeText(mActivity, "列表中无有效数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!mSecondaryMenu) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setSingleChoiceItems(mArray, -1,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mDeleteString = mArray[which];
                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (mArray != null) {
                                            synchronized (mArray) {
                                                mArray = deleteElement(mDeleteString, mArray);
                                            }
                                        } else {
                                            mArray = deleteElement(mDeleteString, mArray);
                                        }
                                        Toast.makeText(mActivity, "已删除", Toast.LENGTH_SHORT).show();
                                        confirmText.setVisibility(View.VISIBLE);
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                });
                        builder.setTitle("请选择需要删除的选项");
                        builder.create().show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);               //一级菜单选择框
                        builder.setSingleChoiceItems(mArray, -1,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mArray2 = mHome.phenomIndex(which);
                                        mHome.setSelectFirstMenuName(mArray[which]);
                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(mActivity);      //二级菜单选择框
                                        dialog.dismiss();
                                        builder2.setSingleChoiceItems(mArray2, -1,
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        mDeleteString = mArray2[which];
                                                        Log.i("cc", "mDeleteString = " + mDeleteString);
                                                    }
                                                })
                                                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (mArray2 != null) {
                                                            synchronized (mArray2) {
                                                                mArray2 = deleteElement(mDeleteString, mArray2);
                                                            }
                                                        } else {
                                                            mArray2 = deleteElement(mDeleteString, mArray2);
                                                        }
                                                        Toast.makeText(mActivity, "已删除", Toast.LENGTH_SHORT).show();
                                                        mHome.saveSecondaryMenu(mArray2);
                                                        confirmText.setText("确定");
                                                        confirmText.setVisibility(View.VISIBLE);
                                                    }
                                                })
                                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });
                                        builder2.setTitle("请选择需要删除的选项");
                                        builder2.create().show();
                                    }
                                });
                        builder.setTitle("请选择一级菜单");
                        builder.create().show();
                    }
                    break;
            }
        }

    }

    private static String[] deleteElement(String stringToDelete, String[] array) {
        String[] result = new String[array.length];
        int index = 0;
        ArrayList rm = new ArrayList();
        for (int i = 0; i < array.length; i++) {
            rm.add(array[i]);
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(stringToDelete)) {
                index = i;
            }
        }
        rm.remove(index);
        result = (String[]) rm.toArray(new String[rm.size()]);
        return result;
    }

    private static String[] addElement(String stringToAdd, String[] array) {
        String[] result;
        ArrayList rm = new ArrayList();
        if (array != null && array.length != 0) {
            result = new String[array.length];
            int index = 0;
            for (int i = 0; i < array.length; i++) {
                rm.add(array[i]);
            }
        }
        rm.add(stringToAdd);
        result = (String[]) rm.toArray(new String[rm.size()]);
        return result;
    }

}
