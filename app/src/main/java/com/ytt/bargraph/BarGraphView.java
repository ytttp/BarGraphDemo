package com.ytt.bargraph;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by ytt on 2017/10/17.
 * 柱状图开始以为要画矩形  后来发现只要画一根粗一点的线也是柱状的效果
 */

public class BarGraphView extends View {
    private Context mContext;
    /**
     * 柱状图的宽度
     */
    private float mLineWidth;
    /**
     * 柱状图之间的间隔
     */
    private float mLineSpaceWidth;
    /**
     * 柱状图的颜色
     */
    private int barGraphBgColor;
    /**
     * X轴字体的大小
     */
    private float mXTextSize;
    /**
     * Y轴的字体大小
     */
    private float mYTextSize;
    /**
     * xy轴线和字体的颜色
     */
    private int mXYBGColor;
    /**
     * 柱状图执行动画的长度
     */
    private Integer duration;
    /**
     * 是否显示X轴的文字
     */
    private boolean isShowXText;
    /**
     * 是否显示Y轴的文字
     */
    private boolean isShowYText;

    /**
     * 柱状图的画笔
     */
    private Paint mBarGraphPaint;
    /**
     * 柱状图上面字的画笔
     */
    private Paint mBarGraphTextPaint;
    /**
     * X轴的文字画笔
     */
    private Paint mXTextPaint;
    /**
     * Y轴的文字画笔
     */
    private Paint mYTextPaint;
    /**
     * XY轴的坐标画笔
     */
    private Paint mXYLinePaint;
    /**
     * 当前柱状图的最大高度
     */
    private int maxHeight;
    /**
     * 实际高度
     */
    private int heightMeasureSpec;
    /**
     * 实际宽度
     */
    private int widthMeasureSpec;
    /**
     * 圆柱占用的高度
     */
    private float bottomHeight;

    //保存柱状图的数据
    private int[][] barGraphDataList;
    //保存柱状图的颜色
    private int[] barGraphColorList;
    //保存柱状图X轴信息
    private String[] barGraphTextList;
    //Y轴线离左边的距离，以便绘制Y轴数字
    private float mLeftYWidth;
    //X轴线离底部的距离，以便绘制X轴的文字
    private float mBottomXWidth;


    public BarGraphView(Context context) {
        this(context, null);
    }

