package com.divine.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.divine.R;

/**
 * toast 封装类
 * <p>
 * by yzl
 * 2018-11-19
 * <p>
 * 保证只有一个toast，可以覆盖上一个toast显示
 */
public class ToastUtils {


    private static Toast toast;
    private static Toast imgToast;

    /**
     * 初始化Toast(消息，时间)
     */
    private static Toast initToast(Context mContext, CharSequence message, int duration) {
        if (toast == null) {
            toast = Toast.makeText(mContext, message, duration);
        } else {
            //设置文字
            toast.setText(message);
            //设置存续期间
            toast.setDuration(duration);
        }
        return toast;
    }

    /**
     * 短时间显示Toast(消息 String等)
     */
    public static void showShort(Context mContext, CharSequence message) {
        initToast(mContext, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * 短时间显示Toast（资源id)
     */
    public static void showShort(Context mContext, int strResId) {
        initToast(mContext, mContext.getResources().getText(strResId), Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示Toast(消息 String等)
     */
    public static void showLong(Context mContext, CharSequence message) {
        initToast(mContext, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 长时间显示Toast（资源id)
     */
    public static void showLong(Context mContext, int strResId) {
        initToast(mContext, mContext.getResources().getText(strResId), Toast.LENGTH_LONG).show();
    }

    /**
     * 自定义显示Toast时间(消息 String等，时间)
     */
    public static void show(Context mContext, CharSequence message, int duration) {
        initToast(mContext, message, duration).show();
    }

    /**
     * 自定义显示Toast时间(消息 资源id，时间)
     */
    public static void show(Context mContext, int strResId, int duration) {
        initToast(mContext, mContext.getResources().getText(strResId), duration).show();
    }

    /**
     * 显示有image的toast 这是个view
     */
    public static Toast showToastWithImg(Context mContext, String tvStr, int imageResource) {
        if (imgToast == null) {
            imgToast = new Toast(mContext);
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.toast_img_layout, null);
        TextView tv = view.findViewById(R.id.toast_custom_tv);
        tv.setText(TextUtils.isEmpty(tvStr) ? "" : tvStr);

        Drawable iconDrawable = mContext.getResources().getDrawable(imageResource);
        iconDrawable.setBounds(0, 0, iconDrawable.getMinimumWidth(), iconDrawable.getMinimumHeight());
        if (imageResource > 0) {
            tv.setCompoundDrawables(null, iconDrawable, null, null);
        } else {
            tv.setCompoundDrawables(null, null, null, null);
        }
        imgToast.setView(view);
        imgToast.setGravity(Gravity.CENTER, 0, 0);
        imgToast.show();
        return imgToast;

    }
}
