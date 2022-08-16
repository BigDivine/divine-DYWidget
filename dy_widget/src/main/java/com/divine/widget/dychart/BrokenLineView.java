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
import android.graphics.PointF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.divine.R;
import com.divine.dy.lib_utils.sys.DensityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Divine
 * CreateDate: 2020/10/20
 * Describe: 折线统计图
 */
public class BrokenLineView extends View {
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
    //折线样式
    private int brokenLineColor;
    private int brokenLineDotColor;
    private float brokenLineWidth;
    private int brokenLineDotMode;
    //折点提示文字样式
    private int brokenDotTextColor;
    private int brokenDotTextSize;
    //实心圆点
    public final static int DOT_MODE_SOLID = 0;
    //空心圆点
    public final static int DOT_MODE_HOLLOW = 1;
    //无边框空心圆点
    public final static int DOT_MODE_FRAMELESS = 2;
    //坐标轴圆点
    private float originPointX, originPointY;
    //x轴画笔
    private Paint xPaint;
    //x轴文字画笔
    private Paint xTextPaint;
    //x轴虚线画笔
    //    private Paint xLinePaint;
    //y轴画笔
    private Paint yPaint;
    //y轴画笔
    private Paint dummyPaint;
    //y轴文字画笔
    private Paint yTextPaint;
    //y轴虚线画笔
    //    private Paint yLinePaint;
    //折线点画笔
    private Paint pointPaint;
    //折线画笔
    private Paint brokenLinePaint;
    //选中的折点画笔
    private Paint effectPaint;
    //选中折点指示线的画笔
    private Paint effectLinePaint;
    //折点半径
    private float brokenDotRadius = 5;
    //组件的padding(:dp)
    private int padding = 20;
    //x,y轴多出来一部分
    private int axisMore = 100;
    //开始绘制折线表
    private boolean start = false;
    //折点集合
    private List<BrokenLineBean> data;
    //折点对应的控件的坐标点
    private ArrayList<PointF> pointFS;
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

    public BrokenLineView(Context context) {
        super(context);
        this.context = context;
        this.data = new ArrayList<>();
    }

