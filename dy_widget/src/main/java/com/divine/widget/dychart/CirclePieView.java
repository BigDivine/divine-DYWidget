package com.divine.widget.dychart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.divine.R;
import com.divine.dy.lib_utils.sys.DensityUtils;

import java.util.ArrayList;
import java.util.Random;

public class CirclePieView extends View {
    private Context context;
    //外部可配属性：扇形信息名称（类别）文字大小，颜色
    private float pieTitleTextSize, piePercentTextSize;
    //外部可配属性：扇形信息百分比文字大小，颜色
    private int pieTitleTextColor, piePercentTextColor;
    //外部可配属性：扇形信息在内部还是外部
    private int pieTextPosition;
    //不同比例饼画笔（颜色随机）;文字画笔
    private Paint piePaint, textPaint;
    //控件内的padding
    private float padding = 50;
    //扇形统计数据
    private ArrayList<CirclePieBean> data;
    //扇形的颜色
    private ArrayList<Integer> colors;

    public CirclePieView(Context context) {
        super(context);
        this.context = context;
        this.data = new ArrayList<>();
        this.colors = new ArrayList<>();
    }

    public CirclePieView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.data = new ArrayList<>();
        this.colors = new ArrayList<>();
        initAttrs(attrs);
    }

    public CirclePieView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.data = new ArrayList<>();
        this.colors = new ArrayList<>();
        initAttrs(attrs);
    }

    public CirclePieView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        this.data = new ArrayList<>();
        this.colors = new ArrayList<>();
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CirclePieView);
        pieTitleTextSize = typedArray.getDimension(R.styleable.CirclePieView_pie_title_text_size, DensityUtils.dip2px(12, context));
        pieTitleTextColor = typedArray.getColor(R.styleable.CirclePieView_pie_title_text_color, Color.parseColor("#000000"));
        piePercentTextSize = typedArray.getDimension(R.styleable.CirclePieView_pie_percent_title_size, DensityUtils.dip2px(12, context));
        piePercentTextColor = typedArray.getColor(R.styleable.CirclePieView_pie_percent_title_color, Color.parseColor("#000000"));
        pieTextPosition = typedArray.getInteger(R.styleable.CirclePieView_pie_text_position, 1);
    }

    private void initView() {
        piePaint = new Paint();
        piePaint.setAntiAlias(true);
        piePaint.setStrokeWidth(1);
        piePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.parseColor("#ffffff"));
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(DensityUtils.dip2px(12, context));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setViewBackground(canvas);
        //确定圆点
        float vWidth = getMeasuredWidth();
        float vHeight = getMeasuredHeight();
        float centerX = vWidth / 2.0f;
        float centerY = vHeight / 2.0f;
        PointF centerPoint = new PointF(centerX, centerY);
        //确定半径
        int pieCount = data.size();
        float maxTextLen = 0;
        for (CirclePieBean bean : data) {
            int piePercent = bean.getPieNum() * 10;
            String pieText = piePercent + "%";
            float textLen = textPaint.measureText(pieText);
            float titleLen = textPaint.measureText(bean.getPieTitle());
            if (textLen > titleLen) {
                if (textLen > maxTextLen) {
                    maxTextLen = textLen;
                }
            } else {
                if (titleLen > maxTextLen) {
                    maxTextLen = titleLen;
                }
            }
        }
        //半径最大化
        float radius = (vHeight / 2.0f - padding - DensityUtils.dip2px(pieTitleTextSize, context)) * 4.0f / 5.0f;
        if (vWidth < vHeight) {
            radius = vWidth / 2.0f - padding - maxTextLen;
        }
        //分割扇形区域
        float lastAngle = 0f;
        PointF lastPoint = new PointF(centerX + radius, centerY);
        for (int i = 0; i < pieCount; i++) {
            CirclePieBean bean = data.get(i);
            int piePercent = bean.getPieNum() * 10;
            String pieText = piePercent + "%";
            //设置扇形颜色
            if (colors.size() > 0) {
                piePaint.setColor(colors.get(i));
            } else {
                piePaint.setColor(getRandomColor());
            }
            //将数字转为角度
            float angle = ((float) piePercent / 100.0f * 360.0f);
            //创建弧度扇形
            Path piePath = new Path();
            RectF pieRectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
            //连线
            piePath.moveTo(centerX, centerY);
            piePath.lineTo(lastPoint.x, lastPoint.y);
            //连扇形
            piePath.arcTo(pieRectF, lastAngle, angle);
            canvas.drawPath(piePath, piePaint);
            //目前已画角度
            float usedAngle = lastAngle + angle;
            //增加文字提示
            //确定文字位置,文字中心点在2/3半径的圆弧上
            PointF textPoint = getTextPiont(centerPoint, radius, lastAngle, angle);
            if (pieTextPosition == 0) {
                drawTextIn(canvas, pieText, bean.getPieTitle(), textPoint);
            }
            //将数据引出圆外展示
            if (pieTextPosition == 1) {
                drawTextOut(canvas, pieText, bean.getPieTitle(), textPoint, centerPoint, radius);
            }
            lastPoint = getPointInCircle(usedAngle, radius, centerPoint);
            lastAngle = usedAngle;
        }
        super.onDraw(canvas);
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

    private void drawTextIn(Canvas canvas, String pieText, String pieTitle, PointF textPoint) {
        float textLen = textPaint.measureText(pieText);
        float titleLen = textPaint.measureText(pieTitle);
        //直接将数据写在圆内
        canvas.drawText(pieText, textPoint.x - textLen / 2, textPoint.y + DensityUtils.dip2px(18, context), textPaint);
        canvas.drawText(pieTitle, textPoint.x - titleLen / 2, textPoint.y, textPaint);
    }

    private void drawTextOut(Canvas canvas, String pieText, String pieTitle, PointF textPoint, PointF centerPoint, float radius) {
        float textLen = textPaint.measureText(pieText);
        float titleLen = textPaint.measureText(pieTitle);
        float centerX = centerPoint.x;
        float centerY = centerPoint.y;
        float targetX = Math.abs(textPoint.x - centerX) / (radius * 2.0f / 3.0f) * radius * 5.0f / 4.0f;
        float targetY = Math.abs(textPoint.y - centerY) / (radius * 2.0f / 3.0f) * radius * 5.0f / 4.0f;
        if (textPoint.x - centerX > 0) {
            targetX = targetX + centerX;
        } else {
            targetX = centerX - targetX;
        }
        if (textPoint.y - centerY > 0) {
            targetY = targetY + centerY;
        } else {
            targetY = centerY - targetY;
        }
        canvas.drawLine(textPoint.x, textPoint.y, targetX, targetY, piePaint);
        PointF lineTargetPoint = new PointF(targetX, targetY);
        float maxLen = 0;
        if (textLen > titleLen) {
            maxLen = textLen;
        } else {
            maxLen = titleLen;
        }
        textPaint.setColor(Color.parseColor("#000000"));
        if (lineTargetPoint.x > centerX) {
            canvas.drawLine(lineTargetPoint.x, lineTargetPoint.y, lineTargetPoint.x + maxLen + 50, lineTargetPoint.y, piePaint);
            canvas.drawText(pieTitle, lineTargetPoint.x + 25, lineTargetPoint.y - DensityUtils.dip2px(5, context), textPaint);
            canvas.drawText(pieText, lineTargetPoint.x + (maxLen - textLen) / 2.0f + 25, lineTargetPoint.y + DensityUtils.dip2px(14, context), textPaint);
        } else {
            canvas.drawLine(lineTargetPoint.x, lineTargetPoint.y, lineTargetPoint.x - maxLen - 50, lineTargetPoint.y, piePaint);
            canvas.drawText(pieTitle, lineTargetPoint.x - maxLen - 25, lineTargetPoint.y - DensityUtils.dip2px(5, context), textPaint);
            canvas.drawText(pieText, lineTargetPoint.x - (maxLen + textLen) / 2.0f - 25, lineTargetPoint.y + DensityUtils.dip2px(14, context), textPaint);
        }
    }

    private PointF getTextPiont(PointF centerPoint, float radius, float lastAngle, float angle) {
        float halfAngle = lastAngle + angle / 2.0f;
        if (halfAngle >= 270) {
            //显示的不对
            halfAngle = 360 - halfAngle;
            double halfDegree = halfAngle * Math.PI / 180.0d;
            float halfX = (float) Math.cos(halfDegree) * radius * 2.0f / 3.0f;
            float halfY = (float) Math.sin(halfDegree) * radius * 2.0f / 3.0f;
            float textCenterX = centerPoint.x + halfX;
            float textCenterY = centerPoint.y - halfY;
            PointF textCenterPoint = new PointF(textCenterX, textCenterY);
            return textCenterPoint;
        } else if (halfAngle >= 180) {
            halfAngle = halfAngle - 180;
            double halfDegree = halfAngle * Math.PI / 180.0d;
            float halfX = (float) Math.cos(halfDegree) * radius * 2.0f / 3.0f;
            float halfY = (float) Math.sin(halfDegree) * radius * 2.0f / 3.0f;
            float textCenterX = centerPoint.x - halfX;
            float textCenterY = centerPoint.y - halfY;
            PointF textCenterPoint = new PointF(textCenterX, textCenterY);
            return textCenterPoint;
        } else if (halfAngle >= 90) {
            halfAngle = 180 - halfAngle;
            double halfDegree = halfAngle * Math.PI / 180.0d;
            float halfX = (float) Math.cos(halfDegree) * radius * 2.0f / 3.0f;
            float halfY = (float) Math.sin(halfDegree) * radius * 2.0f / 3.0f;
            float textCenterX = centerPoint.x - halfX;
            float textCenterY = centerPoint.y + halfY;
            PointF textCenterPoint = new PointF(textCenterX, textCenterY);
            return textCenterPoint;
        } else {
            double halfDegree = halfAngle * Math.PI / 180.0d;
            float halfX = (float) Math.cos(halfDegree) * radius * 2.0f / 3.0f;
            float halfY = (float) Math.sin(halfDegree) * radius * 2.0f / 3.0f;
            float textCenterX = centerPoint.x + halfX;
            float textCenterY = centerPoint.y + halfY;
            PointF textCenterPoint = new PointF(textCenterX, textCenterY);
            return textCenterPoint;
        }
    }

    /**
     * 获取角度对应的在圆上的点坐标
     *
     * @param angle       角度
     * @param radius      半径
     * @param centerPoint 原点坐标
     * @return 目标点在手机屏幕上的坐标
     */
    private PointF getPointInCircle(float angle, double radius, PointF centerPoint) {
        //判断点的象限
        if (angle > 0 && angle <= 90) {
            //No.4
            double degree = (double) angle * Math.PI / 180.0d;
            double x = Math.cos(degree) * radius;
            double y = Math.sin(degree) * radius;
            float targetX = centerPoint.x + (float) x;
            float targetY = centerPoint.y + (float) y;
            return new PointF(targetX, targetY);
        } else if (angle <= 180) {
            //No.3
            double degree = (double) (180.0f - angle) * Math.PI / 180.0d;
            double x = Math.cos(degree) * radius;
            double y = Math.sin(degree) * radius;
            float targetX = centerPoint.x - (float) x;
            float targetY = centerPoint.y + (float) y;
            return new PointF(targetX, targetY);
        } else if (angle <= 270) {
            //No.2
            double degree = (double) (angle - 180.0f) * Math.PI / 180.0d;
            double x = Math.cos(degree) * radius;
            double y = Math.sin(degree) * radius;
            float targetX = centerPoint.x - (float) x;
            float targetY = centerPoint.y - (float) y;
            return new PointF(targetX, targetY);
        } else {
            //No.1
            double degree = (double) (360.0f - angle) * Math.PI / 180.0d;
            double x = Math.cos(degree) * radius;
            double y = Math.sin(degree) * radius;
            float targetX = centerPoint.x + (float) x;
            float targetY = centerPoint.y - (float) y;
            return new PointF(targetX, targetY);
        }
    }

    /**
     * 获取随机颜色
     *
     * @return 颜色rgb
     */
    private int getRandomColor() {
        int red = new Random().nextInt(255);
        int green = new Random().nextInt(255);
        int blue = new Random().nextInt(255);
        return Color.rgb(red, green, blue);
    }

    public void startDraw(ArrayList<CirclePieBean> data) {
        this.data = data;
        initView();
        invalidate();
    }

    public void startDraw(ArrayList<CirclePieBean> data, ArrayList<Integer> colors) {
        this.data = data;
        this.colors = colors;
        initView();
        invalidate();
    }
}
