package com.divine.widget.old;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;

import com.divine.dy.lib_utils.sys.DensityUtils;
import com.divine.R;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * author: Divine
 * <p>
 * date: 2019/2/27
 */
public class FilterTextView extends AppCompatTextView implements OnClickListener {
    private Context mContext;
    private PopupWindow filterPop;
    public boolean isFilterPopShow;
    //    private OnOptionsItemClickListener optionsItemClickListener;

    public FilterTextView(Context context) {
        super(context);
        initView(context);
    }

    public FilterTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FilterTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        setTextAlignment(TEXT_ALIGNMENT_CENTER);
        setDropDown(R.mipmap.ic_arrow_down);
        setTextColor(context.getResources().getColor(R.color.gray_text_color_two));
        setTextSize(11.0f);
    }

    /**
     * 显示筛选框
     */
    //    public void showFilter(int popHeight,
    //                           View contentView,
    //                           View parentView,
    //                           RecyclerView recyclerView,
    //                           View markLayout,
    //                           BaseQuickAdapter adapter) {
    //        filterPop = new PopupWindow();
    //        if (contentView == null) {
    //            contentView = LayoutInflater.from(mContext).inflate(R.layout.pop_engineer_filter_layout, null);
    //            recyclerView = contentView.findViewById(R.id.engineer_filter_cycle);
    //            markLayout = contentView.findViewById(R.id.engineer_filter_mark);
    //        }
    //        //设置popupwindow宽高
    //        int popWidth = LinearLayout.LayoutParams.MATCH_PARENT;
    //        filterPop.setWidth(popWidth);
    //        filterPop.setHeight(popHeight);
    //        //点击空白位置可以dismiss popupwindow
    //        //        filterPop.setFocusable(true);
    //        //        filterPop.setOutsideTouchable(true);
    //        //解决5.0不能dismiss的问题
    //        //        filterPop.setBackgroundDrawable(new BitmapDrawable());
    //        //recycleView设置
    //        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    //        recyclerView.setAdapter(adapter);
    //        recyclerView.addItemDecoration(new ItemDecorationGM(getContext(), LinearLayoutManager.VERTICAL
    //                , DensityUtils.dip2px(1, getContext()), getResources().getColor(R.color.back_divider)));
    //        if (adapter instanceof BaseQuickAdapter) {
    //            adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
    //                @Override
    //                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
    //                    setTextColor(getResources().getColor(R.color.blue_button));
    //                    setDropDown(R.mipmap.icon_tab_drop_down_blue);
    //                    optionsItemClickListener.onOptionsItemClick(adapter, view, position);
    //                    hideFilter();
    //                }
    //            });
    //        }
    //        //点击蒙层部分dismisspopupwindow
    //        markLayout.setOnClickListener(this);
    //        filterPop.setContentView(contentView);
    //        //在控件正下方显示
    //        filterPop.showAsDropDown(parentView, 0, 0);
    //        isFilterPopShow = true;
    //    }
    public void hideFilter() {
        filterPop.dismiss();
        isFilterPopShow = false;
    }

    public PopupWindow getFilterPop() {
        return filterPop;
    }

    /**
     * markLayout点击
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        filterPop.dismiss();
    }

    //    public void setOnOptionsItemClickListener(OnOptionsItemClickListener listener) {
    //        this.optionsItemClickListener = listener;
    //    }
    //
    //    public interface OnOptionsItemClickListener {
    //        void onOptionsItemClick(BaseQuickAdapter adapter, View view, int position);
    //    }

    /**
     * 右侧图标
     *
     * @param resId
     */
    public void setDropDown(int resId) {
        Drawable rightDrawable = getContext().getResources().getDrawable(resId);
        setCompoundDrawablesWithIntrinsicBounds(null, null, rightDrawable, null);
        setCompoundDrawablePadding(DensityUtils.dip2px(8, getContext()));
        postInvalidate();
    }
}
