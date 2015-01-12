package com.idunnolol.sotm.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * A version of ImageView that lets you draw a flat color
 * on top of the image.
 */
public class ShadedImageView extends ImageView {

    private Integer mColor;

    public ShadedImageView(Context context) {
        super(context);
    }

    public ShadedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShadedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setShadeColor(int color) {
        if (mColor == null || mColor != color) {
            mColor = color;
            invalidate();
        }
    }

    public void disableShade() {
        if (mColor != null) {
            mColor = null;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mColor != null) {
            canvas.drawColor(mColor);
        }
    }

}
