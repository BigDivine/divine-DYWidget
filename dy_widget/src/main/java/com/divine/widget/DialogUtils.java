package com.divine.widget;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.divine.R;


/**
 * author: Divine
 * <p>
 * date: 2018/12/4
 */
public class DialogUtils {


    private static Dialog dialog;

    /**
     * 日期选择dialog
     *
     * @param mContext
     * @param dateSetListener 时间选择监听
     * @param year            初始年
     * @param month           初始月
     * @param dayOfMonth      初始日
     */
    public static void showDatePickerDialog(Context mContext
            , DatePickerDialog.OnDateSetListener dateSetListener
            , int year
            , int month
            , int dayOfMonth) {

        DatePickerDialog mDatePickerDialog = new DatePickerDialog(mContext,
                                                                  DatePickerDialog.THEME_HOLO_LIGHT,
                                                                  dateSetListener,
                                                                  year,
                                                                  month,
                                                                  dayOfMonth);
        mDatePickerDialog.show();
    }

    /**
     * 日期选择dialog
     *
     * @param mContext
     * @param dateSetListener 时间选择监听
     * @param year            初始年
     * @param month           初始月
     * @param dayOfMonth      初始日
     * @param minDate         最小日期
     * @param maxDate         最大日期
     */
    public static void showDatePickerDialog(Context mContext
            , DatePickerDialog.OnDateSetListener dateSetListener
            , int year
            , int month
            , int dayOfMonth
            , long minDate
            , long maxDate) {

        DatePickerDialog mDatePickerDialog = new DatePickerDialog(mContext,
                                                                  DatePickerDialog.THEME_HOLO_LIGHT,
                                                                  dateSetListener,
                                                                  year,
                                                                  month,
                                                                  dayOfMonth);
        DatePicker datePicker = mDatePickerDialog.getDatePicker();
        if (minDate != 0) {
            datePicker.setMinDate(minDate);
        }
        datePicker.setMaxDate(maxDate);
        mDatePickerDialog.show();
    }

    /**
     * 时间选择器
     *
     * @param mContext
     * @param dateSetListener 时间选择完成监听
     * @param hourOfDay       初始小时
     * @param minute          初始分钟
     */
    public static void showTimePickerDialog(Context mContext, TimePickerDialog.OnTimeSetListener dateSetListener, int hourOfDay, int minute) {

        TimePickerDialog mDatePickerDialog = new TimePickerDialog(mContext,
                                                                  dateSetListener,
                                                                  hourOfDay,
                                                                  minute,
                                                                  false);
        mDatePickerDialog.show();
    }


    // 确定提示框
    public static void showConfirmDialog(Context context, String title, String message, DialogInterface.OnClickListener confirm) {
        showConfirmDialog(context, title, message, "确定", confirm);
    }

    // 确定提示框
    public static void showConfirmDialog(Context context, String title, String message, String positiveText, DialogInterface.OnClickListener confirm) {
        showConfirmDialog(context, title, message, positiveText, confirm, "取消",
                          (dialog, which) -> {
                              dialog.dismiss();
                          }
        );
    }

    /**
     * 确定提示框
     *
     * @param context
     * @param title
     * @param message
     * @param positiveText
     * @param confirm
     * @param negativeText
     * @param cancel
     */
    public static void showConfirmDialog(Context context, String title, String message, String positiveText, DialogInterface.OnClickListener confirm, String negativeText, DialogInterface.OnClickListener cancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(positiveText, confirm)
                .setNegativeButton(negativeText, cancel)
                .setCancelable(false);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.create().show();
    }

    public interface ExplainResult {
        void explainMessage(String message);
    }

    /**
     * 带输入框的弹框
     *
     * @param context
     * @param explainResult 接口回调，点击确定后，对输入框内的内容进行处理
     */
    public static void showExplainDialog(Context context, final ExplainResult explainResult) {
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_explain_layout, null);

