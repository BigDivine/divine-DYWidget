package com.divine.widget.old;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


import com.divine.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Divine
 * CreateDate: 2020/10/20
 * Describe:
 */
public class ColorBarView extends View {

    private int functionColor;

    private int idlingColor;

    private int restColor;

    private int height;

    private Paint functionPaint;

    private Paint idlingPaint;

    private Paint restPaint;

    private int width;

    private List<WorkSituationModel.WorkRecordListBean> colorBarViews = new ArrayList<>();

    private int unitLength;

    public ColorBarView(Context context) {
        super(context,null);
    }

    public ColorBarView(Context context, AttributeSet attrs) {
        super(context, attrs,0);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorBar);
        functionColor = typedArray.getColor(R.styleable.ColorBar_function, getResources().getColor(R.color.barGreen));
        idlingColor = typedArray.getColor(R.styleable.ColorBar_idling,getResources().getColor(R.color.barOrange));
        restColor = typedArray.getColor(R.styleable.ColorBar_rest,getResources().getColor(R.color.barGray));
        height = typedArray.getDimensionPixelOffset(R.styleable.ColorBar_colorHeight, 0);

        functionPaint = new Paint();
        functionPaint.setColor(functionColor);
        functionPaint.setStyle(Paint.Style.FILL);

        idlingPaint = new Paint();
        idlingPaint.setColor(idlingColor);
        idlingPaint.setStyle(Paint.Style.FILL);

        restPaint = new Paint();
        restPaint.setColor(restColor);
        restPaint.setStyle(Paint.Style.FILL);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        unitLength = width/24;
    }

    public void setColorList(List<WorkSituationModel.WorkRecordListBean> colorBarViews){
        this.colorBarViews.clear();
        this.colorBarViews.addAll(colorBarViews);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i=0;i<colorBarViews.size();i++){
            RectF rectF = new RectF((float) colorBarViews.get(i).getRangeStart()*unitLength, 0, (float)colorBarViews.get(i).getRangeEnd()*unitLength, height);
            if(colorBarViews.get(i).getState().equals("working")) {
                canvas.drawRect(rectF, functionPaint);
            }else if(colorBarViews.get(i).getState().equals("idle")){
                canvas.drawRect(rectF, idlingPaint);
            }
        }
    }
}
class WorkSituationModel {

    private String idleTimeSum;
    private String offTimeSum;
    private String openTimeSum;
    private String workTimeSum;
    private List<WorkRecordResultsBean> workRecordResults;

    public String getIdleTimeSum() {
        return idleTimeSum;
    }

    public void setIdleTimeSum(String idleTimeSum) {
        this.idleTimeSum = idleTimeSum;
    }

    public String getOffTimeSum() {
        return offTimeSum;
    }

    public void setOffTimeSum(String offTimeSum) {
        this.offTimeSum = offTimeSum;
    }

    @Override
    public String toString() {
        return "WorkSituationModel{" +
                "idleTimeSum=" + idleTimeSum +
                ", offTimeSum=" + offTimeSum +
                ", openTimeSum=" + openTimeSum +
                ", workTimeSum=" + workTimeSum +
                ", workRecordResults=" + workRecordResults +
                '}';
    }

    public String getOpenTimeSum() {
        return openTimeSum;
    }

    public void setOpenTimeSum(String openTimeSum) {
        this.openTimeSum = openTimeSum;
    }

    public String getWorkTimeSum() {
        return workTimeSum;
    }

    public void setWorkTimeSum(String workTimeSum) {
        this.workTimeSum = workTimeSum;
    }

    public List<WorkRecordResultsBean> getWorkRecordResults() {
        return workRecordResults;
    }

    public void setWorkRecordResults(List<WorkRecordResultsBean> workRecordResults) {
        this.workRecordResults = workRecordResults;
    }

    public static class WorkRecordResultsBean {
        /**
         * pageBean : null
         * id : null
         * machineKey : null
         * startTime : null
         * endTime : null
         * duration : null
         * state : null
         * createDate : null
         * date : null
         * rangeStart : null
         * rangeEnd : null
         * workTime : 0
         * idleTime : 0
         * offTime : 24
         * days : 2019-01-01
         * workRecordList : []
         */

