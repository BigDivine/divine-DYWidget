package com.divine.widget.dychart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.divine.R;
import com.divine.dy.lib_utils.sys.DensityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Divine
 * CreateDate: 2022/08/23
 * Describe: 柱状统计图
 */
public class PillarView extends View {
    private Context context;
    //外部可设置属性
    //x轴样式
    private int xAxisColor;
    private int xAxisTextColor;
    private float xAxisTextSize;
    private float xAxisWidth;
    //y轴样式
    private int yAxisColor;
    private int yAxisTextColor;
    private float yAxisTextSize;
    private float yAxisWidth;

    //坐标轴圆点
    private float originPointX, originPointY;
    //x轴画笔
    private Paint xPaint;
    //x轴文字画笔
    private Paint xTextPaint;
    //y轴画笔
    private Paint yPaint;
    //虚线画笔
    private Paint dummyPaint;
    //y轴文字画笔
    private Paint yTextPaint;
    //柱状画笔
    private Paint pillarPaint;
    //组件的padding(:dp)
    private int padding = 20;
    //x,y轴多出来一部分
    private int axisMore = 100;
    //开始绘制
    private boolean start = false;
    //数据集合
    private List<PillarBean> data;
    //柱形的颜色
    private ArrayList<Integer> colors;
    //点击的折点
    private PointF touchPoint;
    private boolean isShowPoint = false;
    //下为可设置项
    //必填，y轴最大值
    private float maxYNum;
    //x,y轴分割，默认为这个，可以增加设置项配置。
    private String[] xSplit, ySplit;
    //x,y轴的标题
    private String yTitle, xTitle;
    //是否显示：xy轴虚线，横竖线；x轴虚线，竖线；y轴虚线，横线
    private boolean showXYDummyLine, showXDummyLine, showYDummyLine;
    //是否显示logo背景
    private boolean showLogoBackground = true;

    public PillarView(Context context) {
        super(context);
        this.context = context;
        this.data = new ArrayList<>();
        this.colors = new ArrayList<>();
    }

