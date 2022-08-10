package com.divine.widget.dychart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.divine.R;
import com.divine.base.utils.sys.DateUtils;
import com.divine.dy.lib_utils.sys.DensityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: Divine
 * CreateDate: 2020/10/20
 * Describe:
 */
public class BrokenLineView extends View {
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
    //实心圆点
    public final static int DOT_MODE_SOLID = 0;
    //空心圆点
    public final static int DOT_MODE_HOLLOW = 1;
    //无边框空心圆点
    public final static int DOT_MODE_FRAMELESS = 2;
    //坐标轴圆点
    private float originPointX, originPointY;
    private Context context;
    private Paint xPaint;
    private Paint xTextPaint;
    private Paint xLinePaint;
    private Paint yPaint;
    private Paint yTextPaint;
    private Paint yLinePaint;
    private Paint pointPaint;
    //    private Paint circlePaint;
    private Paint brokenLinePaint;
    private Paint effectPaint;

    private float yuandianx, yuandiany, height, width;
    private String[] ysplit = {"25", "50", "75", "100"};
    private float max;
    private int margin = 13;
    private float circleDotRaduis = 1;

    private float gao;

    private boolean start = false;
    private List<Map<String, Float>> pointList;

    private List<BrokenLineBean> data;
    private boolean isShowPoint = false;

    private int choose;
    private float chooseX;
    private float chooseY;
    private float choosePrecent;

    public BrokenLineView(Context context) {
        super(context);
        this.context = context;
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
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BrokenLineView);

        xAxisColor = typedArray.getColor(R.styleable.BrokenLineView_X_axis_color, context.getResources().getColor(R.color.X_axis_default_color));
        xAxisTextColor = typedArray.getColor(R.styleable.BrokenLineView_X_axis_text_color, context.getResources().getColor(R.color.X_axis_default_text_color));
        xAxisTextSize = typedArray.getDimension(R.styleable.BrokenLineView_X_axis_text_size, DensityUtils.sp2px(10, context));
        xAxisWidth = typedArray.getDimension(R.styleable.BrokenLineView_X_axis_width, 3);

        yAxisColor = typedArray.getColor(R.styleable.BrokenLineView_Y_axis_color, context.getResources().getColor(R.color.X_axis_default_color));
        yAxisTextColor = typedArray.getColor(R.styleable.BrokenLineView_Y_axis_text_color, context.getResources().getColor(R.color.X_axis_default_text_color));
        yAxisTextSize = typedArray.getDimension(R.styleable.BrokenLineView_Y_axis_text_size, DensityUtils.sp2px(10, context));
        yAxisWidth = typedArray.getDimension(R.styleable.BrokenLineView_Y_axis_width, 3);

