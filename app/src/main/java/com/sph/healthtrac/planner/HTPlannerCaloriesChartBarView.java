package com.sph.healthtrac.planner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.sph.healthtrac.R;
import com.sph.healthtrac.common.PixelUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class HTPlannerCaloriesChartBarView extends View {

    private int selectedChart;

    private float plannerMaxCalorie;
    private float dailyTotalCalorie;
    private float dailyMacroCalorie;

    private float dailyCarbsPercentage;
    private float dailyProteinPercentage;
    private float dailyFatPercentage;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int grayBarColor = Color.rgb(244,245,245);
    private int blueBarColor = Color.rgb(116,204,240);
    private int greenBarColor = Color.rgb(187,227,69);
    private int orangeBarColor = Color.rgb(247,168,97);

    public HTPlannerCaloriesChartBarView(Context context) {
        super(context);
        init(null, 0);
    }

    public HTPlannerCaloriesChartBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public HTPlannerCaloriesChartBarView(Context context, AttributeSet attrs, int defStyle) {
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

        barYOffset = ((plannerMaxCalorie - dailyTotalCalorie) / plannerMaxCalorie) * contentHeight;

        // the full gray bar for dailyTotalCalories
        paint.setColor(grayBarColor);
        paint.setAntiAlias(true);

        // the full gray bar for dailyTotalCalories
        canvas.drawRect(barXOffset, barYOffset, contentWidth - barXOffset, contentHeight, paint);

        // offset for calories-only items with no macro information
        barYOffset = ((plannerMaxCalorie - dailyMacroCalorie) / plannerMaxCalorie) * contentHeight;

        float barHeight = 0.0f;
        // the blue bar for dailyCarbsPercentage
        barHeight = (dailyMacroCalorie/plannerMaxCalorie) * (dailyCarbsPercentage/100) * contentHeight;
        if(barHeight > 0) {
            paint.setColor(blueBarColor);
            canvas.drawRect(barXOffset, barYOffset, contentWidth - barXOffset, barYOffset + barHeight - PixelUtil.dpToPx(getContext(), 1), paint);
            barYOffset += barHeight;
        }

        // the green bar for dailyProteinPercentage
        barHeight = (dailyMacroCalorie/plannerMaxCalorie) * (dailyProteinPercentage/100) * contentHeight;
        if(barHeight > 0) {
            paint.setColor(greenBarColor);
            canvas.drawRect(barXOffset, barYOffset, contentWidth - barXOffset, barYOffset + barHeight - PixelUtil.dpToPx(getContext(), 1), paint);
            barYOffset += barHeight;
        }

        // the green bar for dailyProteinPercentage
        barHeight = (dailyMacroCalorie/plannerMaxCalorie) * (dailyFatPercentage/100) * contentHeight;
        if(barHeight > 0) {
            paint.setColor(orangeBarColor);
            canvas.drawRect(barXOffset, barYOffset, contentWidth - barXOffset, barYOffset + barHeight, paint);
        }

    }

    public void setSelectedChart(int selectedChart) {
        this.selectedChart = selectedChart;
    }

    public void setPlannerMaxCalorie(float plannerMaxCalorie) {
        this.plannerMaxCalorie = plannerMaxCalorie;
    }

    public void setDailyTotalCalorie(float dailyTotalCalorie) {
        this.dailyTotalCalorie = dailyTotalCalorie;
    }

    public void setDailyMacroCalorie(float dailyMacroCalorie) {
        this.dailyMacroCalorie = dailyMacroCalorie;
    }

    public void setDailyFatPercentage(float dailyFatPercentage) {
        this.dailyFatPercentage = dailyFatPercentage;
    }

    public void setDailyProteinPercentage(float dailyProteinPercentage) {
        this.dailyProteinPercentage = dailyProteinPercentage;
    }

    public void setDailyCarbsPercentage(float dailyCarbsPercentage) {
        this.dailyCarbsPercentage = dailyCarbsPercentage;
    }

}
