package com.divine.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author: Divine
 * CreateDate: 2021/9/1
 * Describe:
 */
public class ItemDecorationLine extends RecyclerView.ItemDecoration {
    private Context mContext;
    private int decorationColor = -1;
    private int width = 30;

    /**
     * 默认样式
     *
     * @param mContext
     */
    public ItemDecorationLine(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 设置宽度；设置是否显示同行/同列 内线条
     * @param mContext
     * @param colorResId
     */
    public ItemDecorationLine(Context mContext, int colorResId) {
        this.mContext = mContext;
        this.decorationColor = colorResId;
    }

    /**
     * 设置线条颜色;设置宽度
     *
     * @param mContext
     * @param colorResId
     * @param width
     */
    public ItemDecorationLine(Context mContext, int colorResId, int width) {
        this.mContext = mContext;
        this.decorationColor = colorResId;
        this.width = width;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int orientation = getOrientation(parent);
        if (orientation == RecyclerView.VERTICAL) {
            if (position == 0)
                outRect.top = 0;
            else
                outRect.top = width;
        } else if (orientation == RecyclerView.HORIZONTAL) {
            if (position == 0)
                outRect.left = 0;
            else
                outRect.left = width;
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        Paint mPaint = new Paint();
        if (decorationColor == -1) {
            mPaint.setColor(mContext.getResources().getColor(android.R.color.transparent));
        } else {
            mPaint.setColor(mContext.getResources().getColor(decorationColor));
        }
        int childCount = parent.getChildCount();
        int orientation = getOrientation(parent);
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (orientation == RecyclerView.VERTICAL) {
                if (i > 0)
                    c.drawRect(child.getLeft(), child.getTop(), child.getRight(), child.getTop() - width, mPaint);
            } else if (orientation == RecyclerView.HORIZONTAL) {
                if (i > 0)
                    c.drawRect(child.getLeft(), child.getTop(), child.getLeft() - width, child.getBottom(), mPaint);
            }
        }
    }

    private int getOrientation(RecyclerView parent) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        return layoutManager.getOrientation();
    }
}