        brokenLineColor = typedArray.getColor(R.styleable.BrokenLineView_broken_line_color, context.getResources().getColor(R.color.broken_line_default_color));
        brokenLineDotColor = typedArray.getColor(R.styleable.BrokenLineView_broken_line_dot_color, context.getResources().getColor(R.color.broken_line_default_color));
        brokenLineWidth = typedArray.getDimension(R.styleable.BrokenLineView_broken_line_width, 2f);
        brokenLineDotMode = typedArray.getInteger(R.styleable.BrokenLineView_broken_line_dot_mode, DOT_MODE_SOLID);
    }

    private void initView() {
        this.data = new ArrayList<>();
        setXAxis();
        setYAxis();
        setPoint();
        setBrokenLine();
        //        //画坐标点
        //        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //        circlePaint.setAntiAlias(true);
        //        circlePaint.setColor(Color.WHITE);
        //        circlePaint.setStyle(Paint.Style.FILL);
        //        circlePaint.setStrokeWidth(1);
        //坐标点连折线


        //折线图+y轴文字颜色
        effectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        effectPaint.setAntiAlias(true);
        effectPaint.setColor(Color.parseColor("#B3BFD0"));
        effectPaint.setStyle(Paint.Style.FILL);
        effectPaint.setTextSize(xAxisTextSize);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (start) {
            //确定坐标原点
            originPointX = 10;
            originPointY = getMeasuredHeight() - 10;
            //画x轴
            float xStopX = getMeasuredWidth() - 10;
            canvas.drawLine(originPointX, originPointY, xStopX, originPointY, xPaint);
            //x轴箭头
            float[] xArrow = new float[]{xStopX, originPointY, xStopX - 30, originPointY - 10, xStopX, originPointY, xStopX - 30, originPointY + 10};
            canvas.drawLines(xArrow, xPaint);
            //x轴必须等分，所以data中的bean的x一定是x轴总长可以整除的数字
            int pointCount = data.size();
            float xLength = getMeasuredWidth() - 20;
            float xUnitLen = xLength / (float) pointCount;
            //记录每个折点坐标
            float[] points = new float[pointCount * 2];
            //画x轴的标点
            for (int n = 0; n < pointCount; n++) {
                canvas.drawCircle(originPointX + n * xUnitLen, originPointY, 5, xPaint);
            }
            //画y轴
            float yStopY = 10;
            canvas.drawLine(originPointX, originPointY, originPointX, yStopY, yPaint);
            //y轴等分
            float yLength = getMeasuredWidth() - 20;
            float yUnitLen = yLength / (float) 5;
            //画y轴的标点
            for (int n = 0; n < 5; n++) {
                canvas.drawCircle(originPointX, originPointY - n * yUnitLen, 5, yPaint);
            }
            //y轴箭头
            float[] yArrow = new float[]{originPointX, yStopY, originPointX - 10, yStopY + 30, originPointX, yStopY, originPointX + 10, yStopY + 30};
            canvas.drawLines(yArrow, yPaint);
            //画坐标点
            for (int i = 0; i < pointCount; i++) {
                BrokenLineBean bean = data.get(i);
                float circleX;
                if (bean.getX() == 0f) {
                    circleX = 0;
                } else {
                    circleX = xUnitLen * (i + 1);
                }
                float circleY = originPointY - bean.getY() * (yStopY - originPointY);
                canvas.drawCircle(circleX, circleY, 2, pointPaint);
                points[i * 2] = circleX;
                points[(i * 2) + 1] = circleY;
            }
            //连线
            canvas.drawLines(points, brokenLinePaint);


//            //文字的高度
//            Paint.FontMetrics metrics = effectPaint.getFontMetrics();
//            float fontHeight = metrics.bottom - metrics.top;
//            //最大文字宽度
//            float maxTextWidth = effectPaint.measureText("0%");
//            if (null != ysplit && ysplit.length > 0) {
//                for (int i = 0; i < ysplit.length; i++) {
//                    if (maxTextWidth < effectPaint.measureText(ysplit[i] + "%")) {
//                        maxTextWidth = effectPaint.measureText(ysplit[i] + "%");
//                    }
//                }
//            }
//            //x轴
//            yuandianx = DensityUtils.dip2px(margin, context) + maxTextWidth;
//            yuandiany = getMeasuredHeight() - fontHeight - DensityUtils.dip2px(13, context);
//            //            width = getMeasuredWidth() - DensityUtils.dip2px(margin, context);
//            width = getMeasuredWidth() - yuandianx - DensityUtils.dip2px(margin, context);
//            height = getMeasuredHeight() - fontHeight * 2 - DensityUtils.dip2px(13, context);
//
//            float XLineStopX = yuandianx + width;
//            canvas.drawLine(yuandianx, yuandiany, XLineStopX, yuandiany, xPaint);
//            canvas.drawText("0%", maxTextWidth - effectPaint.measureText("0%"), yuandiany, effectPaint);
//
//            if (null != ysplit && ysplit.length > 0) {
//                //百分比x轴
//                gao = height / ysplit.length;
//                for (int i = 0; i < ysplit.length; i++) {
//                    float a = Float.parseFloat(ysplit[i]);
//                    if (max < a) {
//                        max = a;
//                    }
//                    float startY = yuandiany - (i + 1) * gao;
//                    float stopY = yuandiany - (i + 1) * gao;
//                    canvas.drawLine(yuandianx, startY, XLineStopX, stopY, xLinePaint);
//                    float textWidth = effectPaint.measureText(ysplit[i] + "%");
//                    canvas.drawText(ysplit[i] + "%", maxTextWidth - textWidth, stopY, effectPaint);
//                }
//            }
//            long maxX = 24 * 60 * 60;//ms
//            float kuan = width / maxX;
//
//            if (null != data && data.size() > 0) {
//                pointList = new ArrayList<>();
//                //                float scaleX = yuandianx + DensityUtils.dip2px(7, context);
//                float scaleX = yuandianx;
//                for (int i = 0; i < 13; i++) {
//                    //画刻度尺
//                    float x = scaleX + i * (width / 12);
//                    float scaleHeight = DensityUtils.dip2px(4, context);
//                    canvas.drawLine(x, yuandiany, x, yuandiany + scaleHeight, xPaint);
//                    //刻度尺文字
//                    float textX = x - xTextPaint.measureText((i * 2) + "时") / 2;
//                    float textY = yuandiany + fontHeight + DensityUtils.dip2px(5, context);
//                    canvas.drawText((i * 2) + "时", textX, textY, xTextPaint);
//                }
//                for (int i = 0; i < data.size(); i++) {
//
//                    float date = getSeconds(data.get(i).getTime());
//
//                    float precent = Float.parseFloat(data.get(i).getPercent());
//                    float x = scaleX + date * kuan;
//                    float y = yuandiany - height / max * precent;
//                    //画坐标点
//                    //                    canvas.drawCircle(x, y, DensityUtils.dip2px(circleDotRaduis, context), circlePaint);
//                    canvas.drawCircle(x, y, DensityUtils.dip2px(circleDotRaduis / 3, context), pointPaint);
//                    Map<String, Float> map = new HashMap<>();
//                    map.put("x", x);
//                    map.put("y", y);
//                    map.put("precent", precent);
//                    pointList.add(map);
//                    //坐标点连线
//                    if (i > 0) {
//                        float dateL = getSeconds(data.get(i - 1).getTime());
//                        float startX = scaleX + dateL * kuan;
//                        float startY = yuandiany - height / max * Float.parseFloat(data.get(i - 1).getPercent());
//                        canvas.drawLine(startX, startY, x, y, brokenLinePaint);
//                    }
//                }
//                //显示坐标点信息
//                if (isShowPoint && chooseX != 0) {
//
//                    canvas.drawText(choosePrecent + "%", chooseX
//                            + DensityUtils.dip2px(3, context), chooseY - 20, effectPaint);
//                    canvas.drawText(choosePrecent + "%", chooseX
//                            + DensityUtils.dip2px(3, context), chooseY, effectPaint);
//                    canvas.drawLine(chooseX, yuandiany, chooseX, yuandiany - height, xLinePaint);
//                }
//            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //        if (null != pointList && pointList.size() > 0) {
        //            isShowPoint = true;
        //            for (int i = 0; i < pointList.size(); i++) {
        //                Map<String, Float> point = pointList.get(i);
        //                float eventX = event.getX();
        //                float pointX = point.get("x");
        //                float eventY = event.getY();
        //                float pointY = point.get("y");
        //                //                if (event.getX() < pointList.get(i).get("x") + DensityUtils.dip2px(20, context) && event.getY() < pointList.get(i).get("y") + DensityUtils.dip2px(20, context) &&
        //                //                        event.getX() > pointList.get(i).get("x") - DensityUtils.dip2px(20, context) && event.getY() > pointList.get(i).get("y") - DensityUtils.dip2px(20, context)) {
        //                if (eventX < pointX + DensityUtils.dip2px(2, context)
        //                        && eventX > pointX - DensityUtils.dip2px(2, context)
        //                        && eventY < pointY + DensityUtils.dip2px(20, context)
        //                        && eventY > pointY - DensityUtils.dip2px(20, context)) {
        //                    choose = i;
        //                    chooseX = pointX;
        //                    chooseY = pointY;
        //                    choosePrecent = point.get("precent");
        //                    break;
        //                }
        //            }
        //
        //            postInvalidate();
        //        }
        return super.onTouchEvent(event);
    }

    /**
     * 将字符串的毫秒转换为秒。
     *
     * @param msStr
     * @return
     */
    private float getSeconds(String msStr) {
        String hh = DateUtils.timeStamp2Date(Long.parseLong(msStr), "HH");
        String mm = DateUtils.timeStamp2Date(Long.parseLong(msStr), "mm");
        String ss = DateUtils.timeStamp2Date(Long.parseLong(msStr), "ss");
        Float date = Float.parseFloat(hh) * 60 * 60
                + Float.parseFloat(mm) * 60
                + Float.parseFloat(ss);
        return date;
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
        //x坐标轴虚线轴
        xLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xLinePaint.setAntiAlias(true);
        xLinePaint.setColor(xAxisColor);
        xLinePaint.setStyle(Paint.Style.FILL);
        xLinePaint.setStrokeWidth(xAxisWidth);
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
        //x坐标轴虚线轴
        yLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        yLinePaint.setAntiAlias(true);
        yLinePaint.setColor(yAxisColor);
        yLinePaint.setStyle(Paint.Style.FILL);
        yLinePaint.setStrokeWidth(yAxisWidth);
        //x轴文字颜色
        yTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        yTextPaint.setAntiAlias(true);
        yTextPaint.setColor(yAxisTextColor);
        yTextPaint.setStyle(Paint.Style.FILL);
        yTextPaint.setTextSize(yAxisTextSize);
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

    public void startDraw(List<BrokenLineBean> data) {
        start = true;
        this.data = data;
        //        this.ysplit = ysplit;
        initView();
        invalidate();
    }
}