package com.divine.widget.dychart;

public class CirclePieBean {
    private String pieTitle;
    private int pieNum;

    public CirclePieBean(String pieTitle, int pieNum) {
        this.pieTitle = pieTitle;
        this.pieNum = pieNum;
    }

    public String getPieTitle() {
        return pieTitle;
    }

    public void setPieTitle(String pieTitle) {
        this.pieTitle = pieTitle;
    }

    public int getPieNum() {
        return pieNum;
    }

    public void setPieNum(int pieNum) {
        this.pieNum = pieNum;
    }
}
