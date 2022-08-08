package com.divine.widget.old;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.divine.base.utils.sys.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Divine
 * CreateDate: 2020/10/20
 * Describe:
 */
public class HistogramView extends View {
    private Context context;
    private Paint XPaint;

    private Paint YPaint;
    private Paint whitePaint;

    private Paint pointPaint;

    private Paint circlePaint;

    private Paint shapePaint;

    private Paint effectPaint, effectPaint1;

    private Paint linePaint;

    private float yuandianx, yuandiany, height, width;
    private String[] ysplit;
    private float max;
    private int textSize = 8;
    private int margin = 20;
    private float gao;
    private boolean start = false;
    private Map<String, String> mapx;//数据集合

    private List<Map<String, Float>> pointList;//选中柱状图顶部字体的列表
    private List<Map<String, Float>> circleList;//选中月的列表

    public boolean isShowCircle = false;//记录是否点击了月
    public boolean isOnclick = false;//控制月按钮点击事件的回调,true:回调；false：不回调

    private int choose;//所选中的索引
    private float chooseX;//选中柱状图顶部字体的x坐标
    private float chooseY;//选中柱状图顶部字体y坐标
    private float circleX;//选中月的x坐标
    private float circleY;//选中月的有坐标

    private Paint xLinePaint;

    private circleOnClick circleOnClick;

    public HistogramView(Context context) {
        super(context);
        this.context = context;
    }

    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public HistogramView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setCircleOnClick(HistogramView.circleOnClick circleOnClick) {
        this.circleOnClick = circleOnClick;
    }

    private void initView() {
        XPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        XPaint.setAntiAlias(true);
        XPaint.setColor(Color.parseColor("#1e90ff"));
        XPaint.setStrokeWidth(dp2px(1));

        YPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        YPaint.setAntiAlias(true);
        YPaint.setColor(Color.parseColor("#D8D8D8"));
        YPaint.setStrokeWidth(dp2px(1));

        whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whitePaint.setAntiAlias(true);
        whitePaint.setColor(Color.WHITE);
        whitePaint.setStrokeWidth(dp2px(1));
        whitePaint.setTextSize(sp2px(textSize));
        whitePaint.setStyle(Paint.Style.FILL);

        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setAntiAlias(true);
        pointPaint.setColor(Color.parseColor("#2F80F6"));
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setStrokeWidth(dp2px(1));

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.parseColor("#2F80F6"));
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(1);


        shapePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shapePaint.setAntiAlias(true);
        shapePaint.setColor(Color.GREEN);
        shapePaint.setStyle(Paint.Style.FILL);
        shapePaint.setStrokeWidth(1);

