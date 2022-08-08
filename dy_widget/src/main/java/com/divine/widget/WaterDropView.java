package com.divine.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.divine.R;

import androidx.annotation.Nullable;

/**
 * Author: Divine
 * CreateDate: 2021/8/9
 * Describe:
 */
public class WaterDropView extends View {
    private static final String TAG = "DY-WaterDropView";
    private Context context;

    private Paint mPaint = new Paint(), mPaintSin = new Paint();
    private Path mPath = new Path();
    private Canvas mCanvas;
    private int count = 7;
    private float swing, waveLen, percent = 50, maxPercent = 100;

    public WaterDropView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        swing = getWidth() / count;
        waveLen = 3;
        this.mCanvas = canvas;
        mCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        mPaint.setColor(context.getResources().getColor(R.color.white));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(0.1f);
        mPaint.setAntiAlias(true);

        mPath.moveTo(getWidth() / 2, getHeight());
        mPath.quadTo(0, getHeight() - 15, getWidth() / 2, 0);
        mCanvas.drawPath(mPath, mPaint);

        mPath.moveTo(getWidth() / 2, getHeight());
        mPath.quadTo(getWidth(), getHeight() - 15, getWidth() / 2, 0);
        mCanvas.drawPath(mPath, mPaint);
        mCanvas.clipPath(mPath);
        mCanvas.clipPath(mPath);
        /****************************动画相关********************************/
        float curY = getHeight() - percent * getHeight() / maxPercent;
        mPaintSin.setColor(context.getResources().getColor(R.color.blue_button));
        mPaintSin.setStrokeWidth(0f);
        mPaintSin.setAntiAlias(true);
        float curSinX = 0;
        for (int i = 0; i < count; i++) {
            int j = 0;
            while (j < swing) {
                float jNew = j + percent / maxPercent * swing * count;
                float sinY = (float) (Math.sin((float) jNew / swing * (2 * Math.PI)) * waveLen + curY);
                float nextSinY = (float) (Math.sin((float) (jNew + 1) / swing * (2 * Math.PI)) * waveLen + curY);
                mCanvas.drawLine(j + curSinX, sinY, j + 1 + curSinX, nextSinY, mPaintSin);
                mCanvas.drawLine(j + curSinX, getHeight(), j + 1 + curSinX, nextSinY, mPaintSin);
                j++;
            }
            curSinX += swing;
        }
    }

    public void setPercent(float percent) {
        this.percent = percent;
        invalidate();
    }
}