    public PillarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.data = new ArrayList<>();
        this.colors = new ArrayList<>();
        initAttrs(attrs);
    }

    public PillarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.data = new ArrayList<>();
        this.colors = new ArrayList<>();
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        this.data = new ArrayList<>();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BrokenLineView);
        int defaultAxisColor = context.getResources().getColor(R.color.axis_default_color);
        int defaultAxisTextColor = context.getResources().getColor(R.color.axis_default_text_color);
        int defaultLineColor = context.getResources().getColor(R.color.broken_line_default_color);

        xAxisColor = typedArray.getColor(R.styleable.BrokenLineView_X_axis_color, defaultAxisColor);
        xAxisWidth = typedArray.getDimension(R.styleable.BrokenLineView_X_axis_width, DensityUtils.sp2px(1, context));
        xAxisTextColor = typedArray.getColor(R.styleable.BrokenLineView_X_axis_text_color, defaultAxisTextColor);
        xAxisTextSize = typedArray.getDimension(R.styleable.BrokenLineView_X_axis_text_size, DensityUtils.sp2px(10, context));

        yAxisColor = typedArray.getColor(R.styleable.BrokenLineView_Y_axis_color, defaultAxisColor);
        yAxisTextColor = typedArray.getColor(R.styleable.BrokenLineView_Y_axis_text_color, defaultAxisTextColor);
        yAxisTextSize = typedArray.getDimension(R.styleable.BrokenLineView_Y_axis_text_size, DensityUtils.sp2px(10, context));
        yAxisWidth = typedArray.getDimension(R.styleable.BrokenLineView_Y_axis_width, DensityUtils.sp2px(1, context));
    }

    private void initView() {
        setXAxis();
        setYAxis();
        setAxisDummy();
        setPillar();

        if (null == ySplit) {
            ySplit = new String[]{"0%", "25%", "50%", "75%", "100%"};
        }
        if (null == xSplit) {
            if (null != data && data.size() > 0) {
                int count = data.size();
                xSplit = new String[count];
                for (int i = 0; i < count; i++) {
                    xSplit[i] = data.get(i).getX() + "";
                }
            }
        }
        if (TextUtils.isEmpty(xTitle)) {
            xTitle = "x";
        }
        if (TextUtils.isEmpty(yTitle)) {
            yTitle = "y";
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setViewBackground(canvas);
        int paddingPx = DensityUtils.dip2px(padding, context);
        if (start) {
            //确定坐标原点
            //取y轴标点文字最长-用以确定圆点x坐标
            int yCount = ySplit.length;
            float maxYTextLength = yTextPaint.measureText(yTitle);
            for (int n = 0; n < yCount; n++) {
                float textLength = yTextPaint.measureText(ySplit[n]);
                if (maxYTextLength < textLength) {
                    maxYTextLength = textLength;
                }
            }
            originPointX = paddingPx + maxYTextLength;
            originPointY = getMeasuredHeight() - paddingPx - (DensityUtils.dip2px(xAxisTextSize, context));
            //xy终点坐标
            float xStopX = getMeasuredWidth() - paddingPx;
            float yStopY = paddingPx;
            //x轴必须等分，所以data中的bean的x一定是x轴总长可以整除的数字
            int pointCount = data.size();
            float xLength = xStopX - originPointX - axisMore;
            float xUnitLen = xLength / (float) pointCount;
            //y轴等分
            float yLength = originPointY - yStopY - axisMore;
            float yUnitLen = yLength / (float) (yCount - 1);
            //画x轴
            canvas.drawLine(originPointX, originPointY, xStopX, originPointY, xPaint);
            //x轴箭头
            float[] xArrow = new float[]{xStopX, originPointY, xStopX - 30, originPointY - 10, xStopX, originPointY, xStopX - 30, originPointY + 10};
            canvas.drawLines(xArrow, xPaint);
            float XTextLength = xTextPaint.measureText(xTitle);
            canvas.drawText(xTitle, xStopX - XTextLength, originPointY + xAxisTextSize + 10, xTextPaint);
            //画x轴的标点
            for (int n = 0; n < pointCount; n++) {
                canvas.drawCircle(originPointX + n * xUnitLen, originPointY, 5, xPaint);
                if (showXYDummyLine || showXDummyLine) {
                    canvas.drawLine(originPointX + (n + 1) * xUnitLen, originPointY, originPointX + (n + 1) * xUnitLen, originPointY - (yCount - 1) * yUnitLen, dummyPaint);
                }
                float textLength = xTextPaint.measureText(xSplit[n]);
                canvas.drawText(xSplit[n], originPointX + n * xUnitLen + (xUnitLen - textLength) / 2, originPointY + xAxisTextSize, xTextPaint);
            }
            //画y轴
            canvas.drawLine(originPointX, originPointY, originPointX, yStopY, yPaint);
            float YTextLength = yTextPaint.measureText(yTitle);
            canvas.drawText(yTitle, originPointX - YTextLength - 10, yStopY + yAxisTextSize / 2, yTextPaint);
            //y轴箭头
            float[] yArrow = new float[]{originPointX, yStopY, originPointX - 10, yStopY + 30, originPointX, yStopY, originPointX + 10, yStopY + 30};
            canvas.drawLines(yArrow, yPaint);
            //画y轴的标点
            for (int n = 0; n < yCount; n++) {
                canvas.drawCircle(originPointX, originPointY - n * yUnitLen, 5, yPaint);
                if (showXYDummyLine || showYDummyLine) {
                    canvas.drawLine(originPointX, originPointY - n * yUnitLen, originPointX + pointCount * xUnitLen, originPointY - n * yUnitLen, dummyPaint);
                }
                if (n > 0) {
                    float textLength = yTextPaint.measureText(ySplit[n]);
                    canvas.drawText(ySplit[n], originPointX - textLength - 10, originPointY - n * yUnitLen + yAxisTextSize / 2.0f, yTextPaint);
                }
            }
            //记录每个折点坐标
            //画坐标点
            for (int i = 0; i < pointCount; i++) {
                if (this.colors.size() > 0) {
                    pillarPaint.setColor(this.colors.get((i)));
                } else {
                    pillarPaint.setColor(DyChartUtils.getRandomColor());
                }
                PillarBean bean = data.get(i);
                float yNum = maxYNum * data.get(i).getY();
                //数据对应的柱状图
                RectF pillarRectF = new RectF();
                pillarRectF.bottom = originPointY;
                pillarRectF.left = originPointX + i * xUnitLen;
                pillarRectF.top = originPointY - bean.getY() * yLength;
                pillarRectF.right = originPointX + (i + 1) * xUnitLen;
                //全长柱状图
                RectF xRectF = new RectF();
                xRectF.bottom = originPointY;
                xRectF.left = originPointX + i * xUnitLen;
                xRectF.top = originPointY - yLength;
                xRectF.right = originPointX + (i + 1) * xUnitLen;
                if (null != touchPoint && xRectF.contains(touchPoint.x, touchPoint.y)) {
                    pillarPaint.setColor(Color.parseColor("#EF556E"));
                    canvas.drawText(yNum + "", pillarRectF.left, pillarRectF.top, pillarPaint);
                }
                canvas.drawRect(pillarRectF, pillarPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        touchPoint = new PointF(eventX, eventY);
        invalidate();
        return super.onTouchEvent(event);
    }

    /**
     * 设置控件背景图片
     *
     * @param canvas
     */
    private void setViewBackground(Canvas canvas) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_dy_launcher);
        Paint bgPaint = new Paint();
        bgPaint.setAlpha(100);
        Matrix mx = new Matrix();
        float vW = getMeasuredWidth(), vH = getMeasuredHeight();
        float bW = bitmap.getWidth(), bH = bitmap.getHeight();
        float wScale = vW / bW, hScale = vH / bH;
        mx.setScale(wScale, hScale);
        canvas.drawBitmap(bitmap, mx, bgPaint);
    }

    /**
     * x轴画笔
     */
    private void setXAxis() {
        //x轴
        xPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xPaint.setAntiAlias(true);
        xPaint.setColor(xAxisColor);
        xPaint.setStrokeWidth(xAxisWidth);
        //x轴文字颜色
        xTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xTextPaint.setAntiAlias(true);
        xTextPaint.setColor(xAxisTextColor);
        xTextPaint.setStyle(Paint.Style.FILL);
        xTextPaint.setTextSize(xAxisTextSize);
    }

    /**
     * y轴画笔
     */
    private void setYAxis() {
        //x轴
        yPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        yPaint.setAntiAlias(true);
        yPaint.setColor(yAxisColor);
        yPaint.setStrokeWidth(yAxisWidth);
        //x轴文字颜色
        yTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        yTextPaint.setAntiAlias(true);
        yTextPaint.setColor(yAxisTextColor);
        yTextPaint.setStyle(Paint.Style.FILL);
        yTextPaint.setTextSize(yAxisTextSize);
    }

    /**
     * 坐标轴横着虚线的画笔
     */
    private void setAxisDummy() {
        dummyPaint = new Paint();
        //DashPathEffect中第一个参数float[0]为虚线段长度，float[1]为虚线间隔，float长度必须为2的倍数；第二个参数为第一条虚线端隐藏的长度
        DashPathEffect mDashPathEffect = new DashPathEffect(new float[]{10, 20, 10, 5, 1, 5}, 40);
        dummyPaint.setStyle(Paint.Style.STROKE);
        dummyPaint.setPathEffect(mDashPathEffect);
        dummyPaint.setColor(Color.parseColor("#60000000"));
    }

    private void setPillar() {
        pillarPaint = new Paint();
        pillarPaint.setStyle(Paint.Style.FILL);
        pillarPaint.setAntiAlias(true);
    }

    public void setSplit(String[] xSplit, String[] ySplit) {
        this.xSplit = xSplit;
        this.ySplit = ySplit;
    }

    public void setAxisTitle(String xTitle, String yTitle) {
        this.xTitle = xTitle;
        this.yTitle = yTitle;
    }

    public void showXYDummyLine(boolean showXYDummyLine) {
        this.showXYDummyLine = showXYDummyLine;
    }

    public void showYDummyLine(boolean showYDummyLine) {
        this.showYDummyLine = showYDummyLine;
    }

    public void showXDummyLine(boolean showXDummyLine) {
        this.showXDummyLine = showXDummyLine;
    }

    public void showLogoBackground(boolean showLogoBackground) {
        this.showLogoBackground = showLogoBackground;
    }

    public void startDraw(List<PillarBean> data, float maxYNum) {
        start = true;
        this.maxYNum = maxYNum;
        this.data = data;
        for (int i = 0; i < this.data.size(); i++) {
            this.colors.add(DyChartUtils.getRandomColor());
        }
        initView();
        invalidate();
    }

    public void startDraw(List<PillarBean> data, float maxYNum, ArrayList<Integer> colors) {
        start = true;
        this.maxYNum = maxYNum;
        this.data = data;
        this.colors = colors;
        initView();
        invalidate();

    }
}
