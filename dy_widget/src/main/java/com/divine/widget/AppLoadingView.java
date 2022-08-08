package com.divine.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

import com.divine.R;

/**
 * Author: Divine
 * CreateDate: 2021/8/9
 * Describe:
 */
public class AppLoadingView extends View {
    private static final String TAG = "DY-WaterDropView";
    private Context context;

    private Paint mPaint = new Paint(), mPaintSin = new Paint();
    private Path mPath = new Path();
    private Canvas mCanvas;
    private int count = 7;
    private float swing, waveLen, percent = 50, maxPercent = 100;

    public AppLoadingView(Context context, @Nullable AttributeSet attrs) {
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

        mPaintSin.setColor(context.getResources().getColor(R.color.white));
        mPaintSin.setStrokeWidth(2f);
        mPaintSin.setAntiAlias(true);
        mCanvas.drawRect(0,0,getWidth(),getHeight(),mPaintSin);
        Bitmap bmpLauncher = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        Matrix mMatrix = new Matrix();
        int bmpH = bmpLauncher.getHeight();
        int bmpW = bmpLauncher.getWidth();
        mMatrix.postTranslate((getWidth() - bmpW) / 2, (getHeight() - bmpH) / 2);
        mCanvas.drawBitmap(bmpLauncher, mMatrix, mPaint);
        /****************************动画相关********************************/
        float curY = getHeight() - percent * getHeight() / maxPercent;

        float curSinX = 0;
        for (int i = 0; i < count; i++) {
            int j = 0;
            while (j < swing) {
                float jNew = j + percent / maxPercent * swing * count;
                float sinY = (float) (Math.sin((float) jNew / swing * (2 * Math.PI)) * waveLen + curY);
                float nextSinY = (float) (Math.sin((float) (jNew + 1) / swing * (2 * Math.PI)) * waveLen + curY);
                mCanvas.drawLine(j + curSinX, sinY, j + 1 + curSinX, nextSinY, mPaintSin);
                mCanvas.drawLine(j + curSinX, 0, j + 1 + curSinX, nextSinY, mPaintSin);
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


