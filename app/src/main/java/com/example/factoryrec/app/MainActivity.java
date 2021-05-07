package com.example.factoryrec.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.factoryrec.R;
import com.example.factoryrec.util.ProductItem;
import com.example.factoryrec.util.VocConstant;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    public static final int PAGE_HOME = 0;
    public static final int PAGE_DISPLAY = 1;
    public static final int PAGE_OM = 2;
    public static final int PAGE_SIGNAL = 3;
    public static final int PAGE_RESULT = 4;
    private int mCurrentPage = PAGE_HOME;

    private ArrayList<Fragment> mFragmentList;
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;

    private Button mHome_Button;
    private Button mDynamic_Button;
    private Button mOM_Button;
    private Button mSignal_Button;
    private Button mResult_Button;
    private List<Button> mButtonList;

    protected ProductItem mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //请求权限
        requestPremission();
        setContentView(R.layout.activity_main);

        mItem = ProductItem.getInstance();
        initButton();
        initViewPage();

    }

    /**
     * 请求手机权限
     */
    private void requestPremission() {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }

        List<String> permissions = new ArrayList<String>();
        for (String string : VocConstant.PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, string) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(string);
            }
        }
        Log.e("stormxz", "------当前要申请的权限个数---" + permissions.size());
        if (permissions.size() <= 0) {
            return;
        }
        String[] array = new String[permissions.size()];
        permissions.toArray(array);
        requestPermissions(array, 100001);
    }

    public ProductItem getItem() {
        return mItem;
    }

    private void initButton() {
        mHome_Button = (Button) findViewById(R.id.page_home);
        mDynamic_Button = (Button) findViewById(R.id.page_display);
        mOM_Button = (Button) findViewById(R.id.page_om);
        mSignal_Button = (Button) findViewById(R.id.page_signal);
        mResult_Button = (Button) findViewById(R.id.page_result);
        mHome_Button.setOnClickListener(this);
        mDynamic_Button.setOnClickListener(this);
        mOM_Button.setOnClickListener(this);
        mSignal_Button.setOnClickListener(this);
        mResult_Button.setOnClickListener(this);

        mButtonList = new ArrayList<Button>();
        mButtonList.add(mHome_Button);
        mButtonList.add(mDynamic_Button);
        mButtonList.add(mOM_Button);
        mButtonList.add(mSignal_Button);
        mButtonList.add(mResult_Button);
    }

    private void initViewPage() {
        mViewPager = findViewById(R.id.pager);
        mFragmentList = new ArrayList<Fragment>();
        for (int i = 0; i < 5; i++) {
            initPages(i);
        }
        mAdapter = new ViewPageAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener((ViewPager.OnPageChangeListener) mAdapter);
        mViewPager.setCurrentItem(mCurrentPage);
        mViewPager.setPageTransformer(true, new ViewPageTransformer());
        mViewPager.setOffscreenPageLimit(5);
    }

    private void initPages(int index) {
        Fragment fragment = null;
        switch (index) {
            case PAGE_HOME:
                fragment = new Fragment_Home();
                mFragmentList.add(PAGE_HOME, fragment);
                break;
            case PAGE_DISPLAY:
                fragment = new Fragment_Display();
                mFragmentList.add(PAGE_DISPLAY, fragment);
                break;
            case PAGE_OM:
                fragment = new Fragment_OM();
                mFragmentList.add(PAGE_OM, fragment);
                break;
            case PAGE_SIGNAL:
                fragment = new Fragment_Signal();
                mFragmentList.add(PAGE_SIGNAL, fragment);
                break;
            case PAGE_RESULT:
                fragment = new Fragment_Result();
                mFragmentList.add(PAGE_RESULT,fragment);
        }
    }

    private class ViewPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);
            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);
            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);
                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);
                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    private class ViewPageAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        private List<Fragment> mList = new ArrayList<Fragment>();

        public ViewPageAdapter(FragmentManager paramFragmentManager, ArrayList<Fragment> fragmentList) {
            super(paramFragmentManager);
            mList = fragmentList;
        }

        @Override
        public Fragment getItem(int index) {
            return mList.get(index);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public void onPageScrolled(int i, float v, int i1) {
        }

        @Override
        public void onPageSelected(int index) {
            Log.i(TAG, "onPageSelected : index = " + index);
            mCurrentPage = index;
            for (int i = 0; i < 5; i++) {
                if (index == i) {
                    mButtonList.get(i).setBackgroundColor(getResources().getColor(R.color.button_color_selected, null));
                } else {
                    mButtonList.get(i).setBackgroundColor(getResources().getColor(R.color.button_color_unselected, null));
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    }

    @Override
    public void onClick(View v) {
        if (mViewPager == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.page_home:
                mViewPager.setCurrentItem(PAGE_HOME, true);
                break;
            case R.id.page_display:
                mViewPager.setCurrentItem(PAGE_DISPLAY, true);
                break;
            case R.id.page_om:
                mViewPager.setCurrentItem(PAGE_OM, true);
                break;
            case R.id.page_signal:
                mViewPager.setCurrentItem(PAGE_SIGNAL, true);
                break;
            case R.id.page_result:
                mViewPager.setCurrentItem(PAGE_RESULT, true);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFragmentList.get(mViewPager.getCurrentItem()).onActivityResult(requestCode, resultCode, data);

    }
}
