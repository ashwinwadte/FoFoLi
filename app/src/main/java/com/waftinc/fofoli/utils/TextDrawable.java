package com.waftinc.fofoli.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class TextDrawable extends Drawable {
    private final String text;
    private final Paint paint;
    private final Context mContext;

    public TextDrawable(String text, Context context) {
        this.text = text;
        this.paint = new Paint();
        this.mContext = context;

        float spTextSize = 15.3f;
        float floatTextSize = spTextSize * mContext.getResources().getDisplayMetrics().scaledDensity;

        paint.setColor(Color.BLACK);
        paint.setTextSize(floatTextSize);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawText(text, 0, 6, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;

    }
}