        private Object pageBean;
        private Object id;
        private Object machineKey;
        private Object startTime;
        private Object endTime;
        private Object duration;
        private Object state;
        private Object createDate;
        private Object date;
        private Object rangeStart;
        private Object rangeEnd;
        private String workTime;
        private String idleTime;
        private String offTime;
        private String days;
        private List<WorkRecordListBean> workRecordList;

        public Object getPageBean() {
            return pageBean;
        }

        public void setPageBean(Object pageBean) {
            this.pageBean = pageBean;
        }

        public Object getId() {
            return id;
        }

        public void setId(Object id) {
            this.id = id;
        }

        public Object getMachineKey() {
            return machineKey;
        }

        public void setMachineKey(Object machineKey) {
            this.machineKey = machineKey;
        }

        public Object getStartTime() {
            return startTime;
        }

        public void setStartTime(Object startTime) {
            this.startTime = startTime;
        }

        public Object getEndTime() {
            return endTime;
        }

        public void setEndTime(Object endTime) {
            this.endTime = endTime;
        }

        public Object getDuration() {
            return duration;
        }

        public void setDuration(Object duration) {
            this.duration = duration;
        }

        public Object getState() {
            return state;
        }

        public void setState(Object state) {
            this.state = state;
        }

        public Object getCreateDate() {
            return createDate;
        }

        public void setCreateDate(Object createDate) {
            this.createDate = createDate;
        }

        public Object getDate() {
            return date;
        }

        public void setDate(Object date) {
            this.date = date;
        }

        public Object getRangeStart() {
            return rangeStart;
        }

        public void setRangeStart(Object rangeStart) {
            this.rangeStart = rangeStart;
        }

        public Object getRangeEnd() {
            return rangeEnd;
        }

        public void setRangeEnd(Object rangeEnd) {
            this.rangeEnd = rangeEnd;
        }

        public String getWorkTime() {
            return workTime;
        }

        public void setWorkTime(String workTime) {
            this.workTime = workTime;
        }

        public String getIdleTime() {
            return idleTime;
        }

        public void setIdleTime(String idleTime) {
            this.idleTime = idleTime;
        }

        public String getOffTime() {
            return offTime;
        }

        public void setOffTime(String offTime) {
            this.offTime = offTime;
        }

        public String getDays() {
            return days;
        }

        public void setDays(String days) {
            this.days = days;
        }

        public List<WorkRecordListBean> getWorkRecordList() {
            return workRecordList;
        }

        public void setWorkRecordList(List<WorkRecordListBean> workRecordList) {
            this.workRecordList = workRecordList;
        }
    }

    //    private List<ColorBarbean> colorBarbeans;
    //
    //    public List<ColorBarbean> getColorBarbeans() {
    //        return colorBarbeans;
    //    }
    //
    //    public void setColorBarbeans(List<ColorBarbean> colorBarbeans) {
    //        this.colorBarbeans = colorBarbeans;
    //    }

    public static class WorkRecordListBean {

        private String pageBean;
        private String id;
        private String machineKey;
        private String startTime;
        private String endTime;
        private String duration;
        private String state;
        private String createDate;
        private String date;
        private double rangeStart;
        private double rangeEnd;
        private String workTime;
        private String idleTime;
        private String offTime;
        private String days;
        private String workRecordList;

        public String getPageBean() {
            return pageBean;
        }

        public void setPageBean(String pageBean) {
            this.pageBean = pageBean;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMachineKey() {
            return machineKey;
        }

        public void setMachineKey(String machineKey) {
            this.machineKey = machineKey;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public double getRangeStart() {
            return rangeStart;
        }

        public void setRangeStart(double rangeStart) {
            this.rangeStart = rangeStart;
        }

        public double getRangeEnd() {
            return rangeEnd;
        }

        public void setRangeEnd(double rangeEnd) {
            this.rangeEnd = rangeEnd;
        }

        public String getWorkTime() {
            return workTime;
        }

        public void setWorkTime(String workTime) {
            this.workTime = workTime;
        }

        public String getIdleTime() {
            return idleTime;
        }

        public void setIdleTime(String idleTime) {
            this.idleTime = idleTime;
        }

        public String getOffTime() {
            return offTime;
        }

        public void setOffTime(String offTime) {
            this.offTime = offTime;
        }

        public String getDays() {
            return days;
        }

        public void setDays(String days) {
            this.days = days;
        }

        public String getWorkRecordList() {
            return workRecordList;
        }

        public void setWorkRecordList(String workRecordList) {
            this.workRecordList = workRecordList;
        }
    }

}
