package com.divine.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.divine.R;

import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.Nullable;

/**
 * Author: Divine
 * CreateDate: 2020/9/11
 * Describe:
 */
public class RandomVerificationCodeView extends View implements View.OnClickListener {
    /**
     * 需要用到的字符数组
     */
    private static final char[] CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    /**
     * 二维码最大长度为4
     */
    private static final int CODE_LENGTH = 4;

    /**
     * 画数字
     */
    private Paint mPaintText;
    /**
     * 画背景
     */
    private Paint mPaintBg;
    /**
     * 二维码文字
     */
    private String mText;
    private ArrayList<VerBean> vers;
    /**
     * 二维码字体颜色
     */
    private int mTextColor;
    /**
     * 干扰点的数量
     */
    private int mPointSize;
    private ArrayList<PointBean> points;
    /**
     * 干扰线的数量
     */
    private int mLineSize;
    private ArrayList<LineBean> lines;
    /**
     * 二维码字体大小
     */
    private int mTextSize;
    private Rect mRect;
    private Random mRandom;
    private StringBuilder mBuilder;
    private float mTextWidth;
    private boolean refresh = true;

    public RandomVerificationCodeView(Context context) {
        this(context, null);
    }

    public RandomVerificationCodeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RandomVerificationCodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取在attr中自定义的属性
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.MyVerificationCode, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int index = a.getIndex(i);
            if (index == R.styleable.MyVerificationCode_titleText) {
                mText = a.getString(index);
            } else if (index == R.styleable.MyVerificationCode_textColor) {
                mTextColor = a.getColor(index, Color.BLACK);
            } else if (index == R.styleable.MyVerificationCode_titleSize) {
                mTextSize = a.getDimensionPixelSize(index, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
            } else if (index == R.styleable.MyVerificationCode_pointSize) {
                mPointSize = a.getInteger(index, 40);
            } else if (index == R.styleable.MyVerificationCode_lineSize) {
                mLineSize = a.getInteger(index, 3);
            }
        }
        a.recycle();
        init();
    }

    private void init() {
        mRandom = new Random();
        mPaintBg = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBg.setColor(Color.parseColor("#cccccc"));
        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        //设置字体大小
        mPaintText.setTextSize(mTextSize);
        //设置字体粗细
        mPaintText.setFakeBoldText(true);
        mBuilder = new StringBuilder();
        //这里判断xml中有没有给属性传值，如果没有就随机生成
        if (TextUtils.isEmpty(mText)) {
            for (int i = 0; i < CODE_LENGTH; i++) {
                mBuilder.append(CHARS[mRandom.nextInt(CHARS.length)]);
            }
        } else {
            mBuilder.append(mText);
        }
        mRect = new Rect();


        mPaintText.getTextBounds(mBuilder.toString(), 0, mBuilder.toString().length(), mRect);
        setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = widthSize;
        int height = heightSize;
        //当宽高都为wrap_content的时候
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            mPaintText.getTextBounds(mBuilder.toString(), 0, mBuilder.toString().length(), mRect);
            width = mRect.width() + getPaddingLeft() + getPaddingRight();
            height = mRect.height() + getPaddingTop() + getPaddingBottom();
            //当宽为wrap_content
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = mRect.width() + getPaddingLeft() + getPaddingRight();
            //当高为wrap_content
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = mRect.height() + getPaddingTop() + getPaddingBottom();
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        if (refresh) {
            makeConfig(width, height);
            refresh = false;
        }
        //画矩形
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaintBg);
        drawText(canvas, width, height);
        //设置干扰点
        for (int i = 0; i < mPointSize; i++) {
            PointBean point = points.get(i);
            mPaintText.setColor(point.color);
            drawPoint(canvas, mPaintText, point);
        }
        //设置干扰线
        for (int i = 0; i < mLineSize; i++) {
            LineBean line = lines.get(i);
            mPaintText.setColor(line.color);
            drawLine(canvas, mPaintText, line);
        }
    }

    /**
     * 添加点击事件
     * 点击更换
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        refresh();
    }

    /**
     * 绘制倾斜的文字
     *
     * @param canvas 画布
     * @param width  view的宽度
     * @param height view的高度
     */
    private void drawText(Canvas canvas, int width, int height) {
        //第一个字符距离左边的宽度
        float charWidth = width / 5 - 10;
        //字符的高度
        for (int i = 0; i < vers.size(); i++) {
            VerBean ver = vers.get(i);
            ////用来保存Canvas的状态。save之后，可以调用Canvas的平移、放缩、旋转、错切、裁剪等操作。
            canvas.save();
            //设置旋转
            canvas.rotate(ver.rotate, width / 2, height / 2);
            //设置每个字体的颜色
            mPaintText.setColor(ver.color);
            //画文字
            canvas.drawText(String.valueOf(mBuilder.toString().charAt(i)), charWidth, height * 1 / 2 + mRect.height() / 2, mPaintText);
            //字符距离
            charWidth += width / 5;
            canvas.restore();
        }
        //重新设置paint
        mPaintText.setStrokeWidth(2f);
    }

    /**
     * 返回验证码
     */
    public String getVerificationCode() {
        return mBuilder.toString();
    }

    /**
     * 设置矩形的背景颜色
     *
     * @param bgColor
     */
    public void setBgColor(int bgColor) {
        mPaintBg.setColor(bgColor);
        postInvalidate();
    }

    /**
     * 设置字体颜色
     */
    public int getTextColor(int r, int g, int b) {

        if (r > 255) {
            r = 255;
        }
        if (g > 255) {
            g = 255;
        }
        if (b > 255) {
            b = 255;
        }
        int tr = mRandom.nextInt(r);
        int tg = mRandom.nextInt(g);
        int tb = mRandom.nextInt(b);
        return Color.rgb(tr, tg, tb);
    }

    /**
     * 生成干扰点
     */
    private void drawPoint(Canvas canvas, Paint paint, PointBean pointBean) {
        canvas.drawPoint(pointBean.point.x, pointBean.point.y, paint);
    }

    /**
     * 生成干扰线
     */
    private void drawLine(Canvas canvas, Paint paint, LineBean lineBean) {
        canvas.drawLine(lineBean.startX, lineBean.startY, lineBean.stopX, lineBean.stopY, paint);
    }

    /**
     * 设置干扰点和干扰线的颜色
     *
     * @param width  干扰线长度和点位置的宽度限制
     * @param height 干扰线长度和点位置的长度限制
     */
    private void makeConfig(int width, int height) {
        int verLen = mBuilder.toString().length();
        vers = new ArrayList<>();
        for (int i = 0; i < verLen; i++) {
            //随机生成倾斜角度
            int rotate = mRandom.nextInt(10);
            //如果是0则正方向倾斜，如果是1则负方向倾斜
            rotate = mRandom.nextInt(2) == 0 ? rotate : -rotate;
            int color = getTextColor(255, 255, 255);
            vers.add(new VerBean(rotate, color));
        }

        points = new ArrayList<>();
        for (int i = 0; i < mPointSize; i++) {
            int pointColor = getTextColor(255, 255, 255);
            points.add(new PointBean(new PointF(mRandom.nextInt(width), mRandom.nextInt(height)), pointColor));
        }
        lines = new ArrayList<>();
        for (int i = 0; i < mLineSize; i++) {
            int startX = mRandom.nextInt(width);
            int startY = mRandom.nextInt(height);
            int stopX = mRandom.nextInt(width);
            int stopY = mRandom.nextInt(height);

            int lineColor = getTextColor(255, 255, 255);
            lines.add(new LineBean(startX, startY, stopX, stopY, lineColor));
        }
    }

    public void refresh() {
        refresh = true;
        //先清空
        mBuilder.delete(0, mBuilder.length());
        for (int i = 0; i < CODE_LENGTH; i++) {
            mBuilder.append(CHARS[mRandom.nextInt(CHARS.length)]);
        }
        postInvalidate();
    }

    private class VerBean {
        public int rotate;
        public int color;

        public VerBean(int rotate, int color) {
            this.rotate = rotate;
            this.color = color;
        }
    }

    private class PointBean {
        public PointF point;
        public int color;

        public PointBean(PointF point, int color) {
            this.point = point;
            this.color = color;
        }
    }

    private class LineBean {
        public int startX;
        public int startY;
        public int stopX;
        public int stopY;
        public int color;

        public LineBean(int startX, int startY, int stopX, int stopY, int color) {
            this.startX = startX;
            this.startY = startY;
            this.stopX = stopX;
            this.stopY = stopY;
            this.color = color;
        }
    }
}
