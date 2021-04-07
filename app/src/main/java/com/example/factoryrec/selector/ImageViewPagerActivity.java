/** ************************************************************************/
/*                                                                         */
/* Copyright (c) 2015 YULONG Company */
/* 宇龙计算机通信科技（深圳）有限公司 版权所有 2015 */
/*                                                                         */
/* PROPRIETARY RIGHTS of YULONG Company are involved in the */
/* subject matter of this material. All manufacturing, reproduction, use, */
/* and sales rights pertaining to this subject matter are governed by the */
/* license agreement. The recipient of this software implicitly accepts */
/* the terms of the license. */
/* 本软件文档资料是宇龙公司的资产,任何人士阅读和使用本资料必须获得 */
/* 相应的书面授权,承担保密责任和接受相应的法律约束. */
/*                                                                         */
/** ************************************************************************/
package com.example.factoryrec.selector;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.example.factoryrec.R;
import com.example.factoryrec.ui.ImageSource;
import com.example.factoryrec.ui.ScaleImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图片预览ViewPager
 *
 * @author yangcheng
 */
public class ImageViewPagerActivity extends FragmentActivity {

    // ViewPager
    private ViewPager viewpager;
    private ViewPagerAdapter adapter;
    private List<String> data;
    private List<View> mLayouts;
    private Map<Integer, Boolean> mCheckMap;

    // 索引
    private TextView tViewIndex;
    private int position;

    // 删除/勾选
    private CheckBox btnDelete;

    // 完成
    private TextView btnFinish;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // 初始化数据
        initData();

        // 初始化视图组件
        initView();

        // 设置视图组件监听器
        setViewListener();

        // // Android4.4版本以上解决虚拟按键挤压图片变形的问题
        // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
        // {
        // try
        // {
        // Window window = getWindow();
        // window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
        // WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
        // WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        // }
        // catch (Exception e)
        // {
        // e.printStackTrace();
        // }
        // }
    }

    protected void initData() {

        mCheckMap = new HashMap<Integer, Boolean>();
        mLayouts = new ArrayList<View>();
        data = (List<String>) getIntent().getSerializableExtra("imageUris");
        position = getIntent().getIntExtra("position", 0);
        if (data == null || data.isEmpty()) {
            data = new ArrayList<String>();
        }
        for (int i = 0; i < data.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_image_preview, null);
            mLayouts.add(view);
            mCheckMap.put(i, true);
        }
    }

    protected void initView() {

        setContentView(R.layout.activity_image_viewpager);

        viewpager = (ViewPager) this.findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter();
        viewpager.setAdapter(adapter);

        tViewIndex = (TextView) this.findViewById(R.id.tViewIndex);
        btnDelete = (CheckBox) this.findViewById(R.id.btnDelete);
        btnFinish = (TextView) this.findViewById(R.id.btnFinish);
        tViewIndex.setText((position + 1) + "/" + mLayouts.size());

        // 设置初始位置
        viewpager.setCurrentItem(position);
    }

    protected void setViewListener() {

        // 设置ViewPager滑动监听器
        viewpager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                // 刷新索引
                tViewIndex.setText((position + 1) + "/" + mLayouts.size());
                boolean check = mCheckMap.get(position);
                if (check) {
                    btnDelete.setChecked(true);
                } else {
                    btnDelete.setChecked(false);
                }
            }

            @Override
            public void onPageScrolled(int position, float arg1, int arg2) {

                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrollStateChanged(int position) {

                // TODO Auto-generated method stub
            }
        });

        // 设置删除/勾选监听器
        btnDelete.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                int position = viewpager.getCurrentItem();
                if (isChecked) {
                    mCheckMap.put(position, true);
                } else {
                    mCheckMap.put(position, false);
                }
            }
        });

        // 设置完成按钮监听器
        btnFinish.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.putExtra("imageUris", (Serializable) mCheckMap);
                ArrayList<String> newFilePaths = new ArrayList<String>();
                for (int i = 0; i < data.size(); i++) {
                    if (mCheckMap.get(i)) {
                        newFilePaths.add(data.get(i));
                    }
                }
                intent.putExtra("imagePath", (Serializable) newFilePaths);
                setResult(RESULT_OK, intent);
                ImageViewPagerActivity.this.finish();
            }
        });

    }

    /**
     * ViewPager适配器
     */
    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public boolean isViewFromObject(View view, Object object) {

            return view == object;
        }

        @Override
        public Parcelable saveState() {

            return super.saveState();
        }

        @Override
        public int getCount() {

            return mLayouts.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            ((ViewGroup) container).removeView(mLayouts.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ((ViewGroup) container).addView(mLayouts.get(position));
            View view = mLayouts.get(position);
            try {
                String uri = data.get(position);
                ScaleImageView imageView = (ScaleImageView) view.findViewById(R.id.imageView);
                imageView.setImage(ImageSource.uri(uri));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return view;
        }
    }

}