        TextView explain_cancel = inflate.findViewById(R.id.explain_cancel);
        TextView explain_confirm = inflate.findViewById(R.id.explain_confirm);
        final EditText explain_content = inflate.findViewById(R.id.explain_content);
        explain_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        explain_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                explainResult.explainMessage(explain_content.getText().toString());
                dialog.dismiss();
            }
        });
        dialog.setContentView(inflate);
        Window window = dialog.getWindow();
        if (dialog != null && window != null) {
            window.getDecorView().setPadding(130, 0, 130, 0);
            WindowManager.LayoutParams attr = window.getAttributes();
            if (attr != null) {
                attr.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                attr.width = ViewGroup.LayoutParams.MATCH_PARENT;
                attr.gravity = Gravity.CENTER;//设置dialog 在布局中的位置
                window.setAttributes(attr);
            }
        }
        dialog.show();
    }

    /**
     * 是否删除确定弹框
     *
     * @param context
     * @param onClickListener
     */
    public static void showDeleteDialog(Context context, String deleteMsg, View.OnClickListener onClickListener) {
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_delete_layout, null);

        TextView delete_msg = inflate.findViewById(R.id.delete_msg);
        TextView delete_cancel = inflate.findViewById(R.id.delete_cancel);
        TextView delete_confirm = inflate.findViewById(R.id.delete_confirm);
        delete_msg.setText(deleteMsg);
        delete_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        delete_confirm.setOnClickListener(onClickListener);
        dialog.setContentView(inflate);
        Window window = dialog.getWindow();
        if (dialog != null && window != null) {
            window.getDecorView().setPadding(130, 0, 130, 0);
            WindowManager.LayoutParams attr = window.getAttributes();
            if (attr != null) {
                attr.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                attr.width = ViewGroup.LayoutParams.MATCH_PARENT;
                attr.gravity = Gravity.CENTER;//设置dialog 在布局中的位置
                window.setAttributes(attr);
            }
        }
        dialog.show();
    }

    /**
     * 身份证识别，弹框选择视频流识别或者拍照识别
     *
     * @param context
     * @param onClickListener
     */
    public static void showChoosePhotoDialog(Context context, View.OnClickListener onClickListener) {
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_choosephoto, null);
        TextView choosePhoto = inflate.findViewById(R.id.abroad_choosephoto);
        TextView takePhoto = inflate.findViewById(R.id.abroad_takephoto);
        TextView cancel = inflate.findViewById(R.id.abroad_choose_cancel);
        choosePhoto.setOnClickListener(onClickListener);
        takePhoto.setOnClickListener(onClickListener);
        cancel.setOnClickListener(onClickListener);
        dialog.setContentView(inflate);
        Window window = dialog.getWindow();
        if (dialog != null && window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams attr = window.getAttributes();
            if (attr != null) {
                attr.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                attr.width = ViewGroup.LayoutParams.MATCH_PARENT;
                attr.gravity = Gravity.BOTTOM;//设置dialog 在布局中的位置
                window.setAttributes(attr);
            }
        }
        dialog.show();
    }

    /**
     * 显示loading 弹框
     *
     * @param context
     */
    public static void showLoadingDialog(Context context) {
        showLoadingDialog(context, "");
    }

    /**
     * 显示loading 弹框
     *
     * @param context
     */
    public static void showLoadingDialog(Context context, String msg) {
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        View progressBar = LayoutInflater.from(context).inflate(R.layout.dialog_loading_layout, null, false);
        TextView message = progressBar.findViewById(R.id.progress_content);
        if (!TextUtils.isEmpty(msg)) {
            message.setText(msg);
            message.setVisibility(View.VISIBLE);
        } else {
            message.setVisibility(View.GONE);
        }
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(progressBar);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 隐藏loading弹框
     */
    public static void dismissLoadingDialog() {
        dismissDialog();
    }

    public static void showProgressDialog(Context context, int progress, long contentLength) {
        if (null == dialog) {
            dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        }
        View progressLayout = LayoutInflater.from(context).inflate(R.layout.dialog_progress_layout, null, false);
        TextView progressTitle = progressLayout.findViewById(R.id.progress_title);
        ProgressBar mProgressBar = progressLayout.findViewById(R.id.progress_bar);
        mProgressBar.setMax(100);
        int progressPercent = Integer.parseInt(String.valueOf(progress * 100 / contentLength));
        Log.e("download percent", "下载中...(" + progressPercent + "%)");
        progressTitle.setText("下载中...(" + progressPercent + "%)");
        progressTitle.setTextColor(context.getResources().getColor(com.divine.base.R.color.dy_theme_color));
        mProgressBar.setProgress(progressPercent);
        dialog.setCancelable(false);
        dialog.setContentView(progressLayout);
        Window window = dialog.getWindow();
        if (dialog != null && window != null) {
            window.getDecorView().setPadding(50, 50, 50, 50);
            WindowManager.LayoutParams attr = window.getAttributes();
            if (attr != null) {
                attr.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                attr.width = ViewGroup.LayoutParams.MATCH_PARENT;
                attr.gravity = Gravity.CENTER;//设置dialog 在布局中的位置
                window.setAttributes(attr);
            }
        }
        dialog.show();
    }

    /**
     * 显示loading 弹框
     *
     * @param context
     */
    public static void showAppLoadingDialog(Context context, String msg) {
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        View progressBar = LayoutInflater.from(context).inflate(R.layout.dialog_app_loading_layout, null, false);
        TextView message = progressBar.findViewById(R.id.progress_content);
        AppLoadingView appProgress = progressBar.findViewById(R.id.progress_app);

        ValueAnimator anim = ValueAnimator.ofInt(0, 100);
        anim.setRepeatCount(ValueAnimator.INFINITE);//设置无限重复
        anim.setRepeatMode(ValueAnimator.RESTART);//设置重复模式
        anim.setDuration(5000);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int percent = (int) animation.getAnimatedValue();
                appProgress.setPercent(percent);
            }
        });
        anim.start();
        if (!TextUtils.isEmpty(msg)) {
            message.setText(msg);
            message.setVisibility(View.VISIBLE);
        } else {
            message.setVisibility(View.GONE);
        }
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(progressBar);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 关闭自定义弹框
     */
    public static void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public static boolean isShowDialog() {
        if (dialog != null)
            return dialog.isShowing();
        return false;
    }
}
