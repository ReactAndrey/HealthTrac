package com.sph.healthtrac.common;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

public class HTTypefaceSpan extends TypefaceSpan {

    private final Typeface newType;
    private float textSize = -1.0f; // static

    public HTTypefaceSpan(String family, Typeface type) {

        super(family);
        newType = type;
        this.textSize =  -1.0f;
    }

    public HTTypefaceSpan(String family, Typeface type, float textSize) {

        super(family);
        newType = type;
        this.textSize = textSize;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        applyCustomTypeFace(ds, newType);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        applyCustomTypeFace(paint, newType);
    }

    private void applyCustomTypeFace(Paint paint, Typeface tf) { // static

        int oldStyle;

        if(textSize > 0)
            paint.setTextSize(textSize);

        Typeface old = paint.getTypeface();

        if (old == null) {

            oldStyle = 0;

        } else {

            oldStyle = old.getStyle();
        }

        int fake = oldStyle & ~tf.getStyle();

        if ((fake & Typeface.BOLD) != 0) {

            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {

            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(tf);
    }
}