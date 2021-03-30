package com.example.factoryrec.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.example.factoryrec.util.ProductItem;

public class MainFragment extends Fragment {

    protected MainActivity mActivity;
    protected ProductItem mItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mItem = mActivity.getItem();
    }
}
