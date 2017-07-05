package com.arno.qqtabview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Arno on 2017/7/2.
 */

public class QQTabView extends LinearLayout {
    private static final String TAG = "QQTabView";
    private float mTabWidth;
    private float mTabHeight;
    private float mTabRange;
    private String mTabName;
    private int mTabAboveImg;
    private int mTabBelowImg;
    private ViewGroup mContainer;
    private TextView mTextView;
    private ImageView mAboveImg;
    private ImageView mBelowImg;
    private View mView;
    private double mSmallRadio;
    private double mBigRadio;
    private float mLastY;
    private float mLastX;

    // 一个参数的构造方法 -- java 代码构造时调用
    public QQTabView(Context context) {
        this(context, null);
    }

    // 两个参数的构造方法 -- xml 构造时调用
    public QQTabView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    // 三个参数的构造方法
    public QQTabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i(TAG, "QQTabView: ");
        // 初始化属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.QQTabView, defStyleAttr, 0);
        mTabWidth = ta.getDimension(R.styleable.QQTabView_qqtab_width, dp2px(60));
        mTabHeight = ta.getDimension(R.styleable.QQTabView_qqtab_height, dp2px(60));
        mTabRange = ta.getFloat(R.styleable.QQTabView_qqtab_range, 1);
        mTabName = ta.getString(R.styleable.QQTabView_qqtab_name);
        mTabAboveImg = ta.getResourceId(R.styleable.QQTabView_qqtab_imgsrc_above, R.drawable.above);
        mTabBelowImg = ta.getResourceId(R.styleable.QQTabView_qqtab_imgsrc_below, R.drawable.below);
        ta.recycle();// 不回收会导致app崩溃，连日志都没有 = =

        // 初始化布局
        mView = inflate(context, R.layout.view_qqtab, null);
        mContainer = (ViewGroup) mView.findViewById(R.id.view_qqtab_container);
        mTextView = ((TextView) mView.findViewById(R.id.view_qqtab_name));
        mAboveImg = ((ImageView) mView.findViewById(R.id.view_qqtab_above_iv));
        mBelowImg = ((ImageView) mView.findViewById(R.id.view_qqtab_below_iv));

        // 计算拖动范围
        mSmallRadio = 0.1 * Math.min(mTabWidth, mTabHeight) * mTabRange;
        mBigRadio = 1.5 * mSmallRadio;

        // 设置布局属性
        setLayoutAndSize(mAboveImg);
        setLayoutAndSize(mBelowImg);

        // 设置图片和文字
        mAboveImg.setImageResource(mTabAboveImg);
        mBelowImg.setImageResource(mTabBelowImg);
        if (!TextUtils.isEmpty(mTabName)) {
            mTextView.setVisibility(VISIBLE);
            mTextView.setText(mTabName);
        }

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        addView(mView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = 0, h = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                LinearLayout.LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                w = w > childWidth ? w : childWidth;
                h += childHeight;
            }
        }
//        Log.i(TAG, "setMeasuredDimension: width=" + getDefaultSize(w, widthMeasureSpec) + ",height=" + getDefaultSize(h, heightMeasureSpec));
//        setMeasuredDimension(getDefaultSize(w, widthMeasureSpec), getDefaultSize(h, heightMeasureSpec));

        final int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        final int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        final int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        Log.i(TAG, "setMeasuredDimension: width=" + ((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth : w)
                + ",height=" + ((modeHeight == MeasureSpec.EXACTLY) ? sizeHeight : h));
        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth : w,
                (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight : h);
    }

    /**
     * onLayout 方法决定了外部容器摆放自定义视图的方式
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.i(TAG, "onLayout: ");
        int childLeft;
        int childTop = 0;
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (child.getVisibility() != GONE) {
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();
                //水平居中显示
                childLeft = (getWidth() - childWidth) / 2;
                //当前子view的top
                childTop += lp.topMargin;
                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
                //下一个view的top是当前子view的top + height + bottomMargin
                childTop += childHeight + lp.bottomMargin;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //获取原点位置
                mLastY = event.getY();
                mLastX = event.getX();

                break;
            case MotionEvent.ACTION_MOVE:

                float deltaX = event.getX() - mLastX;
                float deltaY = event.getY() - mLastY;

                //移动视图
                onEventMove(deltaX, deltaY);

                break;
            case MotionEvent.ACTION_UP:
                //恢复原位
                setPosition(mAboveImg, 0, 0);
                setPosition(mBelowImg, 0, 0);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void onEventMove(float x, float y) {
        int distance = (int) Math.sqrt(x * x + y * y);
        double angle = Math.atan2(y, x);
        if (distance > mSmallRadio) {
            setPosition(mAboveImg, mBigRadio, angle);
            setPosition(mBelowImg, mSmallRadio, angle);
        } else {
            setPosition(mAboveImg, 1.5 * distance, angle);
            setPosition(mBelowImg, distance, angle);
        }
    }

    private void setPosition(View view, double radio, double angle) {
        if (radio == 0) {
            view.setX(view.getLeft());
            view.setY(view.getTop());
            return;
        }
        view.setX((float) (view.getLeft() + radio * Math.cos(angle)));
        view.setY((float) (view.getTop() + radio * Math.sin(angle)));
    }

    public float dp2px(float value) {
//        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, value, getResources().getDisplayMetrics());
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    public void setLayoutAndSize(View view) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
        lp.width = (int) mTabWidth;
        lp.height = (int) mTabHeight;
        lp.gravity = Gravity.CENTER;
        view.setLayoutParams(lp);

        int p = (int) mBigRadio;
        view.setPadding(p, p, p, p);
    }

    public void setTabName(String name) {
        mTextView.setText(name);
    }

    public void setTabAboveImg(int src) {
        mAboveImg.setImageResource(src);
    }

    public void setTabBelowImg(int src) {
        mBelowImg.setImageResource(src);
    }
}

