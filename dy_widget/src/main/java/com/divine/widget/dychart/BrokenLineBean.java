package com.divine.widget.dychart;

public class BrokenLineBean {
    private String time;
    private String percent;

    public BrokenLineBean(String time, String percent) {
        this.time = time;
        this.percent = percent;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    @Override
    public String toString() {
        return "BrokenLineBean{" +
                "time='" + time + '\'' +
                ", percent='" + percent + '\'' +
                '}';
    }
}
