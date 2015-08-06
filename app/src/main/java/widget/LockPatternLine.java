package widget;

import android.animation.*;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dupengtao on 14-9-3.
 */
public class LockPatternLine implements
        ValueAnimator.AnimatorUpdateListener {

    private static final String LINE_ALPHA = "lineAlpha";
    private static final String LINE_COLOR = "lineColor";
    private static final String LINE_DISAPPEAR = "lineDisappear";
    private static final int COLOR_NORMAL = 0x80FFFFFF;
    private static final int COLOR_WRONG = 0x80FF0000;
    private static final int strokeWidth = 5;
    private final View mView;
    private final Path mPath2;
    private final Paint mPaint, mPaint2;
    private int lineColor = 0x80FFFFFF;
    private int mMoveTimes;
    private float lineAlpha = 0.4f, lineDisappear = 1.0f;
    private float factorY = 0f, factorX = 0f;
    private AnimatorSet mLineErrorAnimator;
    private LockMovePoint mCurPoint = null;
    private List<LockMovePoint> mMovePoints;


    public LockPatternLine(View view, int lineColor) {
        this.lineColor = lineColor;
        mView = view;
        mPaint = getPaint();
        mPaint.setColor(lineColor);
        mPaint2 = getPaint2();
        mPath2 = new Path();
    }

    public LockPatternLine(View view) {
        mView = view;
        mPaint = getPaint();
        mPaint.setColor(lineColor);
        mPaint2 = getPaint2();
        mPath2 = new Path();
    }

    public void resetLine() {
        cancelLineAnim();
        setLineColor(lineColor);
    }

    public void drawLine(Canvas canvas, Path path) {
        cancelLineAnim();
        canvas.drawPath(path, mPaint);
    }

    private Paint getPaint() {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setDither(true);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeJoin(Paint.Join.ROUND);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeWidth(strokeWidth);
        return p;
    }

    public void doError() {
        cancelLineAnim();
        ObjectAnimator lineColorAnim = getLineColorAnim();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(lineColorAnim);
        animatorSet.start();
        mLineErrorAnimator = animatorSet;
    }

    private ObjectAnimator getLineDisappearAnim(Path path) {
        PropertyValuesHolder pvColor = PropertyValuesHolder.ofFloat(LINE_DISAPPEAR,
                1f, 0f);
        ObjectAnimator colorAnim = ObjectAnimator.ofPropertyValuesHolder(
                this, pvColor).setDuration(175);
        colorAnim.setInterpolator(new LinearInterpolator());
        colorAnim.addUpdateListener(this);
        colorAnim.setAutoCancel(true);
        return colorAnim;
    }


    private void cancelLineAnim() {
        if (mLineErrorAnimator != null && mLineErrorAnimator.isRunning()) {
            mLineErrorAnimator.end();
        }
    }

    public void endAnime() {
        mLineErrorAnimator.end();
    }


    private ObjectAnimator getLineColorAnim() {
        PropertyValuesHolder pvColor = PropertyValuesHolder.ofInt(LINE_COLOR,
                lineColor, COLOR_WRONG);
        ObjectAnimator colorAnim = ObjectAnimator.ofPropertyValuesHolder(
                this, pvColor).setDuration(175);
        colorAnim.setInterpolator(new LinearInterpolator());
        colorAnim.addUpdateListener(this);
        colorAnim.setAutoCancel(true);
        return colorAnim;
    }


    public int getLineColor() {
        return mPaint.getColor();
    }

    public void setLineColor(int lineColor) {
        mPaint.setColor(lineColor);
    }

    public float getLineAlpha() {
        return lineAlpha;
    }

    public void setLineAlpha(float lineAlpha) {
        this.lineAlpha = lineAlpha;
    }

    public float getLineDisappear() {
        return lineDisappear;
    }

    public void setLineDisappear(float lineDisappear) {
        this.lineDisappear = lineDisappear;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mView.invalidate();
    }

    public void doNewError(ArrayList<LockMovePoint> mLockMovePoints, LineAnimListener listener) {
        if (mLockMovePoints.size() == 0) {
            return;
        }
        ObjectAnimator moveAnim = getMoveAnim(mLockMovePoints, listener);
        moveAnim.start();
        mPaint.setAlpha(0);
    }

    private Paint getPaint2() {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setDither(true);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeJoin(Paint.Join.ROUND);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeWidth(5);
        p.setColor(COLOR_WRONG);
        return p;
    }

    public void doLineErrorDraw(Canvas canvas) {

        if (mCurPoint != null) {
            mPath2.rewind();
            setError(mMovePoints, mMoveTimes + 1);
            float curX = mCurPoint.getCurX();
            float curY = mCurPoint.getCurY();
            float moveX = mCurPoint.getMoveX();
            float moveY = mCurPoint.getMoveY();
            mPath2.moveTo(curX == moveX ? moveX : curX + ((moveX - curX) * factorX), curY == moveY ? moveY : curY + ((moveY - curY) * factorY));
            mPath2.lineTo(moveX, moveY);
            canvas.drawPath(mPath2, mPaint2);
        } else {
            canvas.drawPath(mPath2, mPaint2);
        }
    }

    public ObjectAnimator getMoveAnim(List<LockMovePoint> points, final LineAnimListener listener) {
        mMovePoints = points;
        mMoveTimes = 0;
        PropertyValuesHolder pvMoveY = PropertyValuesHolder.ofFloat("factorY",
                0f, 1f);
        PropertyValuesHolder pvMoveX = PropertyValuesHolder.ofFloat("factorX",
                0f, 1f);
        ObjectAnimator moveAnim = ObjectAnimator.ofPropertyValuesHolder(
                this, pvMoveY, pvMoveX).setDuration(LockPatternHelper.DISAPPEAR_TIME);
        moveAnim.setRepeatMode(ValueAnimator.RESTART);
        moveAnim.setRepeatCount(points.size() - 1);
        moveAnim.addUpdateListener(this);
        moveAnim.setAutoCancel(true);
        moveAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mCurPoint = mMovePoints.get(mMoveTimes);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                mMoveTimes++;
                if (listener != null) {
                    listener.onErrorLineAnim(mCurPoint.getMoveX(), mCurPoint.getMoveY(), mMoveTimes);
                }
                try {
                    if (mMovePoints.size() != 0) {
                        mCurPoint = mMovePoints.get(mMoveTimes);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return moveAnim;
    }

    public float getFactorY() {
        return factorY;
    }

    public void setFactorY(float factorY) {
        this.factorY = factorY;
    }

    public float getFactorX() {
        return factorX;
    }

    public void setFactorX(float factorX) {
        this.factorX = factorX;
    }

    public void setError(List<LockMovePoint> points, int num) {
        //MovePoint point = points.get(0);
        //mPath.moveTo(point.getCurX(),point.getCurY());
        //mPath.lineTo(point.getMoveX(),point.getMoveY());
        for (int i = num, j = points.size(); i < j; i++) {
            LockMovePoint p = points.get(i);
            mPath2.moveTo(p.getCurX(), p.getCurY());
            mPath2.lineTo(p.getMoveX(), p.getMoveY());
        }
    }


    public interface LineAnimListener {
        void onErrorLineAnim(float x, float y, int times);
    }


}
