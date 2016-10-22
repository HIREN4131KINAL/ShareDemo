package com.example.guest999.firebasenotification.ProgressDialog;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


class Dot {
    int mCurrentColorIndex;
    float cx;
    float cy;
    int position;
    ValueAnimator positionAnimator;
    ValueAnimator colorAnimator;
    private Paint mPaint;
    private int mDotRadius;
    private DotLoader mParent;

    Dot(DotLoader parent, int dotRadius, int position) {
        this.position = position;
        mParent = parent;
        mCurrentColorIndex = 0;
        mDotRadius = dotRadius;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mParent.mColors[mCurrentColorIndex]);
        mPaint.setShadowLayer(5.5f, 6.0f, 6.0f, Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void setColorIndex(int index) {
        mCurrentColorIndex = index;
        mPaint.setColor(mParent.mColors[index]);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    public int getCurrentColor() {
        return mParent.mColors[mCurrentColorIndex];
    }

    public int incrementAndGetColor() {
        incrementColorIndex();
        return getCurrentColor();
    }

    void applyNextColor() {
        mCurrentColorIndex++;
        if (mCurrentColorIndex >= mParent.mColors.length)
            mCurrentColorIndex = 0;
        mPaint.setColor(mParent.mColors[mCurrentColorIndex]);
    }

    int incrementColorIndex() {
        mCurrentColorIndex++;
        if (mCurrentColorIndex >= mParent.mColors.length)
            mCurrentColorIndex = 0;
        return mCurrentColorIndex;
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(cx, cy, mDotRadius, mPaint);
    }
}
