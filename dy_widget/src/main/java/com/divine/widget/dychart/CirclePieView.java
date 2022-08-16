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
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.divine.R;
import com.divine.dy.lib_utils.sys.DensityUtils;

import java.util.ArrayList;
import java.util.Random;

public class CirclePieView extends View {
    private Context context;
    //圆心在手机中的坐标
    private float centerX, centerY;
    private PointF centerPoint;
    //整圆半径
    private float radius;
    //外部可配属性：扇形信息文字大小
    private float pieTextSize;
    //外部可配属性：扇形信息百分比文字，颜色
    private int pieTextColor;
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
    //触摸点
    private PointF touchPoint;

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
        pieTextSize = typedArray.getDimension(R.styleable.CirclePieView_pie_text_size, 12);
        pieTextColor = typedArray.getColor(R.styleable.CirclePieView_pie_text_color, Color.parseColor("#000000"));
        pieTextPosition = typedArray.getInteger(R.styleable.CirclePieView_pie_text_position, 1);
    }

    private void initView() {
        piePaint = new Paint();
        piePaint.setAntiAlias(true);
        piePaint.setStrokeWidth(1);
        piePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(pieTextColor);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(DensityUtils.dip2px(pieTextSize, context));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setViewBackground(canvas);
        //确定圆点
        float vWidth = getMeasuredWidth();
        float vHeight = getMeasuredHeight();
        centerX = vWidth / 2.0f;
        centerY = vHeight / 2.0f;
        centerPoint = new PointF(centerX, centerY);
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
        radius = (vHeight / 2.0f - padding - DensityUtils.dip2px(pieTextSize, context)) * 4.0f / 5.0f;
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
            //判断点是否在扇形内
            if (null != touchPoint) {
                if (isInPie(lastAngle, angle)) {
                    piePaint.setColor(Color.parseColor("#EF556E"));
                }
            }
            canvas.drawPath(piePath, piePaint);

            //目前已画角度
            float usedAngle = lastAngle + angle;
            //增加文字提示
            //确定文字位置,文字中心点在2/3半径的圆弧上
            PointF textPoint = getTextPoint(lastAngle, angle);
            if (pieTextPosition == 0) {
                drawTextIn(canvas, pieText, bean.getPieTitle(), textPoint);
            }
            //将数据引出圆外展示
            if (pieTextPosition == 1) {
                drawTextOut(canvas, pieText, bean.getPieTitle(), textPoint);
            }
            //记录已画的区域
            lastPoint = getPointInCircle(usedAngle);
            lastAngle = usedAngle;
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchPoint = new PointF();
        touchPoint.x = event.getX();
        touchPoint.y = event.getY();
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
     * 画扇形内的文字
     *
     * @param canvas
     * @param pieText   百分比
     * @param pieTitle  名称
     * @param textPoint 文字中心坐标点
     */
    private void drawTextIn(Canvas canvas, String pieText, String pieTitle, PointF textPoint) {
        //获取文字长度
        float textLen = textPaint.measureText(pieText);
        float titleLen = textPaint.measureText(pieTitle);
        //直接将数据写在圆内
        canvas.drawText(pieText, textPoint.x - textLen / 2, textPoint.y + DensityUtils.dip2px(18, context), textPaint);
        canvas.drawText(pieTitle, textPoint.x - titleLen / 2, textPoint.y, textPaint);
    }

    /**
     * 文字写在扇形之外
     *
     * @param canvas
     * @param pieText   百分比
     * @param pieTitle  名称
     * @param textPoint 文字中心坐标点--扇形内部
     */
    private void drawTextOut(Canvas canvas, String pieText, String pieTitle, PointF textPoint) {
        //获取文字长度
        float textLen = textPaint.measureText(pieText);
        float titleLen = textPaint.measureText(pieTitle);
        //通过计算获取文字写入的目标点 取5/4个半径长度的圆外点为文字起点
        float targetX = Math.abs(textPoint.x - centerX) / (radius * 2.0f / 3.0f) * radius * 5.0f / 4.0f;
        float targetY = Math.abs(textPoint.y - centerY) / (radius * 2.0f / 3.0f) * radius * 5.0f / 4.0f;
        //根据圆内点x的坐标判断最终文字起点是在一四象限还是二三象限
        if (textPoint.x - centerX > 0) {
            targetX = targetX + centerX;
        } else {
            targetX = centerX - targetX;
        }
        //根据圆内点y的坐标判断最终文字起点是在一二象限还是三四象限
        if (textPoint.y - centerY > 0) {
            targetY = targetY + centerY;
        } else {
            targetY = centerY - targetY;
        }
        //以圆内已确定的文字起点延伸到圆外的文字起点，连一条线
        canvas.drawLine(textPoint.x, textPoint.y, targetX, targetY, piePaint);
        //写文字
        //创建一个点，坐标为文字起点
        PointF lineTargetPoint = new PointF(targetX, targetY);
        //获取每一部分文字最长长度
        float maxLen = 0;
        if (textLen > titleLen) {
            maxLen = textLen;
        } else {
            maxLen = titleLen;
        }
        //设置文字颜色
        textPaint.setColor(Color.parseColor("#000000"));
        //以文字起点开始画一条横线，长度为最长文字长度+50px，用来分割扇形名称和扇形百分比
        //写扇形的名称文字，在横线上方并居中
        //写扇形的百分比，在横线的下方
        if (lineTargetPoint.x > centerX) {
            canvas.drawLine(lineTargetPoint.x, lineTargetPoint.y, lineTargetPoint.x + maxLen + 50, lineTargetPoint.y, piePaint);
            canvas.drawText(pieTitle, lineTargetPoint.x + 25, lineTargetPoint.y - DensityUtils.dip2px(5, context), textPaint);
            canvas.drawText(pieText, lineTargetPoint.x + (maxLen - textLen) / 2.0f + 25, lineTargetPoint.y + DensityUtils.dip2px(14, context), textPaint);
        } else if (lineTargetPoint.x == centerX) {
            if (lineTargetPoint.y > centerY) {
                canvas.drawLine(lineTargetPoint.x, lineTargetPoint.y, lineTargetPoint.x - maxLen - 50, lineTargetPoint.y, piePaint);
                canvas.drawText(pieTitle, lineTargetPoint.x - maxLen - 25, lineTargetPoint.y - DensityUtils.dip2px(5, context), textPaint);
                canvas.drawText(pieText, lineTargetPoint.x - (maxLen + textLen) / 2.0f - 25, lineTargetPoint.y + DensityUtils.dip2px(14, context), textPaint);
            } else {
                canvas.drawLine(lineTargetPoint.x, lineTargetPoint.y, lineTargetPoint.x + maxLen + 50, lineTargetPoint.y, piePaint);
                canvas.drawText(pieTitle, lineTargetPoint.x + 25, lineTargetPoint.y - DensityUtils.dip2px(5, context), textPaint);
                canvas.drawText(pieText, lineTargetPoint.x + (maxLen - textLen) / 2.0f + 25, lineTargetPoint.y + DensityUtils.dip2px(14, context), textPaint);
            }
        } else {
            canvas.drawLine(lineTargetPoint.x, lineTargetPoint.y, lineTargetPoint.x - maxLen - 50, lineTargetPoint.y, piePaint);
            canvas.drawText(pieTitle, lineTargetPoint.x - maxLen - 25, lineTargetPoint.y - DensityUtils.dip2px(5, context), textPaint);
            canvas.drawText(pieText, lineTargetPoint.x - (maxLen + textLen) / 2.0f - 25, lineTargetPoint.y + DensityUtils.dip2px(14, context), textPaint);
        }
    }

    /**
     * 获取文字的坐标中心点
     *
     * @param lastAngle 已叠加的的扇形区域的角度
     * @param angle     本次对应的扇形区域的角度
     * @return 文字中心点
     */
    private PointF getTextPoint(float lastAngle, float angle) {
        //取已画出的角度+当前扇形角度的一半用于确定坐标点所在的半径位置
        float halfAngle = lastAngle + angle / 2.0f;
        if (halfAngle >= 270) {
            //No.1
            halfAngle = 360 - halfAngle;
            double halfDegree = halfAngle * Math.PI / 180.0d;
            float halfX = (float) Math.cos(halfDegree) * radius * 2.0f / 3.0f;
            float halfY = (float) Math.sin(halfDegree) * radius * 2.0f / 3.0f;
            float textCenterX = centerPoint.x + halfX;
            float textCenterY = centerPoint.y - halfY;
            PointF textCenterPoint = new PointF(textCenterX, textCenterY);
            return textCenterPoint;
        } else if (halfAngle >= 180) {
            //No.2
            halfAngle = halfAngle - 180;
            double halfDegree = halfAngle * Math.PI / 180.0d;
            float halfX = (float) Math.cos(halfDegree) * radius * 2.0f / 3.0f;
            float halfY = (float) Math.sin(halfDegree) * radius * 2.0f / 3.0f;
            float textCenterX = centerPoint.x - halfX;
            float textCenterY = centerPoint.y - halfY;
            PointF textCenterPoint = new PointF(textCenterX, textCenterY);
            return textCenterPoint;
        } else if (halfAngle >= 90) {
            //No.3
            halfAngle = 180 - halfAngle;
            double halfDegree = halfAngle * Math.PI / 180.0d;
            float halfX = (float) Math.cos(halfDegree) * radius * 2.0f / 3.0f;
            float halfY = (float) Math.sin(halfDegree) * radius * 2.0f / 3.0f;
            float textCenterX = centerPoint.x - halfX;
            float textCenterY = centerPoint.y + halfY;
            PointF textCenterPoint = new PointF(textCenterX, textCenterY);
            return textCenterPoint;
        } else {
            //No.4
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
     * @param angle 角度
     * @return 目标点在手机屏幕上的坐标
     */
    private PointF getPointInCircle(float angle) {
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
     * 判断点击的点是否在扇形之内
     *
     * @param lastAngle 已叠加的的扇形区域的角度
     * @param angle     本次对应的扇形区域的角度
     * @return true为该点在本扇形中
     */
    private boolean isInPie(float lastAngle, float angle) {
        //判断点的象限
        if (touchPoint.x > centerX) {
            if (touchPoint.y <= centerY) {
                //No.1
                float touchXDistance = touchPoint.x - centerX;
                float touchYDistance = centerY - touchPoint.y;
                double touchDistanceToCenter = Math.sqrt(touchXDistance * touchXDistance + touchYDistance * touchYDistance);
                if (touchDistanceToCenter <= radius) {
                    double touchDegree = Math.acos((double) touchXDistance / touchDistanceToCenter);
                    float touchAngle = 360.0f - (float) touchDegree / (float) Math.PI * 180.0f;
                    if (touchAngle > lastAngle && touchAngle <= (lastAngle + angle)) {
                        return true;
                    }
                }
            } else {
                //No.4
                float touchXDistance = touchPoint.x - centerX;
                float touchYDistance = touchPoint.y - centerY;
                double touchDistanceToCenter = Math.sqrt(touchXDistance * touchXDistance + touchYDistance * touchYDistance);
                if (touchDistanceToCenter <= radius) {
                    double touchDegree = Math.acos((double) touchXDistance / touchDistanceToCenter);
                    float touchAngle = (float) touchDegree / (float) Math.PI * 180.0f;
                    if (touchAngle > lastAngle && touchAngle <= (lastAngle + angle)) {
                        return true;
                    }
                }
            }
        } else {
            if (touchPoint.y > centerY) {
                //No.3
                float touchXDistance = centerX - touchPoint.x;
                float touchYDistance = touchPoint.y - centerY;
                double touchDistanceToCenter = Math.sqrt(touchXDistance * touchXDistance + touchYDistance * touchYDistance);
                if (touchDistanceToCenter <= radius) {
                    double touchDegree = Math.acos((double) touchXDistance / touchDistanceToCenter);
                    float touchAngle = 180.0f - (float) touchDegree / (float) Math.PI * 180.0f;
                    if (touchAngle > lastAngle && touchAngle <= (lastAngle + angle)) {
                        return true;
                    }
                }
            } else {
                //No.2
                float touchXDistance = centerX - touchPoint.x;
                float touchYDistance = centerY - touchPoint.y;
                double touchDistanceToCenter = Math.sqrt(touchXDistance * touchXDistance + touchYDistance * touchYDistance);
                if (touchDistanceToCenter <= radius) {
                    double touchDegree = Math.acos((double) touchXDistance / touchDistanceToCenter);
                    float touchAngle = (float) touchDegree / (float) Math.PI * 180.0f + 180.0f;
                    if (touchAngle > lastAngle && touchAngle <= (lastAngle + angle)) {
                        return true;
                    }
                }
            }
        }
        return false;
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