    public BarGraphView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarGraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BarGraphView);
        //柱状图的宽度
        mLineWidth = typedArray.getDimension(R.styleable.BarGraphView_bar_graph_width, dip2px(20));
        //柱状图之间的间隔
        mLineSpaceWidth = typedArray.getDimension(R.styleable.BarGraphView_bar_graph_distance, dip2px(20));
        //柱状图的颜色
        barGraphBgColor = typedArray.getColor(R.styleable.BarGraphView_bar_graph_bg_color, Color.BLUE);
        //X轴字体大小
        mXTextSize = typedArray.getDimension(R.styleable.BarGraphView_bar_graph_x_textSize, sp2px(14));
        //y轴字体大小
        mYTextSize = typedArray.getDimension(R.styleable.BarGraphView_bar_graph_y_textSize, sp2px(14));
        //xy轴的颜色
        mXYBGColor = typedArray.getColor(R.styleable.BarGraphView_bar_graph_xy_line_text_color, Color.BLACK);
        //执行动画的时间
        duration = typedArray.getInteger(R.styleable.BarGraphView_bar_graph_animation_duration, 1000);
        isShowXText = typedArray.getBoolean(R.styleable.BarGraphView_bar_graph_isShow_X_text, true);
        isShowYText = typedArray.getBoolean(R.styleable.BarGraphView_bar_graph_isShow_Y_text, true);
        /**
         * 系统使用一个arraypool池来存放typearray 用完及时销毁以便复用
         * */
        typedArray.recycle();
        initView();
    }

    /**
     * 初始化画笔
     */
    private void initView() {
        mBarGraphPaint = new Paint();
        mBarGraphPaint.setStrokeWidth(mLineWidth);
        mBarGraphPaint.setAntiAlias(true);

        mBarGraphTextPaint = new Paint();
        mBarGraphTextPaint.setStrokeWidth(dip2px(8));
        mBarGraphTextPaint.setTextSize(sp2px(14f));
        mBarGraphTextPaint.setAntiAlias(true);

        mXTextPaint = new Paint();
        mXTextPaint.setStrokeWidth(3);
        mXTextPaint.setColor(mXYBGColor);
        mXTextPaint.setTextSize(mXTextSize);
        mXTextPaint.setAntiAlias(true);

        mYTextPaint = new Paint();
        mYTextPaint.setStrokeWidth(3);
        mYTextPaint.setColor(mXYBGColor);
        mYTextPaint.setTextSize(mYTextSize);
        mYTextPaint.setAntiAlias(true);

        mXYLinePaint = new Paint();
        mXYLinePaint.setStrokeWidth(3);
        mXYLinePaint.setColor(mXYBGColor);
        mXYLinePaint.setAntiAlias(true);
    }

    /**
     * 测量宽度
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureWidth(heightMeasureSpec);
    }

    /**
     * 长度可能超出屏幕范围
     */
    private void measureWidth(int heightMeasureSpec) {
        if (barGraphDataList != null && barGraphDataList.length > 0) {
            widthMeasureSpec = (int) (mLineSpaceWidth * (barGraphDataList[0].length + 1) + mLineWidth * barGraphDataList.length * barGraphDataList[0].length) + dip2px(5);
        }
        this.heightMeasureSpec = MeasureSpec.getSize(heightMeasureSpec);
        //左边字体的长度
        widthMeasureSpec += mLeftYWidth + dip2px(10);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        measureWidth(h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        bottomHeight = heightMeasureSpec - mBottomXWidth;
        if (barGraphDataList == null || barGraphDataList.length <= 0)
            return;
        //画柱状图
        drawBarGraph(canvas);
        //画XY轴坐标
        drawXYLine(canvas);
        //给XY轴坐标写字
        drawXYText(canvas);
    }

    //给Y轴和X轴写相应的文字
    private void drawXYText(Canvas canvas) {
        if (isShowYText) {
            //Y轴写字
            for (int i = 1; i <= 5; i++) {
                float startY = bottomHeight - bottomHeight * 0.9f / maxHeight * maxHeight / 5 * i;
                canvas.drawLine(dip2px(10) + mLeftYWidth, startY, dip2px(15) + mLeftYWidth, startY, mYTextPaint);
                float width = mYTextPaint.measureText(maxHeight / 5 * i + "");
                Paint.FontMetricsInt fontMetricsInt = mYTextPaint.getFontMetricsInt();
                float dy = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom;
                canvas.drawText(maxHeight / 5 * i + "", (int) (dip2px(10) + mLeftYWidth - width - dip2px(5)), startY + dy, mYTextPaint);
            }
        }
        if (!isShowXText) {
            return;
        }
        //X轴写字
        if (barGraphTextList != null && barGraphTextList.length > 0) {
            for (int i = 0; i < barGraphTextList.length; i++) {
                float startX = mLineSpaceWidth * (i + 1) + mLineWidth * barGraphDataList.length * i + mLeftYWidth + dip2px(10);
                //中间有一个间隔
                startX = startX + (mLineWidth * barGraphDataList.length) * 1.0f / 2;
                float textWidth = mXTextPaint.measureText(barGraphTextList[i]);
                canvas.drawText(barGraphTextList[i], startX - textWidth / 2, heightMeasureSpec - dip2px(5), mXTextPaint);
            }
        }
    }

    //画X轴和Y轴的竖线+箭头
    private void drawXYLine(Canvas canvas) {
        /**
         * 让Y轴文字与最左有dip2px(10)的边距
         * */
        //Y轴竖线
        canvas.drawLine(dip2px(10) + mLeftYWidth, bottomHeight, dip2px(10) + mLeftYWidth, 10, mXYLinePaint);
        //X轴竖线
        canvas.drawLine(dip2px(10) + mLeftYWidth, bottomHeight, widthMeasureSpec - 10, bottomHeight, mXYLinePaint);
        //画个箭头？？Y轴
        canvas.drawLine(dip2px(10) + mLeftYWidth, 10, dip2px(6) + mLeftYWidth, 20, mXYLinePaint);
        canvas.drawLine(dip2px(10) + mLeftYWidth, 10, dip2px(14) + mLeftYWidth, 20, mXYLinePaint);
        //X轴箭头
        canvas.drawLine(widthMeasureSpec - 10, bottomHeight, widthMeasureSpec - 20, bottomHeight - dip2px(4), mXYLinePaint);
        canvas.drawLine(widthMeasureSpec - 10, bottomHeight, widthMeasureSpec - 20, bottomHeight + dip2px(4), mXYLinePaint);
    }

    //画柱状图
    private void drawBarGraph(Canvas canvas) {
        if (barGraphDataList != null && barGraphDataList.length > 0) {
            for (int i = 0; i < barGraphDataList[0].length; i++) {
                float startX = mLineSpaceWidth * (i + 1) + mLineWidth * barGraphDataList.length * i + mLeftYWidth + dip2px(10) + mLineWidth / 2;
                int index = 0;
                while (index < barGraphDataList.length) {
                    if (barGraphColorList != null && barGraphColorList.length > index) {
                        mBarGraphPaint.setColor(barGraphColorList[index]);
                        mBarGraphTextPaint.setColor(barGraphColorList[index]);
                    } else {
                        mBarGraphPaint.setColor(barGraphBgColor);
                        mBarGraphTextPaint.setColor(barGraphBgColor);
                    }

                    float stopY = bottomHeight * 0.9f / maxHeight * barGraphDataList[index][i];

                    canvas.drawLine(startX, bottomHeight, startX, bottomHeight - stopY, mBarGraphPaint);

                    String text = String.valueOf(barGraphDataList[index][i]);
                    float textWidth = mBarGraphTextPaint.measureText(text, 0, text.length());
                    canvas.drawText(text, startX - textWidth / 2, bottomHeight - stopY - 10, mBarGraphTextPaint);
                    startX += mLineWidth;
                    index++;
                }
            }
        }
    }

    /**
     * 传进来的数组要求保持数组长度一致
     */
    public void setBarGraphData(@NonNull int[][] barGraphDataList, int[] barGraphColorList, String[] barGraphTextList) {
        this.barGraphDataList = barGraphDataList;
        this.barGraphColorList = barGraphColorList;
        this.barGraphTextList = barGraphTextList;

        //计算出最高的坐标
        for (int i = 0; i < barGraphDataList.length; i++) {
            for (int j = 0; j < barGraphDataList[i].length; j++) {
                if (maxHeight < barGraphDataList[i][j]) {
                    maxHeight = barGraphDataList[i][j];
                }
            }
        }
        while (maxHeight % 5 != 0) {
            maxHeight++;
        }
        if (barGraphTextList != null && barGraphTextList.length > 0) {
            isShowXText = true;
        }
        if (isShowYText) {
            mLeftYWidth = mYTextPaint.measureText(String.valueOf(maxHeight));
        }
        mBottomXWidth = dip2px(10);
        if (isShowXText) {
            Paint.FontMetrics fontMetrics = mXTextPaint.getFontMetrics();
            mBottomXWidth += ((fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom) * 2;
        }
        measureWidth(heightMeasureSpec);

        invalidate();
    }

    /**
     * 不需要显示XY轴文字
     */
    public void setIsShowXYText(boolean isShowXText, boolean isShowYText) {
        this.isShowXText = isShowXText;
        this.isShowYText = isShowYText;
    }

    /**
     * 设置圆柱的宽度
     */
    public void setBarGraphWidth(int width) {
        this.mLineWidth = dip2px(width);
        measureWidth(heightMeasureSpec);
        mBarGraphPaint.setStrokeWidth(mLineWidth);
        invalidate();
    }

    /**
     * 设置圆柱之间的间隔
     */
    public void setBarGraphDistance(int distance) {
        this.mLineSpaceWidth = dip2px(distance);
        measureWidth(heightMeasureSpec);
        invalidate();
    }

    /**
     * 设置X轴文字大小
     */
    public void setXTextSize(float textSize) {
        this.mXTextSize = sp2px(textSize);
        mXTextPaint.setTextSize(mXTextSize);
        invalidate();
    }

    /**
     * 设置Y轴的文字大小
     */
    public void setYTextSize(float textSize) {
        this.mYTextSize = sp2px(textSize);
        mYTextPaint.setTextSize(mYTextSize);
        invalidate();
    }

    //开始动画
    public void startAnimation(int... duration) {
        if (barGraphDataList == null || barGraphDataList.length <= 0)
            return;
        AnimatorSet animationSet = new AnimatorSet();
        for (int i = 0; i < barGraphDataList.length; i++) {
            final int finalI = i;
            for (int j = 0; j < barGraphDataList[i].length; j++) {
                ValueAnimator animator = ValueAnimator.ofInt(0, barGraphDataList[i][j]);
                final int finalJ = j;
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        barGraphDataList[finalI][finalJ] = (int) animation.getAnimatedValue();
                        invalidate();
                    }
                });
                animationSet.playTogether(animator);
            }
        }
        animationSet.setInterpolator(new DecelerateInterpolator());
        animationSet.setDuration(duration != null ? duration[0] : this.duration);
        animationSet.start();
    }

    /**
     * sp转px
     */
    public int sp2px(float spValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * dp转px
     */
    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
