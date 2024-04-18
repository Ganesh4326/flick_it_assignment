package com.example.flickit.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CircleAnimationView extends View {

    private Paint circlePaint;
    private float radius = 0;
    private float maxRadius = 100; // Change this as per your requirement
    private boolean isAnimating = false;

    public CircleAnimationView(Context context) {
        super(context);
        init();
    }

    public CircleAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        circlePaint = new Paint();
        circlePaint.setColor(Color.RED); // Change color as per your requirement
        circlePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isAnimating) {
            canvas.drawCircle(canvas.getWidth() / 2f, canvas.getHeight() / 2f, radius, circlePaint);
        }
    }

    public void startAnimation() {
        isAnimating = true;
        animateRadius();
    }

    private void animateRadius() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, maxRadius);
        animator.setDuration(2000); // Change duration as per your requirement
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                radius = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
    }
}
