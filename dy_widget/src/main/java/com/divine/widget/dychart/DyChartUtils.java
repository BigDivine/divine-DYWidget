package com.divine.widget.dychart;

import android.graphics.Color;

import java.util.Random;

public class DyChartUtils {
    /**
     * 获取随机颜色
     *
     * @return 颜色rgb
     */
    public static int getRandomColor() {
        int red = new Random().nextInt(255);
        int green = new Random().nextInt(255);
        int blue = new Random().nextInt(255);
        return Color.rgb(red, green, blue);
    }
}
