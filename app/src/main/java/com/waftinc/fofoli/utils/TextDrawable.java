package com.waftinc.fofoli.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * Created by Ashwin on 30-Mar-16.
 */
public class TextDrawable extends Drawable {
    final String text;
    final Paint paint;
    Context context;

    public TextDrawable(String text, Context context) {
        this.text = text;
        this.paint = new Paint();
        this.context = context;

        float spTextSize = 15.3f;
        float floatTextSize = spTextSize*context.getResources().getDisplayMetrics().scaledDensity;

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
