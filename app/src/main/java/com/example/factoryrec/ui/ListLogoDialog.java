package com.example.factoryrec.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.factoryrec.R;

public class ListLogoDialog extends Dialog {

    private final Context mContext;
    private ListView mLogoListView;
    private TextView mCancelText;
    private TextView mConfirmText;
    private TextView mDeleteText;
    private TextView mAddText;

    public ListLogoDialog(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    private void initView() {
        View contentView = View.inflate(mContext, R.layout.list_logo_image, null);
        mLogoListView = (ListView) contentView.findViewById(R.id.logo_image_view);
        mLogoListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mCancelText = contentView.findViewById(R.id.logo_list_cancel_text);
        mConfirmText = contentView.findViewById(R.id.logo_list_confirm_text);
        mDeleteText = contentView.findViewById(R.id.logo_list_delete_text);
        mAddText = contentView.findViewById(R.id.logo_list_add_text);
        setContentView(contentView);
    }

    public void setCancelText(TextView cancelText) {
        this.mCancelText = cancelText;
    }

    public TextView getCancelText() {
        return mCancelText;
    }

    public void setConfirmText(TextView confirmText) {
        this.mConfirmText = confirmText;
    }

    public TextView getConfirmText() {
        return mConfirmText;
    }

    public void setDeleteText(TextView deleteText) {
        this.mDeleteText = deleteText;
    }

    public TextView getDeleteText() {
        return mDeleteText;
    }

    public void setAddText(TextView addText) {
        this.mAddText = addText;
    }

    public TextView getAddText() {
        return mAddText;
    }

    public void setLogoListView(ListView listView) {
        this.mLogoListView = listView;
    }

    public ListView getLogoListView() {
        return mLogoListView;
    }

}