    public BrokenLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttrs(attrs);
    }

    public BrokenLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
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

        brokenLineColor = typedArray.getColor(R.styleable.BrokenLineView_broken_line_color, defaultLineColor);
        brokenLineDotColor = typedArray.getColor(R.styleable.BrokenLineView_broken_line_dot_color, defaultLineColor);
        brokenLineWidth = typedArray.getDimension(R.styleable.BrokenLineView_broken_line_width, 2f);
        brokenLineDotMode = typedArray.getInteger(R.styleable.BrokenLineView_broken_line_dot_mode, DOT_MODE_SOLID);
        brokenDotTextSize = typedArray.getInteger(R.styleable.BrokenLineView_broken_line_dot_text_size, DensityUtils.sp2px(10, context));
        brokenDotTextColor = typedArray.getInteger(R.styleable.BrokenLineView_broken_line_dot_text_color, defaultLineColor);

    }

    private void initView() {
        setXAxis();
        setYAxis();
        setAxisDummy();
        setPoint();
        setBrokenLine();
        //折点文字颜色
        setEffect();
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
            float xUnitLen = xLength / (float) (pointCount - 1);
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
                    canvas.drawLine(originPointX + n * xUnitLen, originPointY, originPointX + n * xUnitLen, originPointY - (yCount - 1) * yUnitLen, dummyPaint);
                }
                float textLength = xTextPaint.measureText(xSplit[n]);
                canvas.drawText(xSplit[n], originPointX + n * xUnitLen - textLength / 2, originPointY + xAxisTextSize, xTextPaint);
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
                    canvas.drawLine(originPointX, originPointY - n * yUnitLen, originPointX + (pointCount - 1) * xUnitLen, originPointY - n * yUnitLen, dummyPaint);
                }
                if (n > 0) {
                    float textLength = yTextPaint.measureText(ySplit[n]);
                    canvas.drawText(ySplit[n], originPointX - textLength - 10, originPointY - n * yUnitLen + yAxisTextSize / 2.0f, yTextPaint);
                }
            }
            //记录每个折点坐标
            float[] points = new float[pointCount * 2];
            pointFS = new ArrayList<>();
            //画坐标点
            for (int i = 0; i < pointCount; i++) {
                BrokenLineBean bean = data.get(i);
                float circleX;
                if (bean.getX() == 0f) {
                    circleX = originPointX;
                } else {
                    circleX = originPointX + xUnitLen * (i);
                }
                float circleY = originPointY - bean.getY() * yLength;
                if (null != touchPoint && circleX == touchPoint.x && circleY == touchPoint.y && isShowPoint) {
                    float yNum = maxYNum * data.get(i).getY();
                    canvas.drawCircle(circleX, circleY, brokenDotRadius, effectLinePaint);
                    canvas.drawText(yNum + "", circleX + 10, circleY + brokenDotTextSize / 2, effectPaint);
                    canvas.drawLine(circleX, originPointY, circleX, originPointY - (yCount - 1) * yUnitLen, effectLinePaint);
                    canvas.drawLine(originPointX, circleY, originPointX + (pointCount - 1) * xUnitLen, circleY, effectLinePaint);
                } else {
                    canvas.drawCircle(circleX, circleY, brokenDotRadius, pointPaint);
                }
                points[i * 2] = circleX;
                points[(i * 2) + 1] = circleY;
                pointFS.add(new PointF(circleX, circleY));
            }
            //连线
            for (int j = 0; j < pointFS.size() - 1; j++) {
                PointF pointF = pointFS.get(j);
                PointF nextPointF = pointFS.get(j + 1);
                if (brokenLineDotMode == DOT_MODE_HOLLOW) {
                    float xLen = Math.abs(nextPointF.x - pointF.x);
                    float yLen = Math.abs(nextPointF.y - pointF.y);
                    float distance = (float) Math.sqrt(xLen * xLen + yLen * yLen);
                    float xCir, yCir;
                    xCir = xLen / distance * brokenDotRadius;
                    yCir = yLen / distance * brokenDotRadius;
                    if (nextPointF.y - pointF.y > 0) {
                        canvas.drawLine(pointF.x + xCir, pointF.y + yCir, nextPointF.x - xCir, nextPointF.y - yCir, brokenLinePaint);
                    } else {
                        canvas.drawLine(pointF.x + xCir, pointF.y - yCir, nextPointF.x - xCir, nextPointF.y + yCir, brokenLinePaint);
                    }
                } else {
                    canvas.drawLine(pointF.x, pointF.y, nextPointF.x, nextPointF.y, brokenLinePaint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        if (null != pointFS && pointFS.size() > 0) {
            isShowPoint = true;
            for (int i = 0; i < pointFS.size(); i++) {
                PointF point = pointFS.get(i);
                float pointX = point.x;
                float pointY = point.y;
                if (eventX < pointX + 30 && eventX > pointX - 30
                        && eventY < pointY + 30 && eventY > pointY - 30) {
                    touchPoint = point;
                    invalidate();
                }
            }
        }
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

    /**
     * 折点画笔
     */
    private void setPoint() {
        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setAntiAlias(true);
        pointPaint.setColor(brokenLineDotColor);
        if (brokenLineDotMode == DOT_MODE_SOLID) {
            //实心圆点
            pointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        } else if (brokenLineDotMode == DOT_MODE_HOLLOW) {
            //空心圆点
            pointPaint.setStyle(Paint.Style.STROKE);
        } else {
            //实心无边
            pointPaint.setStyle(Paint.Style.FILL);
        }
        pointPaint.setStrokeWidth(DensityUtils.dip2px(1, context));
    }

    /**
     * 折线画笔
     */
    private void setBrokenLine() {
        brokenLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        brokenLinePaint.setAntiAlias(true);
        brokenLinePaint.setColor(brokenLineColor);
        brokenLinePaint.setStrokeWidth(brokenLineWidth);
    }

    /**
     * 折点提示文字
     */
    private void setEffect() {
        effectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        effectPaint.setColor(brokenDotTextColor);
        effectPaint.setTextSize(brokenDotTextSize);
        effectPaint.setStyle(Paint.Style.FILL);
        effectPaint.setAntiAlias(true);

        effectLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        effectLinePaint.setColor(Color.parseColor("#EF556E"));
        effectLinePaint.setTextSize(brokenDotTextSize);
        effectLinePaint.setStyle(Paint.Style.FILL);
        effectLinePaint.setAntiAlias(true);
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

    public void startDraw(List<BrokenLineBean> data, float maxYNum) {
        start = true;
        this.maxYNum = maxYNum;
        this.data = data;
        initView();
        invalidate();
    }
}