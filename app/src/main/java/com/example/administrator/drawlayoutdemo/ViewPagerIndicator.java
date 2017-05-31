package com.example.administrator.drawlayoutdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/3/30.
 */

public class ViewPagerIndicator extends LinearLayout {
    private static final int COLOR_TEXT_NORMAL = 0X77FFFFFF;
    private static final int COLOR_TEXT_HIGH = 0XffFFFFFF;
    private Paint mPaint;//画笔
    private Path mPath;//构建三角形，路径
    //三角形底部宽度
    private int mTrangleWidth;
    //三角形高度
    private int mTrangleHeight;
    //三角形底部宽度与屏幕宽度的比例
    private static final float RADIO_WIDTH = 1 / 6f;
    //三角形起始位置
    private int mInitTransiationX;
    //移动时的偏移量
    private int mTransiationX;


    private int mTabVisibleCount;

    private static final int COUNT_DEFAULT_TAB = 4;
    private List<String> mTitles;
    private ViewPager mViewPager;

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
//获取可见tab数量
        TypedArray typedArray = context.obtainStyledAttributes
                (attrs, R.styleable.ViewPagerIndicator);

        mTabVisibleCount = typedArray.getInt
                (R.styleable.ViewPagerIndicator_visible_tab_count, COUNT_DEFAULT_TAB);
        if (mTabVisibleCount < 0) {
            mTabVisibleCount = COUNT_DEFAULT_TAB;
        }
        typedArray.recycle();


//初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#ffffff"));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mTrangleWidth = (int) (w / mTabVisibleCount * RADIO_WIDTH);
        mInitTransiationX = w / mTabVisibleCount / 2 - mTrangleWidth / 2;

        initTrangle();

    }

    /**
     * xml加载完毕后调用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int cCount = getChildCount();
        if (cCount == 0)
            return;

        for (int i = 0; i < cCount; i++) {
            View view = getChildAt(i);
            LayoutParams lp = (LayoutParams) view.getLayoutParams();

            lp.weight = 0;
            //设置每个tab的宽度
            lp.width = getScreenWidth() / mTabVisibleCount;
            view.setLayoutParams(lp);


        }
        setItemClickEvent();
    }

    /**
     * 初始化三角形
     */
    private void initTrangle() {

        mTrangleHeight = mTrangleWidth / 2;//高度是底边宽度的一半，也就是底边角度为45度

        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(mTrangleWidth, 0);
        mPath.lineTo(mTrangleWidth / 2, -mTrangleHeight);
        mPath.close();


    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.save();
        canvas.translate(mInitTransiationX + mTransiationX, getHeight());
        canvas.drawPath(mPath, mPaint);

        canvas.restore();
    }

    /**
     * 指示器跟随手指进行滚动
     *
     * @param position
     * @param offset
     */
    public void scroll(int position, float offset) {
        int tabWidth = getWidth() / mTabVisibleCount;
        mTransiationX = (int) (tabWidth * offset + tabWidth * position);
//在tab位于最后一个时，容器跟随移动
        if (mTabVisibleCount != 1) {
            if (position >= mTabVisibleCount - 2 && offset > 0 && getChildCount() > mTabVisibleCount) {
                this.scrollTo(
                        (int) (position - (mTabVisibleCount - 2)) * tabWidth + (int) (tabWidth * offset),
                        0);
            }
        } else {
            this.scrollTo((int) (position * tabWidth + tabWidth * offset), 0);
        }


        //三角形重绘
        invalidate();

    }

    /**
     * 获得屏幕宽度
     *
     * @return
     */
    public int getScreenWidth() {
        WindowManager wm = (WindowManager)
                getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();

        wm.getDefaultDisplay().getMetrics(outMetrics);


        return outMetrics.widthPixels;
    }

    /**
     * 根据标题，动态添加tab
     *
     * @param titles
     */
    public void setTabItemTitles(List<String> titles) {
        if (titles != null && titles.size() > 0) {
            this.removeAllViews();
            mTitles = titles;
            for (String title : mTitles) {
                addView(generateTextView(title));
            }
            setItemClickEvent();
        }

    }

    /**
     * 设置可见tab的数量
     *
     * @param count
     */
    public void setVisibleTabCount(int count) {
        mTabVisibleCount = count;


    }

    /**
     * 根据title创建textView
     *
     * @param title
     * @return
     */
    private View generateTextView(String title) {
        TextView tv = new TextView(getContext());
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth() / mTabVisibleCount;
        tv.setText(title);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tv.setTextColor(COLOR_TEXT_NORMAL);
        tv.setLayoutParams(lp);
        return tv;


    }

    interface PageOnchangeListener {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        public void onPageSelected(int position);

        public void onPageScrollStateChanged(int state);
    }

    public PageOnchangeListener mListener;

    public void setOnPaeChangeListener(PageOnchangeListener listener) {
        this.mListener = listener;
    }


    public void setViewPager(ViewPager viewPager, int pos) {

        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                scroll(position, positionOffset);

                if (mListener != null) {
                    mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (mListener != null) {
                    mListener.onPageSelected(position);
                }
                highLightTextView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mListener != null) {
                    mListener.onPageScrollStateChanged(state);
                }
            }
        });
        mViewPager.setCurrentItem(pos);
        highLightTextView(pos);
    }

    /**
     * 设置文本颜色
     */
    private void resetTextViewColor() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
            }
        }
    }

    /**
     * 高亮某个tab文本
     *
     * @param pos
     */
    private void highLightTextView(int pos) {
        resetTextViewColor();
        View view = getChildAt(pos);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(COLOR_TEXT_HIGH);
        }


    }

    /**
     * 设置tab点击事件
     */
    private void setItemClickEvent() {
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });


        }
    }
}
