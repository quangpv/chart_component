package com.example.billyphan.chart_components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Billy Phan on 10/17/2017.
 */

public class FailureChart extends View implements View.OnClickListener {
    private static final int PROGRESS_BAR_COLOR = Color.BLUE;
    private static final int CIRCLE_ZOOM_COLOR = Color.GREEN;
    private static final int TEXT_COLOR = Color.WHITE;
    private static final float TEXT_SIZE = 60;

    private static final float ANGLE_START = -90;
    private static final float ANGLE_SWEEP = 360;
    private Paint paint;
    private LayoutManager layoutManager;
    private int value;
    private int total;

    public FailureChart(Context context) {
        super(context);
        init(null);
    }

    public FailureChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FailureChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        this.layoutManager = new LayoutManager();
        this.paint = new Paint();
        this.value = 7;
        this.total = 10;
        setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        layoutManager.measure(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawProgressBar(canvas);
        if (!layoutManager.isFinishDrawProgressBar()) {
            layoutManager.seekToProgressValue();
            Log.e("DEBUG", "seekToProgressValue");
        } else {
            drawCircleZoom(canvas);
            drawTextZoom(canvas);
            if (!layoutManager.isFinishDrawCircle()) {
                layoutManager.seekToCircleValue();
            }
        }

        if (!layoutManager.isFinishDrawn()) {
            invalidate();
        }
    }

    private void drawTextZoom(Canvas canvas) {
        paint.reset();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(TEXT_COLOR);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        PointF point = layoutManager.getPointCircleZoom();
        float ratioScale = layoutManager.getCurrentRadius() / layoutManager.getRadius();
        paint.setTextSize(TEXT_SIZE * ratioScale);

        String text = value + "/" + total;
        float oX = point.x - paint.measureText(text) / 2;
        float oY = point.y - (paint.descent() + paint.ascent()) / 2;
        canvas.drawText(text, oX, oY, paint);
    }

    private void drawCircleZoom(Canvas canvas) {
        paint.reset();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(CIRCLE_ZOOM_COLOR);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        PointF point = layoutManager.getPointCircleZoom();
        float radius = layoutManager.getCurrentRadius();
        canvas.drawCircle(point.x, point.y, radius, paint);
    }

    private void drawProgressBar(Canvas canvas) {
        paint.reset();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(PROGRESS_BAR_COLOR);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        RectF bound = layoutManager.getBound();
        float currentAngleSweep = layoutManager.getCurrentSweepAngle();
        canvas.drawArc(bound, ANGLE_START, currentAngleSweep, true, paint);
    }

    @Override
    public void onClick(View view) {
        redraw();
    }

    private void redraw() {
        layoutManager.reset();
        invalidate();
    }

    class LayoutManager {

        private static final float STEP_SWEEP_ANGLE = 5;
        private float currentSweepAngle;
        private float sweepAngle;
        private float currentRadius;
        private float radius;
        private RectF bound;
        private PointF pointCircleZoom;

        public LayoutManager() {
            this.currentSweepAngle = 0;
            this.sweepAngle = 0;
            this.currentRadius = 0;
            this.radius = 0;
            this.bound = new RectF(0, 0, 0, 0);
            this.pointCircleZoom = new PointF(0, 0);
        }

        public void measure(int width, int height) {
            this.currentSweepAngle = 0;
            this.sweepAngle = ((float) value / total) * ANGLE_SWEEP;

            this.currentRadius = 0;
            this.radius = (float) width / 3;

            this.bound.set(0, 0, width, height);
            this.pointCircleZoom.set(width / 2, height / 2);
        }

        public void seekToProgressValue() {
            float dim = sweepAngle - currentSweepAngle;
            float distance = Math.abs(dim);
            float step = distance > STEP_SWEEP_ANGLE ? STEP_SWEEP_ANGLE : distance;
            if (dim > 0) {
                this.currentSweepAngle += step;
            } else {
                this.currentSweepAngle -= step;
            }
        }

        public void seekToCircleValue() {
            float dim = radius - currentRadius;
            float distance = Math.abs(dim);
            float step = distance > STEP_SWEEP_ANGLE ? STEP_SWEEP_ANGLE : distance;
            if (dim > 0) {
                this.currentRadius += step;
            } else {
                this.currentRadius -= step;
            }
        }

        public boolean isFinishDrawProgressBar() {
            return this.currentSweepAngle == this.sweepAngle;
        }

        public boolean isFinishDrawCircle() {
            return this.currentRadius == this.radius;
        }

        public boolean isFinishDrawn() {
            return isFinishDrawCircle() && isFinishDrawProgressBar();
        }

        public RectF getBound() {
            return bound;
        }

        public PointF getPointCircleZoom() {
            return pointCircleZoom;
        }

        public float getCurrentRadius() {
            return currentRadius;
        }

        public float getCurrentSweepAngle() {
            return currentSweepAngle;
        }

        public void reset() {
            this.currentRadius = 0;
            this.currentSweepAngle = 0;
        }

        public float getRadius() {
            return radius;
        }
    }
}
