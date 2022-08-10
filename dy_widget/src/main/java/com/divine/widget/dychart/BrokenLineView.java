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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Divine
 * CreateDate: 2020/10/20
 * Describe:
 */
public class BrokenLineView extends View {
    //实心圆点
    public final static int DOT_MODE_SOLID = 0;
    //空心圆点
    public final static int DOT_MODE_HOLLOW = 1;

    private Context context;

    private Paint xPaint;
    private Paint xTextPaint;
    private Paint xLinePaint;

    private Paint pointPaint;
    //    private Paint circlePaint;
    private Paint pointLinePaint;

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

    //外部可设置属性
    private int xAxisColor;
    private int xAxisTextColor;
    private float xAxisTextSize;
    private float xAxisWidth;
    private int yAxisTextColor;
    private float yAxisTextSize;
    private int brokenLineColor;
    private int brokenLineDotColor;
    private float brokenLineWidth;
    private int brokenLineDotMode;

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
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BrokenlineView);

        xAxisColor = typedArray.getColor(R.styleable.BrokenlineView_X_axis_color
                , context.getResources().getColor(R.color.X_axis_default_color));
        xAxisTextColor = typedArray.getColor(R.styleable.BrokenlineView_X_axis_text_color
                , context.getResources().getColor(R.color.X_axis_default_text_color));
        xAxisTextSize = typedArray.getDimension(R.styleable.BrokenlineView_X_axis_text_size, DensityUtils.sp2px(10, context));
        xAxisWidth = typedArray.getDimension(R.styleable.BrokenlineView_X_axis_width, 3);
        yAxisTextColor = typedArray.getColor(R.styleable.BrokenlineView_X_axis_text_color
                , context.getResources().getColor(R.color.X_axis_default_text_color));
        yAxisTextSize = typedArray.getDimension(R.styleable.BrokenlineView_X_axis_text_size, 11);
        brokenLineColor = typedArray.getColor(R.styleable.BrokenlineView_broken_line_color
                , context.getResources().getColor(R.color.broken_line_default_color));
        brokenLineDotColor = typedArray.getColor(R.styleable.BrokenlineView_broken_line_dot_color
                , context.getResources().getColor(R.color.broken_line_default_color));
        brokenLineWidth = typedArray.getDimension(R.styleable.BrokenlineView_broken_line_width, 2f);
        brokenLineDotMode = typedArray.getInteger(R.styleable.BrokenlineView_broken_line_dot_mode, DOT_MODE_SOLID);
    }

    private void initView() {
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
        //坐标点
        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setAntiAlias(true);
        pointPaint.setColor(brokenLineDotColor);
        if (brokenLineDotMode == DOT_MODE_SOLID) {
            pointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        } else if (brokenLineDotMode == DOT_MODE_HOLLOW) {
            pointPaint.setStyle(Paint.Style.STROKE);
        } else {
            pointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        }
        pointPaint.setStrokeWidth(DensityUtils.dip2px(1, context));

        //        //画坐标点
        //        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //        circlePaint.setAntiAlias(true);
        //        circlePaint.setColor(Color.WHITE);
        //        circlePaint.setStyle(Paint.Style.FILL);
        //        circlePaint.setStrokeWidth(1);
        //坐标点连折线
        pointLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointLinePaint.setAntiAlias(true);
        pointLinePaint.setColor(brokenLineColor);
        pointLinePaint.setStrokeWidth(brokenLineWidth);

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
            //文字的高度
            Paint.FontMetrics metrics = effectPaint.getFontMetrics();
            float fontHeight = metrics.bottom - metrics.top;
            //最大文字宽度
            float maxTextWidth = effectPaint.measureText("0%");
            if (null != ysplit && ysplit.length > 0) {
                for (int i = 0; i < ysplit.length; i++) {
                    if (maxTextWidth < effectPaint.measureText(ysplit[i] + "%")) {
                        maxTextWidth = effectPaint.measureText(ysplit[i] + "%");
                    }
                }
            }
            //x轴
            yuandianx = DensityUtils.dip2px(margin, context) + maxTextWidth;
            yuandiany = getMeasuredHeight() - fontHeight - DensityUtils.dip2px(13, context);
            //            width = getMeasuredWidth() - DensityUtils.dip2px(margin, context);
            width = getMeasuredWidth() - yuandianx - DensityUtils.dip2px(margin, context);
            height = getMeasuredHeight() - fontHeight * 2 - DensityUtils.dip2px(13, context);

            float XLineStopX = yuandianx + width;
            canvas.drawLine(yuandianx, yuandiany, XLineStopX, yuandiany, xPaint);
            canvas.drawText("0%", maxTextWidth - effectPaint.measureText("0%"), yuandiany, effectPaint);

            if (null != ysplit && ysplit.length > 0) {
                //百分比x轴
                gao = height / ysplit.length;
                for (int i = 0; i < ysplit.length; i++) {
                    float a = Float.parseFloat(ysplit[i]);
                    if (max < a) {
                        max = a;
                    }
                    float startY = yuandiany - (i + 1) * gao;
                    float stopY = yuandiany - (i + 1) * gao;
                    canvas.drawLine(yuandianx, startY, XLineStopX, stopY, xLinePaint);
                    float textWidth = effectPaint.measureText(ysplit[i] + "%");
                    canvas.drawText(ysplit[i] + "%", maxTextWidth - textWidth, stopY, effectPaint);
                }
            }
            long maxX = 24 * 60 * 60;//ms
            float kuan = width / maxX;

            if (null != data && data.size() > 0) {
                pointList = new ArrayList<>();
                //                float scaleX = yuandianx + DensityUtils.dip2px(7, context);
                float scaleX = yuandianx;
                for (int i = 0; i < 13; i++) {
                    //画刻度尺
                    float x = scaleX + i * (width / 12);
                    float scaleHeight = DensityUtils.dip2px(4, context);
                    canvas.drawLine(x, yuandiany, x, yuandiany + scaleHeight, xPaint);
                    //刻度尺文字
                    float textX = x - xTextPaint.measureText((i * 2) + "时") / 2;
                    float textY = yuandiany + fontHeight + DensityUtils.dip2px(5, context);
                    canvas.drawText((i * 2) + "时", textX, textY, xTextPaint);
                }
                for (int i = 0; i < data.size(); i++) {

                    float date = getSeconds(data.get(i).getTime());

                    float precent = Float.parseFloat(data.get(i).getPercent());
                    float x = scaleX + date * kuan;
                    float y = yuandiany - height / max * precent;
                    //画坐标点
                    //                    canvas.drawCircle(x, y, DensityUtils.dip2px(circleDotRaduis, context), circlePaint);
                    canvas.drawCircle(x, y, DensityUtils.dip2px(circleDotRaduis / 3, context), pointPaint);
                    Map<String, Float> map = new HashMap<>();
                    map.put("x", x);
                    map.put("y", y);
                    map.put("precent", precent);
                    pointList.add(map);
                    //坐标点连线
                    if (i > 0) {
                        float dateL = getSeconds(data.get(i - 1).getTime());
                        float startX = scaleX + dateL * kuan;
                        float startY = yuandiany - height / max * Float.parseFloat(data.get(i - 1).getPercent());
                        canvas.drawLine(startX, startY, x, y, pointLinePaint);
                    }
                }
                //显示坐标点信息
                if (isShowPoint && chooseX != 0) {

                    canvas.drawText(choosePrecent + "%", chooseX
                            + DensityUtils.dip2px(3, context), chooseY - 20, effectPaint);
                    canvas.drawText(choosePrecent + "%", chooseX
                            + DensityUtils.dip2px(3, context), chooseY, effectPaint);
                    canvas.drawLine(chooseX, yuandiany, chooseX, yuandiany - height, xLinePaint);
                }
            }
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

    public void startDraw(List<BrokenLineBean> data) {
        start = true;
        this.data = data;
        //        this.ysplit = ysplit;
        initView();
        invalidate();
    }
}