        //x坐标轴虚线轴
        xLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xLinePaint.setAntiAlias(true);
        xLinePaint.setColor(Color.parseColor("#B3BFD0"));
        xLinePaint.setStyle(Paint.Style.FILL);
        xLinePaint.setStrokeWidth(1);
        xLinePaint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));


        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.GRAY);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setStrokeWidth(1);

        effectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        effectPaint.setAntiAlias(true);
        effectPaint.setColor(Color.parseColor("#2F80F6"));
        effectPaint.setStyle(Paint.Style.FILL);
        effectPaint.setStrokeWidth(1);
        effectPaint.setTextSize(sp2px(textSize));
        effectPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        effectPaint1.setAntiAlias(true);
        effectPaint1.setColor(Color.parseColor("#2F80F6"));
        effectPaint1.setStyle(Paint.Style.FILL);
        effectPaint1.setStrokeWidth(1);
        effectPaint1.setTextSize(sp2px(textSize));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (start) {
            float maxTextWidth = effectPaint.measureText("0");
            if (null != ysplit && ysplit.length > 0) {
                for (int i = 0; i < ysplit.length; i++) {
                    if (maxTextWidth < effectPaint.measureText(ysplit[i])) {
                        maxTextWidth = effectPaint.measureText(ysplit[i]);
                    }
                }
            }
            circleList = new ArrayList<>();
            pointList = new ArrayList<>();
            yuandianx = maxTextWidth;
            yuandiany = getMeasuredHeight() - dp2px(margin) + sp2px(textSize);
            width = getMeasuredWidth() - dp2px(margin);
            height = getMeasuredHeight() - dp2px(margin);
            canvas.drawLine(yuandianx, yuandiany, yuandianx + width, yuandiany, YPaint);
            canvas.drawText("0", maxTextWidth - effectPaint.measureText("0"), yuandiany + sp2px(textSize / 2), effectPaint);

            if (null != ysplit && ysplit.length > 0) {
                gao = height / ysplit.length;
                for (int i = 0; i < ysplit.length; i++) {
                    float a = Float.parseFloat(ysplit[i]);
                    if (max < a) {
                        max = a;
                    }
                    float startX = yuandianx;
                    float startY = yuandiany - (i + 1) * gao;
                    float stopX = yuandianx + width;
                    float stopY = yuandiany - (i + 1) * gao;
                    canvas.drawLine(startX, startY, stopX, stopY, xLinePaint);
                    float textWidth = effectPaint.measureText(ysplit[i]);
                    canvas.drawText(ysplit[i], maxTextWidth - textWidth, stopY + sp2px(textSize / 2), effectPaint);
                }
            }
            if (mapx.size() > 0) {
                float kuan = width / (mapx.size() + 1);
                final Object[] o = mapx.keySet().toArray();
                for (int i = 0; i < o.length; i++) {
                    String s = o[i].toString();
                    float x = yuandianx + (i + 1) * kuan;
                    float y = yuandiany + sp2px(textSize);
                    canvas.drawLine(x, yuandiany, x, yuandiany + dp2px(3), YPaint);

                    if (isShowCircle) {//点击的情况下执行
                        if (i == choose) {//如果是点击选中的月则设置月的背景为蓝色
                            canvas.drawCircle(circleX, circleY, dp2px(8), pointPaint);
                            canvas.drawText(o[choose].toString(), circleX - ((sp2px(textSize) * o[choose].toString().length()) * 0.28f), circleY + dp2px(3), whitePaint);
                            if (isOnclick) {//防止重负调用点击回调，造成死循环，（死循环的情况会一直访问接口）
                                circleOnClick.circleOnClick(o[choose].toString());
                            }
                            isOnclick = false;
                        } else {//绘制正常的白色背景
                            canvas.drawCircle(x, y, dp2px(8), circlePaint);
                            canvas.drawText(s, x - ((sp2px(textSize) * s.length()) * 0.28f), y + dp2px(3), effectPaint);
                        }
                    } else {//未点击，首次绘制的情况下执行
                        if (s.equals(DateUtils.getMonth() + "")) {//当前月的背景绘制蓝色背景
                            canvas.drawCircle(x, y, dp2px(8), pointPaint);
                            canvas.drawText(s, x - ((sp2px(textSize) * s.length()) * 0.28f), y + dp2px(3), whitePaint);
                        } else {
                            canvas.drawCircle(x, y, dp2px(8), circlePaint);
                            canvas.drawText(s, x - ((sp2px(textSize) * s.length()) * 0.28f), y + dp2px(3), effectPaint);
                        }
                    }

                    Map<String, Float> map = new HashMap<>();
                    map.put("x", x);
                    map.put("y", y);
                    circleList.add(map);//记录月的x和y坐标
                }

                for (int i = 0; i < o.length; i++) {//绘制柱状图
                    float x = yuandianx + (i + 1) * kuan;
                    float y = yuandiany - height / max * Float.parseFloat(mapx.get(o[i]));
                    canvas.drawRect(x - dp2px(5), y, x + dp2px(5), yuandiany, pointPaint);

                    Map<String, Float> map = new HashMap<>();
                    map.put("x", x);
                    map.put("y", y);
                    map.put("left", x - dp2px(5));
                    map.put("top", y);
                    map.put("right", x + dp2px(5));
                    map.put("bottom", yuandiany);
                    pointList.add(map);//记录柱状图的坐标
                }

                if (isShowCircle) {//点击情况下直接获取到所点击的按钮，进行柱状图顶部字体的绘制
                    String text = mapx.get(o[choose]);
                    canvas.drawText(text, chooseX - text.length() * sp2px(textSize / 4), chooseY - dp2px(3), effectPaint1);
                } else {//未点击，首次进入进行绘制时，默认当前月的柱状图顶部字体显示
                    for (int i = 0; i < o.length; i++) {
                        if (o[i].toString().equals(DateUtils.getMonth() + "")) {
                            float x = yuandianx + (i + 1) * kuan;
                            float y = yuandiany - height / max * Float.parseFloat(mapx.get(o[i]));
                            String text = mapx.get(o[i]);
                            canvas.drawText(text, x - text.length() * sp2px(textSize / 4), y - dp2px(3), effectPaint1);
                        }
                    }
                }
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < circleList.size(); i++) {
                    if ((event.getX() < circleList.get(i).get("x") + dp2px(10) && event.getX() > circleList.get(i).get("x") - dp2px(10))) {
                        isShowCircle = true;
                        circleX = circleList.get(i).get("x");//记录点击月的x坐标
                        circleY = circleList.get(i).get("y");//记录所点击月的y坐标
                        choose = i;
                        chooseX = pointList.get(i).get("x");//记录柱状图顶部字体的x坐标
                        chooseY = pointList.get(i).get("y");//记录柱状图顶部字体的y坐标
                        isOnclick = true;
                    }
                }
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    private Map<String, Float> mapAll;//留存一份所有的数据

    public void startDraw(Map<String, String> mapx, String[] ysplit, String unit, int margin, int textSize) {
        start = true;
//        Object o[] = mapx.keySet().toArray();
//        for (int i = 0; i < 7; i++) {
//            this.mapx.put(o[i].toString(),mapx.get(o[i]));
//        }
        this.mapx = mapx;
        this.ysplit = ysplit;
        this.textSize = textSize;
        this.margin = margin;
        initView();
        invalidate();
    }

    /**
     * sp转换成px
     */
    private int sp2px(float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    /**
     * dp转换成px
     */
    private int dp2px(float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public interface circleOnClick {
        void circleOnClick(String date);
    }
}
