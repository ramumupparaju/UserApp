package com.incon.connect.user.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

public class MaxHeightScrollView extends NestedScrollView {

    private int maxHeight;
    private final int defaultHeight = 80;

    public MaxHeightScrollView(Context context) {
        super(context);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }


    private void init(Context context, AttributeSet attrs) {
        maxHeight = (int) (Resources.getSystem().getDisplayMetrics().heightPixels * 0.8);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}