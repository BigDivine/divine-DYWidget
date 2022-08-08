package com.divine.widget.old;

import android.content.Context;

import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * viewpager 屏蔽左右滑动
 * <p>
 * author yzl
 * <p>
 * 2018-11-19
 */
public class ControlScrollViewPager extends ViewPager {
    private boolean isCanScroll = false;

    public void setCanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    public ControlScrollViewPager(@NonNull Context context) {
        super(context);
    }

    public ControlScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isCanScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            performClick();
        }
        return isCanScroll;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void setCurrentItem(int item) {
        this.setCurrentItem(item, false);
    }
}
