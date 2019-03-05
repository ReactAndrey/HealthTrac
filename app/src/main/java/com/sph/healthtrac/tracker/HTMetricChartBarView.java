package com.sph.healthtrac.tracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.sph.healthtrac.common.PixelUtil;

/**
 * TODO: document your custom view class.
 */
public class HTMetricChartBarView extends View {

    private int selectedChart;
    private String selectedMetric;
    private float chartMaxMetric;
    private float chartMinMetric;
    private float dailyTotalMetric;
    private float chartTargetMetric;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int greenBarColor = Color.rgb(187,227,69);
    private int lightGreenBarColor = Color.rgb(228,244,181);

    public HTMetricChartBarView(Context context) {
        super(context);
        init(null, 0);
    }

    public HTMetricChartBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public HTMetricChartBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        float contentWidth = getWidth() - paddingLeft - paddingRight;
        float contentHeight = getHeight() - paddingTop - paddingBottom;

        float barYOffset = 0.0f;
        float barXOffset = 0.0f;

        switch (selectedChart){
            case 0:
                barXOffset = contentWidth * (11.75f / 36.0f);
                break;
            case 1:
                barXOffset = contentWidth / 3.0f;
                break;
            case 2:
                barXOffset = contentWidth * (1.7f / 8.4f);
                break;
            case 3:
                barXOffset = contentWidth / 7.0f;
                break;
        }


        if("Weight".equals(selectedMetric))
            barYOffset = ((chartMaxMetric - dailyTotalMetric) / (chartMaxMetric - chartMinMetric)) * contentHeight;
        else
            barYOffset = ((chartMaxMetric - dailyTotalMetric) / chartMaxMetric) * contentHeight;

        // the full color bar for dailyTotalMetric
        if ("Weight".equals(selectedMetric)) {
            if(dailyTotalMetric >= chartTargetMetric) //over their target
                paint.setColor(lightGreenBarColor);
            else //they hit their target
                paint.setColor(greenBarColor);
        } else { //all other metrics
            if(dailyTotalMetric >= chartTargetMetric)
                paint.setColor(greenBarColor);
            else //didn't hit their target
                paint.setColor(lightGreenBarColor);
        }
        paint.setAntiAlias(true);

        // the full gray bar for dailyTotalMetric
        canvas.drawRect(barXOffset, barYOffset, contentWidth - barXOffset, contentHeight, paint);


    }

    public void setSelectedChart(int selectedChart) {
        this.selectedChart = selectedChart;
    }

    public void setSelectedMetric(String selectedMetric) {
        this.selectedMetric = selectedMetric;
    }

    public void setChartMaxMetric(float chartMaxMetric) {
        this.chartMaxMetric = chartMaxMetric;
    }

    public void setChartMinMetric(float chartMinMetric) {
        this.chartMinMetric = chartMinMetric;
    }

    public void setDailyTotalMetric(float dailyTotalMetric) {
        this.dailyTotalMetric = dailyTotalMetric;
    }

    public void setChartTargetMetric(float chartTargetMetric) {
        this.chartTargetMetric = chartTargetMetric;
    }

}
