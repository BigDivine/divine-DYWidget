package com.divine.widget.dychart;

public class PillarBean {
    //x为x轴的节点标记，取值：[0,1,2,3,4,5...]，表示第一个节点，第二个节点。。。
    //y为y轴的节点标记，取值为百分比，[0.1f,0.2f,0.5f.....1f],表示节点y轴的位置
    private int x;
    private float y;

    public PillarBean(int x, float y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "BrokenLineBean{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
