package com.example.factoryrec.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 自定义解决与ScrollView冲突的GridView
 */
public class ScrollGridView extends GridView {

    public ScrollGridView(Context context) {

        super(context);
    }

    public ScrollGridView(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    // 测量尺寸(解决ScrollView嵌套ListView产生的冲突问题)
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
