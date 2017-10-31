package com.example.billyphan.chart_components.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Billy Phan on 10/17/2017.
 */

public class CircleProgressBar extends View implements View.OnClickListener {
    private static final float CIRCLE_ANGLE = 360;
    private static final float START_ANGLE = -90;
    private static final float ANGLE_MAX = 10;
    private Paint paint;
    private LayoutManager layoutManager;
    private int backgroundProgressColor;
    private int progressColor;
    private int textColor;
    private float textSize;
    private int progress;
    private float progressWidth;
    private int total;

    public CircleProgressBar(Context context) {
        super(context);
        init(null);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        this.paint = new Paint();
        this.layoutManager = new LayoutManager();
        this.backgroundProgressColor = Color.GRAY;
        this.progressColor = Color.RED;
        this.textColor = Color.RED;
        this.textSize = 70;
        this.progressWidth = 20;
        this.progress = 17;
        this.total = 20;
        setOnClickListener(this);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.layoutManager
                .measure(MeasureSpec.getSize(widthMeasureSpec),
                        MeasureSpec.getSize(heightMeasureSpec));
    }

    //region Draw view
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackgroundProgress(canvas);
        drawProgress(canvas);
        clearBackgroundInner(canvas);
        drawTextProgress(canvas);

        if (!this.layoutManager.isFinishDraw()) {
            this.layoutManager.moveToSweepAngle();
            invalidate();
        }
    }

    private void drawBackgroundProgress(Canvas canvas) {
        paint.reset();
        paint.setStyle(Paint.Style.FILL);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        this.paint.setColor(this.backgroundProgressColor);
        RectF boundCircle = this.layoutManager.getBoundCircle();
        canvas.drawArc(boundCircle, START_ANGLE, CIRCLE_ANGLE, true, paint);
    }

    private void clearBackgroundInner(Canvas canvas) {
        paint.reset();
        paint.setColor(Color.TRANSPARENT);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        RectF boundCircle = this.layoutManager.getBoundCircle();
        RectF boundBackground = new RectF(boundCircle.left + this.progressWidth,
                boundCircle.top + this.progressWidth,
                boundCircle.right - this.progressWidth,
                boundCircle.bottom - this.progressWidth
        );
        canvas.drawArc(boundBackground, START_ANGLE, CIRCLE_ANGLE, true, paint);
    }

    private void drawProgress(Canvas canvas) {
        paint.reset();
        paint.setStyle(Paint.Style.FILL);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(this.progressColor);
        RectF boundCircle = this.layoutManager.getBoundCircle();
        float sweepAngle = this.layoutManager.getCurrentSweepAngle();
        canvas.drawArc(boundCircle, START_ANGLE, sweepAngle, true, this.paint);
    }

    private void drawTextProgress(Canvas canvas) {
        paint.reset();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(textColor);
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
        String text = progress + "";
        float left = this.getMeasuredWidth() / 2 - this.paint.measureText(text, 0, text.length()) / 2;
        float top = getMeasuredHeight() / 2 - (this.paint.ascent() + this.paint.descent()) / 2;
        canvas.drawText(text + "", left, top, this.paint);
    }
    //endregion

    @Override
    public void onClick(View view) {
        redraw();
    }

    private void redraw() {
        this.layoutManager.reset();
        invalidate();
    }

    public class LayoutManager {

        private static final float ALPHA_MOVE = 50;
        private static final float MIN_DELTA_MOVE = 3;
        private static final float MAX_DELTA_MOVE = 15;
        private RectF boundCircle;
        private float sweepAngle;
        private float currentSweepAngle;
        private float startSweep;

        public LayoutManager() {
            this.boundCircle = new RectF();
            this.sweepAngle = 0;
            this.currentSweepAngle = 0;
        }

        public RectF getBoundCircle() {
            return boundCircle;
        }

        public void measure(int width, int height) {
            this.boundCircle.set(0, 0, width, height);
            this.sweepAngle = ((float) progress / total) * CIRCLE_ANGLE;
            this.startSweep = 0;
        }

        public float getCurrentSweepAngle() {
            return this.currentSweepAngle;
        }

        public void moveToSweepAngle() {
            float dimension = this.sweepAngle - this.currentSweepAngle;
            float distance = Math.abs(dimension);
            float deltaSweep = getDeltaMove();
            deltaSweep = distance > deltaSweep ? deltaSweep : distance;
            if (dimension > 0)
                this.currentSweepAngle += deltaSweep;
            else this.currentSweepAngle -= deltaSweep;
        }

        public boolean isFinishDraw() {
            return sweepAngle == currentSweepAngle;
        }

        public void reset() {
            this.startSweep = 0;
            this.currentSweepAngle = 0;
        }

        public float getDeltaMove() {
            float distanceSweep = Math.abs(sweepAngle - startSweep);
            float currentSweep = Math.abs(currentSweepAngle - startSweep);
            float yMax = distanceSweep * distanceSweep / 2;
            float y = -currentSweep * currentSweep + distanceSweep * currentSweep;
            float deltaMove = (y / yMax) * ALPHA_MOVE;

            deltaMove = deltaMove < MIN_DELTA_MOVE ? MIN_DELTA_MOVE : deltaMove;
            deltaMove = deltaMove > MAX_DELTA_MOVE ? MAX_DELTA_MOVE : deltaMove;
            return deltaMove;
        }
    }
}
