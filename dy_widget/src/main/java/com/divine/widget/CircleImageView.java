package com.divine.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Author: Divine
 * CreateDate: 2021/10/15
 * Describe:
 */
public class CircleImageView extends androidx.appcompat.widget.AppCompatImageView {

    public CircleImageView(@NonNull Context context) {
        super(context);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        } else {
            xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
            srcPath = new Path();
        }
    }

    public CircleImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        } else {
            xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
            srcPath = new Path();
        }
    }

    public CircleImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        } else {
            xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
            srcPath = new Path();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float radius = Math.min(w, h) / 2.0f;
        srcRectF.set(w / 2.0f - radius, h / 2.0f - radius, w / 2.0f + radius, h / 2.0f + radius);
    }

    PorterDuffXfermode xfermode;
    RectF srcRectF = new RectF();
    Path srcPath;

    @Override
    protected void onDraw(Canvas canvas) { // 使用离屏缓存，新建一个srcRectF区域大小的图层
        canvas.saveLayer(srcRectF, null, Canvas.ALL_SAVE_FLAG);
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        float r = Math.min(w, h) / 2.0f;
        Path circlePath = new Path();
        Paint mPaint = new Paint();
        srcPath.reset();
        circlePath.reset();
        mPaint.reset();
        circlePath.addCircle(w / 2, h / 2, r, Path.Direction.CCW);
        mPaint.setAntiAlias(true);
        // 画笔为填充模式
        mPaint.setStyle(Paint.Style.FILL);
        // 设置混合模式
        mPaint.setXfermode(xfermode);
        // 绘制path
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            canvas.drawPath(circlePath, mPaint);
        } else {
            srcPath.addRect(srcRectF, Path.Direction.CCW);
            // 计算tempPath和path的差集
            srcPath.op(circlePath, Path.Op.DIFFERENCE);
            canvas.drawPath(srcPath, mPaint);
        }
        // 清除Xfermode
        mPaint.setXfermode(null);
        // 恢复画布状态
        canvas.restore();

        Paint mBorderPaint = new Paint();
        circlePath.reset();
        circlePath.addCircle(w / 2, h / 2, r, Path.Direction.CCW);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(1);
        //        canvas.drawPath(circlePath, mBorderPaint);
    }
}
