package com.example.billyphan.chart_components.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.billyphan.chart_components.utils.DrawUtils;

/**
 * Created by Billy Phan on 10/17/2017.
 */

public class HorizontalProgressBar extends View implements View.OnClickListener {
    private Paint paint;
    private LayoutManager layoutManager;

    private int backgroundProgressColor;
    private int progressColor;
    private int textColor;

    private float textSize;
    private int total;
    private int value;
    private float connerSize;
    private int oldValue = 0;
    private long mTimeSetProgress = 0;

    public HorizontalProgressBar(Context context) {
        super(context);
        init(null);
    }

    public HorizontalProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public HorizontalProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        this.paint = new Paint();
        this.layoutManager = new LayoutManager();

        this.backgroundProgressColor = Color.GRAY;
        this.textColor = Color.WHITE;
        this.progressColor = Color.RED;
        this.textSize = 40;
        this.connerSize = 20;
        this.value = 8;
        this.total = 10;
        setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.layoutManager.measure(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackgroundProgress(canvas);
        drawProgress(canvas);
        drawTextProgress(canvas);
        if (!layoutManager.isFinishDrawn()) {
            layoutManager.moveToSweepValue();
            invalidate();
        }
    }

    private void drawBackgroundProgress(Canvas canvas) {
        paint.reset();
        paint.setStyle(Paint.Style.FILL);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(this.backgroundProgressColor);
        RectF rect = layoutManager.getBound();
        Path path = DrawUtils.makeCornerRectPath(rect, this.connerSize,
                this.connerSize,
                this.connerSize,
                this.connerSize);
        canvas.drawPath(path, paint);
    }

    private void drawProgress(Canvas canvas) {
        paint.reset();
        paint.setStyle(Paint.Style.FILL);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(this.progressColor);
        RectF rectBackground = layoutManager.getBound();
        RectF rectProgress = layoutManager.getCurrentBoundProgress();
        float rightConner = 0;
        if (rectProgress.right == 0) return;

        if (rectProgress.right == rectBackground.right) {
            rightConner = this.connerSize;
        }
        Path path = DrawUtils.makeCornerRectPath(rectProgress, this.connerSize,
                rightConner,
                rightConner,
                this.connerSize);
        canvas.drawPath(path, paint);
    }

    private void drawTextProgress(Canvas canvas) {
        paint.reset();
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(this.textSize);
        paint.setAntiAlias(true);
        paint.setColor(this.textColor);
        String text = this.value + "";
        float left = getMeasuredWidth() / 2 - paint.measureText(text) / 2;
        float bottom = getMeasuredHeight() / 2 - (paint.descent() + paint.ascent()) / 2;
        canvas.drawText(text, left, bottom, paint);
    }

    @Override
    public void onClick(View view) {
        redraw();
    }

    private void redraw() {
        layoutManager.reset();
        invalidate();
    }

    public void setProgress(int progress) {
        progress = progress > total ? total : progress;
        if (!layoutManager.isFinishDrawn()) {
            if (Math.abs(this.oldValue - progress) < Math.abs(this.value - progress))
                this.oldValue = this.value;
        } else {
            this.oldValue = value;
        }
        this.value = progress;
        layoutManager.measure(getMeasuredWidth(), getMeasuredHeight());
        invalidate();
    }

    class LayoutManager {
        private static final float ALPHA_MOVE = 100;
        private static final float MIN_DELTA_MOVE = 5;
        private static final float MAX_DELTA_MOVE = 30;
        private RectF bound;
        private RectF boundProgress;
        private RectF currentBoundProgress;
        private float startSweep;

        LayoutManager() {
            this.bound = new RectF();
            this.boundProgress = new RectF();
            this.currentBoundProgress = new RectF();
            this.currentBoundProgress.set(0, 0, 0, 0);
        }

        RectF getBound() {
            return bound;
        }

        void measure(int width, int height) {
            bound.set(0, 0, width, height);
            boundProgress.set(0, 0, ((float) value / total) * width, height);
            currentBoundProgress.bottom = height;
            startSweep = ((float) oldValue / total) * width;
        }

        boolean isFinishDrawn() {
            return this.currentBoundProgress.right == this.boundProgress.right;
        }

        void moveToSweepValue() {
            float dimension = this.boundProgress.right - this.currentBoundProgress.right;
            float distance = Math.abs(dimension);
            float deltaMove = getDeltaMove();
            deltaMove = distance > deltaMove ? deltaMove : distance;
            if (dimension > 0)
                this.currentBoundProgress.right += deltaMove;
            else
                this.currentBoundProgress.right -= deltaMove;

        }

        RectF getCurrentBoundProgress() {
            return currentBoundProgress;
        }

        void reset() {
            this.startSweep = 0;
            this.currentBoundProgress.right = 0;
        }

        public float getDeltaMove() {
            float distanceSweep = Math.abs(boundProgress.right - startSweep);
            float currentSweep = Math.abs(currentBoundProgress.right - startSweep);
            float yMax = distanceSweep * distanceSweep / 2;
            float y = -currentSweep * currentSweep + distanceSweep * currentSweep;
            float deltaMove = (y / yMax) * ALPHA_MOVE;

            deltaMove = deltaMove < MIN_DELTA_MOVE ? MIN_DELTA_MOVE : deltaMove;
            deltaMove = deltaMove > MAX_DELTA_MOVE ? MAX_DELTA_MOVE : deltaMove;
            return deltaMove;
        }
    }
}
