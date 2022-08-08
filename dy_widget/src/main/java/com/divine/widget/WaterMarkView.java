package com.divine.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.divine.R;
import com.divine.base.utils.sys.RandomUtils;
import androidx.annotation.Nullable;

/**
 * Author: Divine
 * CreateDate: 2021/8/16
 * Describe: 可用来实现触摸滑动轨迹显示萌icon。
 */
public class WaterMarkView extends View {
    private final String TAG = "DY-WaterMarkView";
    private Context mContext;
    private int[] icMotions = new int[]{
            R.mipmap.ic_motion_1,
            R.mipmap.ic_motion_2,
            R.mipmap.ic_motion_3,
            R.mipmap.ic_motion_4,
            R.mipmap.ic_motion_5,
            R.mipmap.ic_motion_6,
            R.mipmap.ic_motion_7,
            R.mipmap.ic_motion_8,
            R.mipmap.ic_motion_9,
            R.mipmap.ic_motion_10,
            R.mipmap.ic_motion_11,
            R.mipmap.ic_motion_12,
            R.mipmap.ic_motion_13,
            R.mipmap.ic_motion_14,
            R.mipmap.ic_motion_15,
            R.mipmap.ic_motion_16,
            R.mipmap.ic_motion_17,
            R.mipmap.ic_motion_18,
            R.mipmap.ic_motion_19,
            R.mipmap.ic_motion_20
    };
    protected ValueAnimator animator;

    public WaterMarkView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    PointF touchPoint;
    Paint mPaint;
    int randomInt;
    float animatedValue = 0f;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (touchPoint != null) {
            Bitmap pointBmp = BitmapFactory.decodeResource(mContext.getResources(), icMotions[randomInt]);
            mPaint = new Paint();
            Matrix mMatrix = new Matrix();
            mMatrix.postTranslate(touchPoint.x - pointBmp.getWidth() / 2, touchPoint.y - pointBmp.getHeight() / 2);
            mMatrix.postScale(animatedValue / 100f, animatedValue / 100f, touchPoint.x - animatedValue * 2, touchPoint.y);
            canvas.drawBitmap(pointBmp, mMatrix, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG, "MotionEvent：(" + event.getX() + "," + event.getY() + ")");
        return false;
    }

    public void setTouchPoint(PointF point) {
        this.touchPoint = point;
        randomInt = RandomUtils.instance().getRandomInt(20);

        animator = ValueAnimator.ofFloat(0f, 50f);
        animator.setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatedValue = (float) animation.getAnimatedValue();
                if (animatedValue == 50) {
                    touchPoint = null;
                }
                invalidate();
            }
        });
        animator.start();
    }
}
